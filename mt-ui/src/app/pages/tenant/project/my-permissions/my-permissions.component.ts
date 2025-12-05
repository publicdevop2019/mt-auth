import { Component } from '@angular/core';
import { HttpProxyService } from 'src/app/services/http-proxy.service';
import { ProjectService } from 'src/app/services/project.service';
import { IEndpoint, IPermission, IProtectedEndpoint, IQueryProvider } from 'src/app/misc/interface';
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
  private epUrl = Utility.getProjectResource(this.projectId, RESOURCE_NAME.ENDPOINTS_PROTECTED)
  public changeId = Utility.getChangeId();
  public allowError = false;
  public nameErrorMsg: string = undefined;
  fg = new FormGroup({
    name: new FormControl(''),
    description: new FormControl(''),
    projectId: new FormControl(''),
    parentId: new FormControl(''),
    apiId: new FormControl([]),
  });
  private initialColumnList: any = {
    id: 'ID',
    name: 'PERM_NAME',
    description: 'DESCRIPTION',
    linkedApi: 'LINKED_API',
    delete: 'DELETE',
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
    this.deviceSvc.updateDocTitle('TENANT_PERM_DOC_TITLE')
    this.permissionHelper.canDo(this.projectId, httpSvc.currentUserAuthInfo.permissionIds, 'PERMISSION_MGMT').pipe(take(1)).subscribe(b => {
      if (b.result) {
        this.tableSource.loadPage(0)
      }
    })
    this.fg.valueChanges.subscribe(() => {
      this.changeId = Utility.getChangeId();
      if (this.allowError) {
        this.validateCreateForm()
      }
    })
  }
  getEndpoints(): IQueryProvider {
    return {
      readByQuery: (num: number, size: number, query?: string, by?: string, order?: string, header?: {}) => {
        return this.httpSvc.readEntityByQuery<IProtectedEndpoint>(this.epUrl, num, size, query, by, order, header)
      }
    } as IQueryProvider
  }
  convertToPayload(): IPermission {
    return {
      id: '',//value is ignored
      name: this.fg.get('name').value,
      description: this.fg.get('description').value ? this.fg.get('description').value : null,
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
  removeFirst(input: string[]) {
    return input.filter((e, i) => i !== 0);
  }
}