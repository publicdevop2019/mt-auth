import { Component } from '@angular/core';
import { FormGroup, FormControl } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { IRole } from 'src/app/pages/tenant/project/my-roles/my-roles.component';
import { HttpProxyService } from 'src/app/services/http-proxy.service';
import { RouterWrapperService } from 'src/app/services/router-wrapper';
import { RESOURCE_NAME } from 'src/app/misc/constant';
import { Utility } from 'src/app/misc/utility';
import { IOption, IQueryProvider } from 'src/app/misc/interface';

@Component({
  selector: 'app-add-permission-dialog',
  templateUrl: './add-permission-dialog.component.html',
  styleUrls: ['./add-permission-dialog.component.css']
})
export class AddPermissionDialogComponent{
  public projectId = this.route.getProjectIdFromUrl()
  private url = Utility.getProjectResource(this.projectId, RESOURCE_NAME.PERMISSIONS)
  private sharedPermUrl = Utility.getProjectResource(this.projectId, RESOURCE_NAME.SHARED_PERMISSION)
  fg = new FormGroup({
    type: new FormControl({ value: 'COMMON_PERMISSIONS', disabled: false }),
    commonPermissionIds: new FormControl({ value: [], disabled: false }),
    apiPermissionIds: new FormControl({ value: [], disabled: false }),
    sharedApi: new FormControl({ value: [], disabled: false }),
  });
  parentOptions: IOption[] = []
  commonOptions: IOption[] = []
  apiOptions: IOption[] = []
  sharedApiOptions: IOption[] = [];
  permErrorMsg = undefined;
  constructor(
    public dialogRef: MatDialogRef<AddPermissionDialogComponent>,
    public httpProxySvc: HttpProxyService,
    public route: RouterWrapperService,
  ) { }
  getShared(): IQueryProvider {
    return {
      readByQuery: (num: number, size: number, query?: string, by?: string, order?: string, header?: {}) => {
        return this.httpProxySvc.readEntityByQuery<IRole>(this.sharedPermUrl, num, size, undefined, by, order, header)
      }
    } as IQueryProvider
  }
  getCommonPermissions(): IQueryProvider {
    return {
      readByQuery: (num: number, size: number, query?: string, by?: string, order?: string, header?: {}) => {
        return this.httpProxySvc.readEntityByQuery<IRole>(this.url, num, size, "types:COMMON", by, order, header)
      }
    } as IQueryProvider
  }
  getApiPermissions(): IQueryProvider {
    return {
      readByQuery: (num: number, size: number, query?: string, by?: string, order?: string, header?: {}) => {
        return this.httpProxySvc.readEntityByQuery<IRole>(this.url, num, size, "types:API", by, order, header)
      }
    } as IQueryProvider
  }
  dismiss(event: MouseEvent) {
    let permIds = []
    if (this.fg.get('type').value === 'COMMON_PERMISSIONS') {
      permIds = this.fg.get('commonPermissionIds').value || []
    } else if (this.fg.get('type').value === 'API_PERMISSIONS') {
      permIds = this.fg.get('apiPermissionIds').value || []
    } else if (this.fg.get('type').value === 'EXTERNAL_PERMISSION') {
      permIds = this.fg.get('sharedApi').value || []
    }
    if (permIds.length === 0) {
      this.permErrorMsg = 'PERM_ERROR_MSG'
      return;
    }
    this.dialogRef.close({
      permIds: permIds,
      type: this.fg.get('type').value,
    })
    event.preventDefault();
  }
  close(event: MouseEvent) {
    this.dialogRef.close()
    event.preventDefault();
  }
}
