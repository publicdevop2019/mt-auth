import { ChangeDetectorRef, Component } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { map, tap } from 'rxjs/operators';
import { Utility } from 'src/app/misc/utility';
import { INode } from 'src/app/components/dynamic-tree/dynamic-tree.component';
import { IProjectUser } from 'src/app/misc/interface';
import { RouterWrapperService } from 'src/app/services/router-wrapper';
import { RESOURCE_NAME } from 'src/app/misc/constant';
import { HttpProxyService } from 'src/app/services/http-proxy.service';
import { IRole } from '../my-roles/my-roles.component';
import { DeviceService } from 'src/app/services/device.service';
import { MatTableDataSource } from '@angular/material/table';
import { MatDialog } from '@angular/material/dialog';
import { AddRoleDialogComponent } from 'src/app/components/add-role-dialog/add-role-dialog.component';
import { Logger } from 'src/app/misc/logger';
interface IRoleTable {
  id: string,
  name: string,
}
@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.css']
})
export class UserComponent {
  public projectId = this.router.getProjectIdFromUrl()
  private userId = this.router.getUserIdFromUrl()
  private url = Utility.getProjectResource(this.projectId, RESOURCE_NAME.USERS)
  private data: IProjectUser = undefined
  columnList = {
    name: 'NAME',
    delete: 'DELETE',
  };
  displayedColumns() {
    return ['name', 'delete']
  };
  dataSource: MatTableDataSource<IRoleTable> = new MatTableDataSource([]);
  public fg: FormGroup = new FormGroup({
    name: new FormControl({ value: '', disabled: true }),
  })
  constructor(
    public router: RouterWrapperService,
    public httpSvc: HttpProxyService,
    public deviceSvc: DeviceService,
    public dialog: MatDialog,
  ) {
    this.deviceSvc.updateDocTitle('USER_DOC_TITLE')
    this.loadUser()
  }
  removeRole(row: IRoleTable) {
    this.httpSvc.removeUserRole(this.projectId, this.userId, row.id, Utility.getChangeId()).subscribe(next => {
      this.deviceSvc.notify(next)
      this.loadUser()
    })
  }
  addRole() {
    const dialogRef = this.dialog.open(AddRoleDialogComponent, { data: {} });
    dialogRef.afterClosed().subscribe(data => {
      if (data !== undefined) {
        Logger.debug("role ids {}", data)
        this.httpSvc.adddUserRole(this.projectId, this.userId, data.roleIds, Utility.getChangeId()).subscribe(next => {
          this.deviceSvc.notify(next)
          this.loadUser()
        })
      }
    })
  }
  loadUser() {
    this.httpSvc.readEntityById<IProjectUser>(this.url, this.userId).subscribe(next => {
      this.fg.get('name').setValue(next.displayName);
      this.data = next;
      this.dataSource = new MatTableDataSource(this.data.roleDetails || []);
    });
  }
}
