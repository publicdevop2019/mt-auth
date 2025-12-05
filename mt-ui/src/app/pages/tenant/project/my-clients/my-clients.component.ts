import { Component } from '@angular/core';
import { HttpProxyService } from 'src/app/services/http-proxy.service';
import { ProjectService } from 'src/app/services/project.service';
import { ITenantClientSummary } from 'src/app/misc/interface';
import { take } from 'rxjs/operators';
import { MatDialog } from '@angular/material/dialog';
import { ClientCreateDialogComponent } from 'src/app/components/client-create-dialog/client-create-dialog.component';
import { Logger } from 'src/app/misc/logger';
import { Utility } from 'src/app/misc/utility';
import { RouterWrapperService } from 'src/app/services/router-wrapper';
import { RESOURCE_NAME } from 'src/app/misc/constant';
import { PermissionHelper } from 'src/app/clazz/permission-helper';
import { TableHelper } from 'src/app/clazz/table-helper';
import { DeviceService } from 'src/app/services/device.service';

@Component({
  selector: 'app-my-clients',
  templateUrl: './my-clients.component.html',
  styleUrls: ['./my-clients.component.css']
})
export class MyClientsComponent {
  public projectId = this.router.getProjectIdFromUrl()
  private url = Utility.getProjectResource(this.projectId, RESOURCE_NAME.CLIENTS)
  columnList: any = {};
  public tableSource: TableHelper<ITenantClientSummary> = new TableHelper(this.columnList, 10, this.httpSvc, this.url);
  public permissionHelper: PermissionHelper = new PermissionHelper(this.projectSvc.permissionDetail)
  constructor(
    public projectSvc: ProjectService,
    public httpSvc: HttpProxyService,
    public deviceSvc: DeviceService,
    private router: RouterWrapperService,
    public dialog: MatDialog,
  ) {
    this.deviceSvc.updateDocTitle('TENANT_CLIENT_DOC_TITLE')
    this.permissionHelper.canDo(this.projectId, httpSvc.currentUserAuthInfo.permissionIds, 'CLIENT_MGMT').pipe(take(1)).subscribe(b => {
      this.tableSource.columnConfig = b.result ? {
        name: 'NAME',
        type: 'TYPE',
        edit: 'EDIT',
        delete: 'DELETE',
      } : {
        name: 'NAME',
        type: 'TYPE',
      }
      this.tableSource.loadPage(0)
    })
  }
  createNewClient() {
    const dialogRef = this.dialog.open(ClientCreateDialogComponent, { data: {} });
    dialogRef.afterClosed().subscribe(next => {
      if (next !== undefined) {
        Logger.traceObj('client basic info', next)
        this.router.navProjectNewClientsDetail(next)
      }
    })
  }
  editClient(id: string): void {
    this.router.navProjectClientsDetail(id)
  }
  removeFirst(input: string[]) {
    return input.filter((e, i) => i !== 0);
  }
  doDeleteById(id: string) {
    this.httpSvc.deleteEntityById(this.url, id, Utility.getChangeId()).subscribe(next => {
      this.deviceSvc.notify(next)
      this.tableSource.refresh()
    }, () => {
      this.tableSource.refresh()
    })
  }
}