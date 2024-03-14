import { Component } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { filter, switchMap, take } from 'rxjs/operators';
import { Utility } from 'src/app/misc/utility';
import { EndpointAnalysisComponent } from 'src/app/components/endpoint-analysis-dialog/endpoint-analysis-dialog.component';
import { EnterReasonDialogComponent } from 'src/app/components/enter-reason-dialog/enter-reason-dialog.component';
import { ISearchConfig, ISearchEvent } from 'src/app/components/search/search.component';
import { HttpProxyService } from 'src/app/services/http-proxy.service';
import { ProjectService } from 'src/app/services/project.service';
import { IDomainContext, IEndpoint, IOption, IPermission } from 'src/app/misc/interface';
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
    public banner: DeviceService,
  ) {
    this.permissionHelper.canDo(this.projectId, httpSvc.currentUserAuthInfo.permissionIds, 'VIEW_API').pipe(take(1)).subscribe(b => {
      if (b.result) {
        this.tableSource.loadPage(0)
      }
    })
    this.permissionHelper.canDo(this.projectId, httpSvc.currentUserAuthInfo.permissionIds, 'VIEW_API', 'VIEW_CLIENT').pipe(take(1)).subscribe(b => {
      if (b.result) {
        //prepare search
        this.searchConfigs = [...this.initSearchConfig, {
          searchLabel: 'PARENT_CLIENT',
          searchValue: 'resourceId',
          type: 'dynamic',
          resourceUrl: Utility.getTenantUrl(b.projectId, APP_CONSTANT.TENANT_RESOURCE_CLIENT_DROPDOWN),
          multiple: {
            delimiter: '.'
          },
          source: []
        },];
      } else {
        this.searchConfigs = [...this.initSearchConfig]
      }
    })
    this.permissionHelper.canDo(this.projectId, httpSvc.currentUserAuthInfo.permissionIds, 'EDIT_API').pipe(take(1)).subscribe(b => {
      this.tableSource.columnConfig = b.result ? {
        id: 'ID',
        name: 'NAME',
        resourceId: 'PARENT_CLIENT',
        path: 'URL',
        method: 'METHOD',
        edit: 'EDIT',
        delete: 'DELETE',
        expire: 'EXPIRE',
        expireReason: 'EXPIRE_REASON',
        report: 'VIEW_REPORT',
      } : {
        id: 'ID',
        name: 'NAME',
        resourceId: 'PARENT_CLIENT',
        path: 'URL',
        method: 'METHOD',
      }
    })
  }
  createNewEndpoint() {
    const dialogRef = this.dialog.open(EndpointCreateDialogComponent, { data: {} });
    dialogRef.afterClosed().subscribe(next => {
      if (next !== undefined) {
        const data = <IDomainContext<IEndpoint>>{ context: 'new', from: next, params: { 'projectId': this.projectId } }
        this.route.navProjectNewEndpointDetail(data)
      }
    })
  }
  editEndpoint(id: string): void {
    this.route.navProjectEndpointDetail(id)
  }
  getOption(row: IEndpoint) {
    return <IOption>{ label: row.resourceName, value: row.resourceId }
  }
  getHttpOption(value: string, options: IOption[]) {
    return options.find(e => e.value == value)
  }
  doExpireById(id: string) {
    const dialogRef = this.dialog.open(EnterReasonDialogComponent, { data: {} });
    dialogRef.afterClosed().pipe(filter(e => e)).pipe(switchMap((reason: string) => {
      return this.httpSvc.expireEndpoint(this.projectId, id, reason, Utility.getChangeId())
    })).subscribe(() => {
      this.banner.notify(true)
      this.tableSource.refresh()
    }, () => {
      this.banner.notify(false)
    })
  }
  viewReport(id: string) {
    this.dialog.open(EndpointAnalysisComponent, { data: { endpointId: id, projectId: this.route.getProjectIdFromUrl() } });
  }
  doSearch(config: ISearchEvent) {
    this.tableSource = new TableHelper(this.tableSource.columnConfig, this.tableSource.pageSize, this.httpSvc, this.url, config.value);
    this.tableSource.loadPage(0)
  }
}
