import { Component } from '@angular/core';
import { Utility } from 'src/app/misc/utility';
import { ISearchConfig, ISearchEvent } from 'src/app/components/search/search.component';
import { IEndpoint, IOption } from 'src/app/misc/interface';
import { APP_CONSTANT, CONST_HTTP_METHOD, RESOURCE_NAME } from 'src/app/misc/constant';
import { TableHelper } from 'src/app/clazz/table-helper';
import { HttpProxyService } from 'src/app/services/http-proxy.service';
import { RouterWrapperService } from 'src/app/services/router-wrapper';
import { DeviceService } from 'src/app/services/device.service';
@Component({
  selector: 'app-summary-endpoint',
  templateUrl: './summary-endpoint.component.html',
  styleUrls: []
})
export class SummaryEndpointComponent{
  columnList = {
    id: 'ID',
    name: 'NAME',
    description: 'DESCRIPTION',
    routerId: 'MY_ROUTER',
    path: 'URL',
    method: 'METHOD',
    more: 'MORE',
  }
  httpMethodList = CONST_HTTP_METHOD;
  searchConfigs: ISearchConfig[] = [
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
    {
      searchLabel: 'MY_ROUTER',
      searchValue: 'routerId',
      sourceUrl: APP_CONSTANT.MGMT_RESOURCE_ROUTER_DROPDOWN,
      type: 'dynamic',
      multiple: {
        delimiter: '.'
      },
      source:[]
    }
  ]
  private url = Utility.getMgmtResource(RESOURCE_NAME.MGMT_ENDPOINTS)
  public tableSource: TableHelper<IEndpoint> = new TableHelper(this.columnList, 10, this.httpSvc, this.url);
  constructor(
    public httpSvc: HttpProxyService,
    public route: RouterWrapperService,
    private deviceSvc: DeviceService
  ) {
    this.deviceSvc.updateDocTitle('MGMT_EP_SUM_DOC_TITLE')
    this.tableSource.loadPage(0)
  }
  getOption(row: IEndpoint) {
    return <IOption>{ label: row.routerName, value: row.routerId }
  }
  getHttpOption(value: string, options: IOption[]) {
    return options.find(e => e.value == value)
  }
  doSearch(config: ISearchEvent) {
    this.tableSource = new TableHelper(this.tableSource.columnConfig, this.tableSource.pageSize, this.httpSvc, this.tableSource.url, config.value);
    this.tableSource.loadPage(0)
  }
  viewEndpoint(id:string){
    this.route.navMgmtEndpointDetail(id)
  }
}
