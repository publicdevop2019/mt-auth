import { Component, Inject, OnInit } from '@angular/core';
import { FormGroup, FormControl } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { IOption, IQueryProvider } from 'mt-form-builder/lib/classes/template.interface';
import { IRole } from 'src/app/pages/tenant/project/my-roles/my-roles.component';
import { HttpProxyService } from 'src/app/services/http-proxy.service';
import { MyPermissionService } from 'src/app/services/my-permission.service';
import { SharedPermissionService } from 'src/app/services/shared-permission.service';
import { DialogData } from '../batch-update-cors/batch-update-cors.component';

@Component({
  selector: 'app-add-permission-dialog',
  templateUrl: './add-permission-dialog.component.html',
  styleUrls: ['./add-permission-dialog.component.css']
})
export class AddPermissionDialogComponent implements OnInit {
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
    @Inject(MAT_DIALOG_DATA) public data: DialogData,
    public permissoinSvc: MyPermissionService,
    public httpProxySvc: HttpProxyService,
    public sharedPermSvc: SharedPermissionService,
  ) { }

  ngOnInit(): void {
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
