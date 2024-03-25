import { Component } from '@angular/core';
import { HttpProxyService } from 'src/app/services/http-proxy.service';
import { ProjectService } from 'src/app/services/project.service';
import { IEndpoint, IPermission, IQueryProvider } from 'src/app/misc/interface';
import { FormGroup, FormControl } from '@angular/forms';
import { Validator } from 'src/app/misc/validator';
import { Utility } from 'src/app/misc/utility';
import { RouterWrapperService } from 'src/app/services/router-wrapper';
import { RESOURCE_NAME } from 'src/app/misc/constant';
import { TableHelper } from 'src/app/clazz/table-helper';
import { PermissionHelper } from 'src/app/clazz/permission-helper';
import { DeviceService } from 'src/app/services/device.service';
import { take } from 'rxjs/operators';

@Component({
  selector: 'app-my-permissions',
  templateUrl: './my-permissions.component.html',
  styleUrls: ['./my-permissions.component.css']
})
export class MyPermissionsComponent {
  public projectId = this.route.getProjectIdFromUrl()
  private url = Utility.getProjectResource(this.projectId, RESOURCE_NAME.PERMISSIONS)
  private epUrl = Utility.getProjectResource(this.projectId, RESOURCE_NAME.ENDPOINTS_ROLE)
  public changeId = Utility.getChangeId();
  public allowError = false;
  public nameErrorMsg: string = undefined;
  fg = new FormGroup({
    name: new FormControl(''),
    projectId: new FormControl(''),
    parentId: new FormControl(''),
    apiId: new FormControl([]),
  });
  private initialColumnList: any = {
    id: 'ID',
    name: 'PERM_NAME',
    type: 'TYPE',
  };
  parentIdOption = [];
  apiOptions = [];
  public tableSource: TableHelper<IPermission> = new TableHelper(this.initialColumnList, 10, this.httpSvc, this.url, 'types:COMMON');
  public permissionHelper: PermissionHelper = new PermissionHelper(this.projectSvc.permissionDetail)
  constructor(
    public projectSvc: ProjectService,
    public httpSvc: HttpProxyService,
    public deviceSvc: DeviceService,
    public route: RouterWrapperService,
  ) {
    this.permissionHelper.canDo(this.projectId, httpSvc.currentUserAuthInfo.permissionIds, 'VIEW_PERMISSION').pipe(take(1)).subscribe(b => {
      if (b.result) {
        this.tableSource.loadPage(0)
      }
    })
    this.permissionHelper.canDo(this.projectId, httpSvc.currentUserAuthInfo.permissionIds, 'EDIT_PERMISSION').pipe(take(1)).subscribe(b => {
      this.tableSource.columnConfig = b.result ? {
        id: 'ID',
        name: 'PERM_NAME',
        type: 'TYPE',
        delete: 'DELETE',
      } : {
        id: 'ID',
        name: 'PERM_NAME',
        type: 'TYPE',
      }
    })
    this.fg.valueChanges.subscribe(() => {
      if (this.allowError) {
        this.validateCreateForm()
      }
    })
  }
  getParentPerm(): IQueryProvider {
    return {
      readByQuery: (num: number, size: number, query?: string, by?: string, order?: string, header?: {}) => {
        return this.httpSvc.readEntityByQuery<IPermission>(this.url, num, size, `types:COMMON`, by, order, header)
      }
    } as IQueryProvider
  }
  getEndpoints(): IQueryProvider {
    return {
      readByQuery: (num: number, size: number, query?: string, by?: string, order?: string, header?: {}) => {
        return this.httpSvc.readEntityByQuery<IEndpoint>(this.epUrl, num, size, query, by, order, header)
      }
    } as IQueryProvider
  }
  convertToPayload(): IPermission {
    return {
      id: '',//value is ignored
      parentId: this.fg.get('parentId').value ? this.fg.get('parentId').value : null,
      name: this.fg.get('name').value,
      projectId: this.projectId,
      linkedApiIds: this.fg.get('apiId').value || [],
      version: 0
    }
  }
  create() {
    this.allowError = true
    if (this.validateCreateForm()) {
      this.httpSvc.createEntity(this.url, this.convertToPayload(), this.changeId).subscribe(() => {
        this.deviceSvc.notify(true)
        this.tableSource.refresh()
      }, () => {
        this.deviceSvc.notify(false)
      })
    }
  }

  private validateCreateForm() {
    const var0 = Validator.exist(this.fg.get('name').value)
    this.nameErrorMsg = var0.errorMsg
    return !var0.errorMsg
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