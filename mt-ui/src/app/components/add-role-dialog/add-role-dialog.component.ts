import { Component } from '@angular/core';
import { FormGroup, FormControl } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { IRole } from 'src/app/pages/tenant/project/my-roles/my-roles.component';
import { HttpProxyService } from 'src/app/services/http-proxy.service';
import { RouterWrapperService } from 'src/app/services/router-wrapper';
import { RESOURCE_NAME } from 'src/app/misc/constant';
import { Utility } from 'src/app/misc/utility';
import { IOption, IQueryProvider } from 'src/app/misc/interface';
import { Logger } from 'src/app/misc/logger';

@Component({
  selector: 'app-add-role-dialog',
  templateUrl: './add-role-dialog.component.html',
  styleUrls: ['./add-role-dialog.component.css']
})
export class AddRoleDialogComponent {
  public projectId = this.route.getProjectIdFromUrl()
  private url = Utility.getProjectResource(this.projectId, RESOURCE_NAME.ROLES)
  fg = new FormGroup({
    roleIds: new FormControl({ value: [], disabled: false }),
  });
  options: IOption[] = []
  errorMsg = undefined;
  constructor(
    public dialogRef: MatDialogRef<AddRoleDialogComponent>,
    public httpProxySvc: HttpProxyService,
    public route: RouterWrapperService,
  ) { }
  getRoles(): IQueryProvider {
    return {
      readByQuery: (num: number, size: number, query?: string, by?: string, order?: string, header?: {}) => {
        return this.httpProxySvc.readEntityByQuery<IRole>(this.url, num, size, "types:USER", by, order, header)
      }
    } as IQueryProvider
  }
  dismiss(event: MouseEvent) {
    let permIds = this.fg.get('roleIds').value || [];
    if (permIds.length === 0) {
      this.errorMsg = 'ROLE_ERROR_MSG'
      return;
    }
    Logger.debug("role ids to be added {}", permIds)
    this.dialogRef.close({
      roleIds: permIds,
    })
    event.preventDefault();
  }
  close(event: MouseEvent) {
    this.dialogRef.close()
    event.preventDefault();
  }
}
