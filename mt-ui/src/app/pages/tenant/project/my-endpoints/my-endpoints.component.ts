import { Component } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { filter, switchMap, take } from 'rxjs/operators';
import { Utility } from 'src/app/misc/utility';
import { EnterReasonDialogComponent } from 'src/app/components/enter-reason-dialog/enter-reason-dialog.component';
import { ISearchConfig, ISearchEvent } from 'src/app/components/search/search.component';
import { HttpProxyService } from 'src/app/services/http-proxy.service';
import { ProjectService } from 'src/app/services/project.service';
import { IEndpoint, IOption, IPermission } from 'src/app/misc/interface';
import { APP_CONSTANT, CONST_HTTP_METHOD, RESOURCE_NAME } from 'src/app/misc/constant';
import { EndpointCreateDialogComponent } from 'src/app/components/endpoint-create-dialog/endpoint-create-dialog.component';
import { RouterWrapperService } from 'src/app/services/router-wrapper';
import { PermissionHelper } from 'src/app/clazz/permission-helper';
import { TableHelper } from 'src/app/clazz/table-helper';
import { DeviceService } from 'src/app/services/device.service';
@Component({
  selector: 'app-my-endpoints',
  templateUrl: './my-endpoints.component.html',
  styleUrls: ['./my-endpoints.component.css']
})
export class MyApisComponent {
  public projectId = this.route.getProjectIdFromUrl()
  private url = Utility.getProjectResource(this.projectId, RESOURCE_NAME.ENDPOINTS)
  httpMethodList = CONST_HTTP_METHOD;
  private initSearchConfig: ISearchConfig[] = [
    {
      searchLabel: 'ID',
      searchValue: 'id',
      type: 'text',
      multiple: {
        delimiter: '.'
      }
    },
    {
      searchLabel: 'METHOD',
      searchValue: 'method',
      type: 'dropdown',
      source: CONST_HTTP_METHOD
    },
  ]
  searchConfigs: ISearchConfig[] = []
  public tableSource: TableHelper<IPermission> = new TableHelper({}, 10, this.httpSvc, this.url);
  public permissionHelper: PermissionHelper = new PermissionHelper(this.projectSvc.permissionDetail)
  constructor(
    public projectSvc: ProjectService,
    public httpSvc: HttpProxyService,
    public dialog: MatDialog,
    public route: RouterWrapperService,
    public deviceSvc: DeviceService,
  ) {
    this.deviceSvc.updateDocTitle('TENANT_EP_DOC_TITLE')
    this.permissionHelper.canDo(this.projectId, httpSvc.currentUserAuthInfo.permissionIds, 'API_MGMT').pipe(take(1)).subscribe(b => {
      if (b.result) {
        this.tableSource.loadPage(0)
      }
    })
    this.permissionHelper.canDo(this.projectId, httpSvc.currentUserAuthInfo.permissionIds, 'API_MGMT').pipe(take(1)).subscribe(b => {
      if (b.result) {
        //prepare search
        this.searchConfigs = [...this.initSearchConfig, {
          searchLabel: 'MY_ROUTER',
          searchValue: 'routerId',
          type: 'dynamic',
          sourceUrl: Utility.getUrl(["/",APP_CONSTANT.MT_AUTH_ACCESS_PATH, 'projects', this.projectId, RESOURCE_NAME.ROUTER]),
          multiple: {
            delimiter: '.'
          },
          source: []
        },];
      } else {
        this.searchConfigs = [...this.initSearchConfig]
      }
    })
    this.permissionHelper.canDo(this.projectId, httpSvc.currentUserAuthInfo.permissionIds, 'API_MGMT').pipe(take(1)).subscribe(b => {
      this.tableSource.columnConfig = b.result ? {
        id: 'ID',
        name: 'NAME',
        routerId: 'MY_ROUTER',
        path: 'URL',
        method: 'METHOD',
        edit: 'EDIT',
        delete: 'DELETE',
        expire: 'EXPIRE',
        expireReason: 'EXPIRE_REASON',
      } : {
        id: 'ID',
        name: 'NAME',
        routerId: 'MY_ROUTER',
        path: 'URL',
        method: 'METHOD',
      }
    })
  }
  createNewEndpoint() {
    const dialogRef = this.dialog.open(EndpointCreateDialogComponent, { data: {} });
    dialogRef.afterClosed().subscribe(next => {
      if (next !== undefined) {
        this.route.navProjectNewEndpointDetail(next)
      }
    })
  }
  editEndpoint(id: string): void {
    this.route.navProjectEndpointDetail(id)
  }
  getOption(row: IEndpoint) {
    return <IOption>{ label: row.routerName, value: row.routerId }
  }
  getHttpOption(value: string, options: IOption[]) {
    return options.find(e => e.value == value)
  }
  doExpireById(id: string) {
    const dialogRef = this.dialog.open(EnterReasonDialogComponent, { data: {} });
    dialogRef.afterClosed().pipe(filter(e => e)).pipe(switchMap((reason: string) => {
      return this.httpSvc.expireEndpoint(this.projectId, id, reason, Utility.getChangeId())
    })).subscribe(() => {
      this.deviceSvc.notify(true)
      this.tableSource.refresh()
    }, () => {
      this.deviceSvc.notify(false)
    })
  }
  doSearch(config: ISearchEvent) {
    this.tableSource = new TableHelper(this.tableSource.columnConfig, this.tableSource.pageSize, this.httpSvc, this.url, config.value);
    this.tableSource.loadPage(0)
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
