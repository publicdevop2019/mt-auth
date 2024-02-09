import { ChangeDetectorRef, Component, OnDestroy } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { IOption, IQueryProvider } from 'mt-form-builder/lib/classes/template.interface';
import { combineLatest, Observable, Subscription } from 'rxjs';
import { EndpointService } from 'src/app/services/endpoint.service';
import { HttpProxyService } from 'src/app/services/http-proxy.service';
import { SharedPermissionService } from 'src/app/services/shared-permission.service';
import { IRole } from '../my-roles/my-roles.component';
import { Validator } from 'src/app/misc/validator';
import { Utility } from 'src/app/misc/utility';
import { MatTableDataSource } from '@angular/material/table';
import { Logger } from 'src/app/misc/logger';
import { MatDialog } from '@angular/material/dialog';
import { AddPermissionDialogComponent } from 'src/app/components/add-permission-dialog/add-permission-dialog.component';
import { DeviceService } from 'src/app/services/device.service';
import { RouterWrapperService } from 'src/app/services/router-wrapper';
import { RESOURCE_NAME } from 'src/app/misc/constant';
import { BannerService } from 'src/app/services/banner.service';
interface IPermTable {
  id: string,
  name: string, type: string
}
@Component({
  selector: 'app-role',
  templateUrl: './role.component.html',
  styleUrls: ['./role.component.css']
})
export class RoleComponent implements OnDestroy {
  public projectId = this.router.getProjectIdFromUrl()
  private roleUrl = Utility.getProjectResource(this.projectId, RESOURCE_NAME.ROLES)
  fg = new FormGroup({
    id: new FormControl({ value: '', disabled: true }),
    name: new FormControl(''),
    description: new FormControl(''),
    parentId: new FormControl(''),
    commonPermissionIds: new FormControl({ value: [], disabled: false }),
    apiPermissionIds: new FormControl({ value: [], disabled: false }),
    sharedApi: new FormControl({ value: [], disabled: false }),
  });
  public allowError = false;
  public changeId = Utility.getChangeId();
  public nameErrorMsg: string;
  public data: IRole
  parentOptions: IOption[] = []
  commonOptions: IOption[] = []
  apiOptions: IOption[] = []
  sharedApiOptions: IOption[] = []
  dataSource: MatTableDataSource<IPermTable>;
  columnList = {
    name: 'NAME',
    type: 'TYPE',
    delete: 'DELETE',
  };
  subs: Subscription = new Subscription()
  private permissionUrl = Utility.getProjectResource(this.projectId, RESOURCE_NAME.PERMISSIONS)
  constructor(
    public epSvc: EndpointService,
    public httpProxySvc: HttpProxyService,
    public sharedPermSvc: SharedPermissionService,
    public cdr: ChangeDetectorRef,
    public router: RouterWrapperService,
    public dialog: MatDialog,
    public banner: BannerService,
    public deviceSvc: DeviceService,
  ) {
    const roleId = this.router.getRoleIdFromUrl()
    this.sharedPermSvc.setProjectId(this.router.getProjectIdFromUrl())

    this.httpProxySvc.readEntityById<IRole>(this.roleUrl, roleId).subscribe(next => {
      this.data = next
      this.fg.get('name').setValue(this.data.name)
      this.fg.get('description').setValue(this.data.description)
      this.reusme();
    })
    this.dataSource = new MatTableDataSource([])
    this.fg.valueChanges.subscribe(next => {
      if (this.allowError) {
        this.validateForm()
      }
    })
    const sub2 = this.deviceSvc.refreshSummary.subscribe(() => {
      this.httpProxySvc.readEntityById<IRole>(this.roleUrl, this.data.id).subscribe(next => {
        Logger.debug('reload view')
        this.data = next;
        this.dataSource = new MatTableDataSource(this.data.permissionDetails || []);
      })
    })
    this.subs.add(sub2)
  }
  ngOnDestroy(): void {
    //prevent memory leak
    this.subs.unsubscribe()
  }
  getParents(): IQueryProvider {
    return {
      readByQuery: (num: number, size: number, query?: string, by?: string, order?: string, header?: {}) => {
        return this.httpProxySvc.readEntityByQuery<IRole>(this.roleUrl, num, size, `types:PROJECT.USER`, by, order, header)
      }
    } as IQueryProvider
  }
  getShared(): IQueryProvider {
    return {
      readByQuery: (num: number, size: number, query?: string, by?: string, order?: string, header?: {}) => {
        return this.httpProxySvc.readEntityByQuery<IRole>(this.sharedPermSvc.entityRepo, num, size, undefined, by, order, header)
      }
    } as IQueryProvider
  }
  getCommonPermissions(): IQueryProvider {
    return {
      readByQuery: (num: number, size: number, query?: string, by?: string, order?: string, header?: {}) => {
        return this.httpProxySvc.readEntityByQuery<IRole>(this.permissionUrl, num, size, "types:COMMON", by, order, header)
      }
    } as IQueryProvider
  }
  getApiPermissions(): IQueryProvider {
    return {
      readByQuery: (num: number, size: number, query?: string, by?: string, order?: string, header?: {}) => {
        return this.httpProxySvc.readEntityByQuery<IRole>(this.permissionUrl, num, size, "types:API", by, order, header)
      }
    } as IQueryProvider
  }
  reusme(): void {
    this.dataSource = new MatTableDataSource(this.data.permissionDetails || []);
    if (this.data.parentId || (this.data.externalPermissionIds && this.data.externalPermissionIds.length > 0)) {
      let var0: Observable<any>[] = [];
      if (this.data.parentId) {
        var0.push(this.httpProxySvc.readEntityByQuery(this.roleUrl, 0, 1, 'id:' + this.data.parentId))
      }
      if ((this.data.externalPermissionIds && this.data.externalPermissionIds.length > 0)) {
        var0.push(this.sharedPermSvc.readEntityByQuery(0, 1, 'id:' + this.data.externalPermissionIds.join('.')))
      }
      combineLatest(var0).subscribe(next => {
        if (this.data.parentId) {
          this.parentOptions = next[0].data.map(e => <IOption>{ label: e.name, value: e.id });
          if (next.length > 1) {
            this.sharedApiOptions = next[1].data.map(e => <IOption>{ label: e.name, value: e.id })
          }
        } else {
          this.sharedApiOptions = next[1].data.map(e => <IOption>{ label: e.name, value: e.id })
        }
        this.resumeForm()
      })
    } else {
      this.resumeForm()
    }
  }
  resumeForm() {
    this.fg.get('id').setValue(this.data.id)
    this.fg.get('name').setValue(this.data.name)
    this.fg.get('parentId').setValue(this.data.parentId)
    this.fg.get('description').setValue(this.data.description ? this.data.description : '')
    this.fg.get('sharedApi').setValue(this.data.externalPermissionIds);
    this.fg.get('apiPermissionIds').setValue(this.data.apiPermissionIds);
    this.fg.get('commonPermissionIds').setValue(this.data.commonPermissionIds);
  }
  convertToUpdateBasicPayload(): any {
    const formGroup = this.fg;
    let type = ''
    type = 'BASIC'
    return {
      type: type,
      name: this.data.systemCreate ? this.data.originalName : formGroup.get('name').value,
      parentId: formGroup.get('parentId').value || null,
      description: formGroup.get('description').value ? formGroup.get('description').value : null,
      version: this.data && this.data.version
    }
  }
  convertToUpdatePermissionPayload(sourceType: 'COMMON_PERMISSIONS' | 'API_PERMISSIONS' | 'EXTERNAL_PERMISSION',
    ids: string[], isAdd: boolean): any {
    let type = ''
    if (sourceType === 'API_PERMISSIONS') {
      type = 'API_PERMISSION'
      return {
        type: type,
        apiPermissionIds: isAdd ? [...this.data.apiPermissionIds, ...ids]
          : this.data.apiPermissionIds.filter(e => !ids.includes(e)),
        externalPermissionIds: this.data.externalPermissionIds,
        version: this.data && this.data.version
      }
    } else if (sourceType === 'EXTERNAL_PERMISSION') {
      type = 'API_PERMISSION'
      return {
        type: type,
        apiPermissionIds: this.data.apiPermissionIds,
        externalPermissionIds: isAdd ? [...this.data.externalPermissionIds, ...ids]
          : this.data.externalPermissionIds.filter(e => !ids.includes(e)),
        version: this.data && this.data.version
      }
    } else if (sourceType === 'COMMON_PERMISSIONS') {
      type = 'COMMON_PERMISSION'
      return {
        type: type,
        commonPermissionIds: isAdd ? [...this.data.commonPermissionIds, ...ids]
          : this.data.commonPermissionIds.filter(e => !ids.includes(e)),
        version: this.data && this.data.version
      }
    }
  }
  private validateForm() {
    const var0 = Validator.exist(this.fg.get('name').value)
    this.nameErrorMsg = var0.errorMsg
    return !var0.errorMsg
  }
  update() {
    this.allowError = true;
    if (this.validateForm()) {
      this.httpProxySvc.updateEntity(this.roleUrl, this.data.id, this.convertToUpdateBasicPayload(), this.changeId).subscribe(next => {
        this.banner.notify(next)
      })
    }
  }
  addPermission() {
    const dialogRef = this.dialog.open(AddPermissionDialogComponent, { data: {} });
    dialogRef.afterClosed().subscribe(next => {
      if (next !== undefined) {
        this.httpProxySvc.updateEntity(this.roleUrl, this.data.id, this.convertToUpdatePermissionPayload(next.type, next.permIds, true), this.changeId)
      }
    })
  }
  displayedColumns() {
    return ['name', 'type', 'delete']
  };
  removePerm(row: IPermTable) {
    if (row.type === 'COMMON') {
      this.httpProxySvc.updateEntity(this.roleUrl, this.data.id, this.convertToUpdatePermissionPayload('COMMON_PERMISSIONS', [row.id], false), this.changeId)
    } else if (row.type === 'API') {
      this.httpProxySvc.updateEntity(this.roleUrl, this.data.id, this.convertToUpdatePermissionPayload('API_PERMISSIONS', [row.id], false), this.changeId)
    } else if (row.type === 'SHARED') {
      this.httpProxySvc.updateEntity(this.roleUrl, this.data.id, this.convertToUpdatePermissionPayload('EXTERNAL_PERMISSION', [row.id], false), this.changeId)
    }
  }
}
