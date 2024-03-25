import { Component } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { RoleComponent } from 'src/app/pages/tenant/project/role/role.component';
import { RoleCreateDialogComponent } from 'src/app/components/role-create-dialog/role-create-dialog.component';
import { HttpProxyService } from 'src/app/services/http-proxy.service';
import { ProjectService } from 'src/app/services/project.service';
import { IIdBasedEntity, IRoleLinkedPermission } from 'src/app/misc/interface';
import { Utility } from 'src/app/misc/utility';
import { RouterWrapperService } from 'src/app/services/router-wrapper';
import { RESOURCE_NAME } from 'src/app/misc/constant';
import { PermissionHelper } from 'src/app/clazz/permission-helper';
import { TableHelper } from 'src/app/clazz/table-helper';
import { take } from 'rxjs/operators';
import { DeviceService } from 'src/app/services/device.service';
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
export class MyRolesComponent {
  columnList: any = {
    name: 'NAME',
    description: 'DESCRIPTION',
  }
  public projectId = this.route.getProjectIdFromUrl()
  private url = Utility.getProjectResource(this.projectId, RESOURCE_NAME.ROLES)
  public tableSource: TableHelper<IRole> = new TableHelper(this.columnList, 10, this.httpSvc, this.url, 'types:USER');
  public permissionHelper: PermissionHelper = new PermissionHelper(this.projectSvc.permissionDetail)
  constructor(
    public httpProxySvc: HttpProxyService,
    public projectSvc: ProjectService,
    public httpSvc: HttpProxyService,
    public route: RouterWrapperService,
    public dialog: MatDialog,
    public deviceSvc: DeviceService,
  ) {
    this.permissionHelper.canDo(this.projectId, httpSvc.currentUserAuthInfo.permissionIds, 'VIEW_ROLE').pipe(take(1)).subscribe(b => {
      if (b.result) {
        this.tableSource.loadPage(0)
      }
    })
    this.permissionHelper.canDo(this.projectId, httpSvc.currentUserAuthInfo.permissionIds, 'EDIT_ROLE').pipe(take(1)).subscribe(b => {
      this.columnList = b.result ? {
        name: 'NAME',
        description: 'DESCRIPTION',
        edit: 'EDIT',
        delete: 'DELETE',
      } : {
        name: 'NAME',
        description: 'DESCRIPTION',
      }
      this.tableSource.columnConfig = this.columnList;
    })
  }
  editable(row: IRole) {
    return row.roleType !== 'CLIENT_ROOT' && row.roleType !== 'PROJECT'
  }
  createNewRole() {
    const dialogRef = this.dialog.open(RoleCreateDialogComponent, { data: {} });
    dialogRef.afterClosed().subscribe(next => {
      if (next !== undefined) {
        this.httpProxySvc.createEntity(this.url, this.convertToPayload(next.name, next.description), Utility.getChangeId()
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
      projectId: this.projectId,
      commonPermissionIds: [],
      apiPermissionIds: [],
      externalPermissionIds: [],
      version: 0
    }
  }
  public doDelete(id: string) {
    this.httpSvc.deleteEntityById(this.url, id, Utility.getChangeId()).subscribe(() => {
      this.deviceSvc.notify(true)
      this.tableSource.refresh()
    }, () => {
      this.deviceSvc.notify(false)
    })
  }
}