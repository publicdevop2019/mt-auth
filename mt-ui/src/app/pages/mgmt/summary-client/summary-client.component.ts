import { Component } from '@angular/core';
import { Utility } from 'src/app/misc/utility';
import { ISearchConfig, ISearchEvent } from 'src/app/components/search/search.component';
import { MgmtClientComponent } from '../client/client.component';
import { APP_CONSTANT, CONST_GRANT_TYPE, RESOURCE_NAME } from 'src/app/misc/constant';
import { IClient, IOption } from 'src/app/misc/interface';
import { TableHelper } from 'src/app/clazz/table-helper';
import { BannerService } from 'src/app/services/banner.service';
import { HttpProxyService } from 'src/app/services/http-proxy.service';
import { RouterWrapperService } from 'src/app/services/router-wrapper';
@Component({
  selector: 'app-summary-client',
  templateUrl: './summary-client.component.html',
})
export class SummaryClientComponent {
  columnList = {
    name: 'NAME',
    id: 'ID',
    description: 'DESCRIPTION',
    resourceIndicator: 'RESOURCE_INDICATOR',
    grantTypeEnums: 'GRANTTYPE_ENUMS',
    accessTokenValiditySeconds: 'ACCESS_TOKEN_VALIDITY_SECONDS',
    resourceIds: 'RESOURCEIDS',
    more: 'MORE',
    token: 'REVOKE_TOKEN',
  }
  sheetComponent = MgmtClientComponent;
  public grantTypeList: IOption[] = CONST_GRANT_TYPE;
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
      searchLabel: 'NAME',
      searchValue: 'name',
      type: 'text',
      multiple: {
        delimiter: '.'
      }
    },
    {
      searchLabel: 'GRANTTYPE_ENUMS',
      searchValue: 'grantTypeEnums',
      type: 'dropdown',
      multiple: {
        delimiter: '$'
      },
      source: CONST_GRANT_TYPE
    },
    {
      searchLabel: 'RESOURCE_INDICATOR',
      searchValue: 'resourceIndicator',
      type: 'boolean',
    },
    {
      searchLabel: 'ACCESS_TOKEN_VALIDITY_SECONDS',
      searchValue: 'accessTokenValiditySeconds',
      type: 'range',
    },
    {
      searchLabel: 'RESOURCEIDS',
      searchValue: 'resourceIds',
      type: 'dynamic',
      resourceUrl: APP_CONSTANT.MGMT_RESOURCE_CLIENT_DROPDOWN,
      multiple: {
        delimiter: '.'
      },
      source: []
    }
  ]
  private url = Utility.getMgmtResource(RESOURCE_NAME.MGMT_CLIENTS)
  public tableSource: TableHelper<IClient> = new TableHelper(this.columnList, 10, this.httpSvc, this.url);
  constructor(
    public banner: BannerService,
    public httpSvc: HttpProxyService,
    public route: RouterWrapperService,
  ) {
    this.tableSource.loadPage(0)
  }
  revokeClientToken(clientId: string) {
    this.httpSvc.revokeClientToken(clientId).subscribe(result => {
      this.banner.notify(result)
    })
  }
  getList(inputs: string[]) {
    return inputs.map(e => <IOption>{ label: e, value: e })
  }
  getResourceList(inputs?: { name: string, id: string }[]) {
    return (inputs || []).map(e => ({ label: e.name, value: e.id }))
  }
  doSearch(config: ISearchEvent) {
    this.tableSource = new TableHelper(this.tableSource.columnConfig, this.tableSource.pageSize, this.httpSvc, this.tableSource.url, config.value);
    this.tableSource.loadPage(0)
  }
  view(id: string) {
    this.route.navMgmtClientDetail(id)
  }
}