import { Component, OnDestroy } from '@angular/core';
import { MatBottomSheet } from '@angular/material/bottom-sheet';
import { MatDialog } from '@angular/material/dialog';
import { FormInfoService } from 'mt-form-builder';
import { combineLatest } from 'rxjs';
import { IDomainContext, IIdBasedEntity } from 'src/app/clazz/summary.component';
import { TenantSummaryEntityComponent } from 'src/app/clazz/tenant-summary.component';
import { ISearchConfig, ISearchEvent } from 'src/app/components/search/search.component';
import { RoleComponent } from 'src/app/pages/tenant/project/role/role.component';
import { RoleCreateDialogComponent } from 'src/app/components/role-create-dialog/role-create-dialog.component';
import { AuthService } from 'src/app/services/auth.service';
import { DeviceService } from 'src/app/services/device.service';
import { HttpProxyService } from 'src/app/services/http-proxy.service';
import { MyRoleService } from 'src/app/services/my-role.service';
import { ProjectService } from 'src/app/services/project.service';
import { IRoleLinkedPermission } from 'src/app/misc/interface';
import { Utility } from 'src/app/misc/utility';
import { RouterWrapperService } from 'src/app/services/router-wrapper';
import { ActivatedRoute } from '@angular/router';
export interface IRole extends IIdBasedEntity {
  name: string,
  originalName?: string,
  parentId?: string,
  tenantId?: string,
  systemCreate?: boolean,
  projectId: string,
  roleType?: 'USER' | 'CLIENT' | 'PROJECT' | 'CLIENT_ROOT',
  apiPermissionIds: string[],
  permissionDetails?: { id: string, name: string, type: string }[],
  commonPermissionIds: string[],
  externalPermissionIds?: string[],
  permissions?: IRoleLinkedPermission[],
  description?: string
}
@Component({
  selector: 'app-my-roles',
  templateUrl: './my-roles.component.html',
  styleUrls: ['./my-roles.component.css']
})
export class MyRolesComponent extends TenantSummaryEntityComponent<IRole, IRole> implements OnDestroy {
  columnList: any = {};
  params: any = {};
  sheetComponent = RoleComponent;
  searchConfigs: ISearchConfig[] = [
    {
      searchLabel: 'ID',
      searchValue: 'id',
      type: 'text',
      multiple: {
        delimiter: '.'
      }
    },
  ]
  constructor(
    public roleSvc: MyRoleService,
    public authSvc: AuthService,
    public httpProxySvc: HttpProxyService,
    public projectSvc: ProjectService,
    public deviceSvc: DeviceService,
    public httpSvc: HttpProxyService,
    public fis: FormInfoService,
    public bottomSheet: MatBottomSheet,
    public router: ActivatedRoute,
    public route: RouterWrapperService,
    public dialog: MatDialog,
  ) {
    super(router, route, projectSvc, httpSvc, roleSvc, bottomSheet, fis);
    const sub2 = this.canDo('VIEW_ROLE').subscribe(b => {
      if (b.result) {
        this.doSearch({ value: 'types:USER', resetPage: true })
      }
    });
    const sub3 = this.deviceSvc.refreshSummary.subscribe(() => {
      this.doSearch({ value: 'types:USER', resetPage: true })
    })
    this.roleSvc.setProjectId(this.route.getProjectIdFromUrl());
    this.params['projectId'] = this.route.getProjectIdFromUrl();
    const sub = combineLatest([this.canDo('EDIT_ROLE')]).subscribe(next => {
      const temp = next[0].result ? {
        name: 'NAME',
        description: 'DESCRIPTION',
        edit: 'EDIT',
        delete: 'DELETE',
      } : {
        name: 'NAME',
        description: 'DESCRIPTION',
      }
      this.columnList = temp;
      this.initTableSetting();
    })
    this.subs.add(sub2)
    this.subs.add(sub)
    this.subs.add(sub3)
  }
  ngOnDestroy(): void {
    this.fis.reset(this.formId)
    super.ngOnDestroy()
  }
  editable(row: IRole) {
    return row.roleType !== 'CLIENT_ROOT' && row.roleType !== 'PROJECT'
  }
  doSearchWrapperCommon(config: ISearchEvent) {
    config.value = "types:USER"
    this.doSearch(config)
  }
  createNewRole() {
    const dialogRef = this.dialog.open(RoleCreateDialogComponent, { data: {} });
    dialogRef.afterClosed().subscribe(next => {
      if (next !== undefined) {
        this.httpProxySvc.createEntity(this.roleSvc.entityRepo, this.convertToPayload(next.name, next.description), Utility.getChangeId()
        ).subscribe(id => {
          this.editRole(id)
        });
      }
    })
  }
  editRole(id: string) {
    this.route.navProjectRolesDetail(id)
  }
  convertToPayload(name: string, description: string): IRole {
    return {
      id: '',
      name: name,
      description: Utility.hasValue(description) ? description : undefined,
      projectId: this.roleSvc.getProjectId(),
      commonPermissionIds: [],
      apiPermissionIds: [],
      externalPermissionIds: [],
      version: 0
    }
  }
}