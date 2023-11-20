import { ChangeDetectorRef, Component, OnDestroy } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { IOption, IQueryProvider } from 'mt-form-builder/lib/classes/template.interface';
import { combineLatest, Observable, Subscription } from 'rxjs';
import { IDomainContext } from 'src/app/clazz/summary.component';
import { EndpointService } from 'src/app/services/endpoint.service';
import { HttpProxyService } from 'src/app/services/http-proxy.service';
import { MyPermissionService } from 'src/app/services/my-permission.service';
import { MyRoleService } from 'src/app/services/my-role.service';
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
  public data: IDomainContext<IRole>
  aggregate: IRole;
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
  constructor(
    public roleSvc: MyRoleService,
    public epSvc: EndpointService,
    public permissoinSvc: MyPermissionService,
    public httpProxySvc: HttpProxyService,
    public sharedPermSvc: SharedPermissionService,
    public cdr: ChangeDetectorRef,
    public router: RouterWrapperService,
    public dialog: MatDialog,
    public deviceSvc: DeviceService,
  ) {
    this.data = this.router.getData().extras.state as IDomainContext<IRole>
    if (this.data === undefined) {
      this.router.navProjectHome()
    }
    this.aggregate = this.data.from;
    this.permissoinSvc.setProjectId(this.data.params['projectId'])
    this.sharedPermSvc.setProjectId(this.data.params['projectId'])
    this.roleSvc.setProjectId(this.data.params['projectId'])
    this.dataSource = new MatTableDataSource([])
    if (this.data.context === 'new') {
      this.fg.get('name').setValue(this.data.from.name)
      this.fg.get('description').setValue(this.data.from.description)
    }
    this.fg.valueChanges.subscribe(next => {
      if (this.allowError) {
        this.validateForm()
      }
    })
    this.reusme();
    const sub2 = this.deviceSvc.refreshSummary.subscribe(() => {
      this.roleSvc.readById(this.data.from.id).subscribe(next => {
        Logger.debug('reload view')
        this.data.from = next;
        this.aggregate = next;
        this.dataSource = new MatTableDataSource(this.aggregate.permissionDetails || []);
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
        return this.httpProxySvc.readEntityByQuery<IRole>(this.roleSvc.entityRepo, num, size, `types:PROJECT.USER`, by, order, header)
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
        return this.httpProxySvc.readEntityByQuery<IRole>(this.permissoinSvc.entityRepo, num, size, "types:COMMON", by, order, header)
      }
    } as IQueryProvider
  }
  getApiPermissions(): IQueryProvider {
    return {
      readByQuery: (num: number, size: number, query?: string, by?: string, order?: string, header?: {}) => {
        return this.httpProxySvc.readEntityByQuery<IRole>(this.permissoinSvc.entityRepo, num, size, "types:API", by, order, header)
      }
    } as IQueryProvider
  }
  reusme(): void {
    if (this.data.context === 'edit') {
      this.dataSource = new MatTableDataSource(this.aggregate.permissionDetails || []);
      Logger.debug(this.aggregate.permissionDetails)
      if (this.aggregate.parentId || (this.aggregate.externalPermissionIds && this.aggregate.externalPermissionIds.length > 0)) {
        let var0: Observable<any>[] = [];
        if (this.aggregate.parentId) {
          var0.push(this.roleSvc.readEntityByQuery(0, 1, 'id:' + this.aggregate.parentId))
        }
        if ((this.aggregate.externalPermissionIds && this.aggregate.externalPermissionIds.length > 0)) {
          var0.push(this.sharedPermSvc.readEntityByQuery(0, 1, 'id:' + this.aggregate.externalPermissionIds.join('.')))
        }
        combineLatest(var0).subscribe(next => {
          if (this.aggregate.parentId) {
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
  }
  resumeForm() {
    this.fg.get('id').setValue(this.aggregate.id)
    this.fg.get('name').setValue(this.aggregate.name)
    this.fg.get('parentId').setValue(this.aggregate.parentId)
    this.fg.get('description').setValue(this.aggregate.description ? this.aggregate.description : '')
    this.fg.get('sharedApi').setValue(this.aggregate.externalPermissionIds);
    this.fg.get('apiPermissionIds').setValue(this.aggregate.apiPermissionIds);
    this.fg.get('commonPermissionIds').setValue(this.aggregate.commonPermissionIds);
  }
  convertToUpdateBasicPayload(): any {
    const formGroup = this.fg;
    let type = ''
    type = 'BASIC'
    return {
      type: type,
      name: this.aggregate.systemCreate ? this.aggregate.originalName : formGroup.get('name').value,
      parentId: formGroup.get('parentId').value || null,
      description: formGroup.get('description').value ? formGroup.get('description').value : null,
      version: this.aggregate && this.aggregate.version
    }
  }
  convertToUpdatePermissionPayload(sourceType: 'COMMON_PERMISSIONS' | 'API_PERMISSIONS' | 'EXTERNAL_PERMISSION',
    ids: string[], isAdd: boolean): any {
    let type = ''
    if (sourceType === 'API_PERMISSIONS') {
      type = 'API_PERMISSION'
      return {
        type: type,
        apiPermissionIds: isAdd ? [...this.data.from.apiPermissionIds, ...ids]
          : this.data.from.apiPermissionIds.filter(e => !ids.includes(e)),
        externalPermissionIds: this.data.from.externalPermissionIds,
        version: this.aggregate && this.aggregate.version
      }
    } else if (sourceType === 'EXTERNAL_PERMISSION') {
      type = 'API_PERMISSION'
      return {
        type: type,
        apiPermissionIds: this.data.from.apiPermissionIds,
        externalPermissionIds: isAdd ? [...this.data.from.externalPermissionIds, ...ids]
          : this.data.from.externalPermissionIds.filter(e => !ids.includes(e)),
        version: this.aggregate && this.aggregate.version
      }
    } else if (sourceType === 'COMMON_PERMISSIONS') {
      type = 'COMMON_PERMISSION'
      return {
        type: type,
        commonPermissionIds: isAdd ? [...this.data.from.commonPermissionIds, ...ids]
          : this.data.from.commonPermissionIds.filter(e => !ids.includes(e)),
        version: this.aggregate && this.aggregate.version
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
      this.roleSvc.update(this.aggregate.id, this.convertToUpdateBasicPayload(), this.changeId)
    }
  }
  addPermission() {
    const dialogRef = this.dialog.open(AddPermissionDialogComponent, { data: {} });
    dialogRef.afterClosed().subscribe(next => {
      if (next !== undefined) {
        this.roleSvc.update(this.aggregate.id, this.convertToUpdatePermissionPayload(next.type, next.permIds, true), this.changeId)
      }
    })
  }
  displayedColumns() {
    return ['name', 'type', 'delete']
  };
  removePerm(row: IPermTable) {
    if (row.type === 'COMMON') {
      this.roleSvc.update(this.aggregate.id, this.convertToUpdatePermissionPayload('COMMON_PERMISSIONS', [row.id], false), this.changeId)
    } else if (row.type === 'API') {
      this.roleSvc.update(this.aggregate.id, this.convertToUpdatePermissionPayload('API_PERMISSIONS', [row.id], false), this.changeId)
    } else if (row.type === 'SHARED') {
      this.roleSvc.update(this.aggregate.id, this.convertToUpdatePermissionPayload('EXTERNAL_PERMISSION', [row.id], false), this.changeId)
    }
  }
}
