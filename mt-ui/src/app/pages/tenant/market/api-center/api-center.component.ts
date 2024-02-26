import { Component } from '@angular/core';
import { ISearchConfig, ISearchEvent } from 'src/app/components/search/search.component';
import { IEndpoint, IOption } from 'src/app/misc/interface';
import { APP_CONSTANT, CONST_HTTP_METHOD, RESOURCE_NAME } from 'src/app/misc/constant';
import { RouterWrapperService } from 'src/app/services/router-wrapper';
import { TableHelper } from 'src/app/clazz/table-helper';
import { HttpProxyService } from 'src/app/services/http-proxy.service';
import { getUrl } from 'src/app/misc/utility';
import { environment } from 'src/environments/environment';
@Component({
  selector: 'app-api-center',
  templateUrl: './api-center.component.html',
  styleUrls: ['./api-center.component.css']
})
export class ApiCenterComponent {
  private url = getUrl([environment.serverUri, APP_CONSTANT.MT_AUTH_ACCESS_PATH, RESOURCE_NAME.SHARED_ENDPOINTS])
  public formId = "sharedEndpointTableColumnConfig";
  columnList = {
    id: 'ID',
    name: 'NAME',
    type: 'TYPE',
    description: 'DESCRIPTION',
    projectId: 'API_PROJECT_ID',
    path: 'URL',
    method: 'METHOD',
    action: 'SUBSCRIBE',
  }
  httpMethodList = CONST_HTTP_METHOD;
  public allClientList: IOption[];
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
  public tableSource: TableHelper<IEndpoint> = new TableHelper(this.columnList, 10, this.httpSvc, this.url);
  searchConfigs: ISearchConfig[] = this.initSearchConfig
  constructor(
    public httpSvc: HttpProxyService,
    public router: RouterWrapperService,
  ) {
  }
  getOption(value: string, options: IOption[]) {
    return options.find(e => e.value == value)
  }
  public create(id: string) {
    const endpoint = this.tableSource.dataSource.data.find(e => e.id === id)!
    this.router.navNewSubscribeRequestDetail(endpoint)
  }
  doSearch(config: ISearchEvent) {
    this.tableSource = new TableHelper(this.columnList, 10, this.httpSvc, this.url, config.value);
    this.tableSource.loadPage(0)
  }
}
