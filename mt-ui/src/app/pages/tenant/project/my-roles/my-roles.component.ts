import { Component, OnDestroy } from '@angular/core';
import { MatBottomSheet } from '@angular/material/bottom-sheet';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute, Router } from '@angular/router';
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
    public entitySvc: MyRoleService,
    public authSvc: AuthService,
    public httpProxySvc: HttpProxyService,
    public projectSvc: ProjectService,
    public deviceSvc: DeviceService,
    public httpSvc: HttpProxyService,
    public fis: FormInfoService,
    public bottomSheet: MatBottomSheet,
    public route: ActivatedRoute,
    private router: Router,
    public dialog: MatDialog,
  ) {
    super(route, projectSvc, httpSvc, entitySvc, deviceSvc, bottomSheet, fis, 2);
    const sub2 = this.canDo('VIEW_ROLE').subscribe(b => {
      if (b.result) {
        this.doSearch({ value: 'types:USER', resetPage: true })
      }
    });
    const sub = combineLatest([this.projectId, this.canDo('EDIT_ROLE')]).subscribe(next => {
      this.entitySvc.setProjectId(next[0]);
      this.params['projectId'] = next[0];
      const temp = next[1].result ? {
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
        const data = <IDomainContext<IRole>>{ context: 'new', from: next, params: this.params }
        this.router.navigate(['home', 'role-detail'], { state: data })
      }
    })
  }
  editRole(id: string) {
    this.entitySvc.readById(id).subscribe(next => {
      const data = <IDomainContext<IRole>>{ context: 'edit', from: next, params: this.params }
      this.router.navigate(['home', 'role-detail'], { state: data })
    })
  }
}