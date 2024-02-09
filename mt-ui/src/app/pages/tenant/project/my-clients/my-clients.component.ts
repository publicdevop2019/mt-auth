import { Component } from '@angular/core';
import { HttpProxyService } from 'src/app/services/http-proxy.service';
import { ProjectService } from 'src/app/services/project.service';
import { IClient, IClientCreate } from 'src/app/misc/interface';
import { IDomainContext } from 'src/app/clazz/summary.component';
import { take } from 'rxjs/operators';
import { MatDialog } from '@angular/material/dialog';
import { ClientCreateDialogComponent } from 'src/app/components/client-create-dialog/client-create-dialog.component';
import { Logger } from 'src/app/misc/logger';
import { Utility } from 'src/app/misc/utility';
import { RouterWrapperService } from 'src/app/services/router-wrapper';
import { RESOURCE_NAME } from 'src/app/misc/constant';
import { PermissionHelper } from 'src/app/clazz/permission-helper';
import { TableHelper } from 'src/app/clazz/table-helper';
import { BannerService } from 'src/app/services/banner.service';

@Component({
  selector: 'app-my-clients',
  templateUrl: './my-clients.component.html',
  styleUrls: ['./my-clients.component.css']
})
export class MyClientsComponent{
  public projectId = this.router.getProjectIdFromUrl()
  private url = Utility.getProjectResource(this.projectId, RESOURCE_NAME.CLIENTS)
  columnList: any = {};
  public tableSource: TableHelper<IClient> = new TableHelper(this.columnList, 10, this.httpSvc, this.url);
  public permissionHelper: PermissionHelper = new PermissionHelper(this.projectSvc.permissionDetail)
  constructor(
    public projectSvc: ProjectService,
    public httpSvc: HttpProxyService,
    public banner: BannerService,
    private router: RouterWrapperService,
    public dialog: MatDialog,
  ) {
    this.permissionHelper.canDo(this.projectId, httpSvc.currentUserAuthInfo.permissionIds, 'EDIT_CLIENT').pipe(take(1)).subscribe(b => {
      this.tableSource.columnConfig = b.result ? {
        name: 'NAME',
        types: 'TYPES',
        edit: 'EDIT',
        delete: 'DELETE',
      } : {
        name: 'NAME',
        types: 'TYPES',
      }
      this.tableSource.loadPage(0)
    })
  }
  createNewClient() {
    const dialogRef = this.dialog.open(ClientCreateDialogComponent, { data: {} });
    dialogRef.afterClosed().subscribe(next => {
      if (next !== undefined) {
        Logger.debugObj('client basic info', next)
        const data = <IDomainContext<IClientCreate>>{ context: 'new', from: next, params: {} }
        this.router.navProjectNewClientsDetail({ state: data })
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
      this.banner.notify(next)
    })
  }
}