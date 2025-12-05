import { Component } from '@angular/core';
import { Utility } from 'src/app/misc/utility';
import { ISearchConfig, ISearchEvent } from 'src/app/components/search/search.component';
import { MgmtClientComponent } from '../client/client.component';
import { APP_CONSTANT, CONST_GRANT_TYPE, RESOURCE_NAME } from 'src/app/misc/constant';
import { IClient, IOption } from 'src/app/misc/interface';
import { TableHelper } from 'src/app/clazz/table-helper';
import { DeviceService } from 'src/app/services/device.service';
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
    grantTypeEnums: 'GRANTTYPE_ENUMS',
    accessTokenValiditySeconds: 'ACCESS_TOKEN_VALIDITY_SECONDS',
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
      searchLabel: 'ACCESS_TOKEN_VALIDITY_SECONDS',
      searchValue: 'accessTokenValiditySeconds',
      type: 'range',
    }
  ]
  private url = Utility.getMgmtResource(RESOURCE_NAME.MGMT_CLIENTS)
  public tableSource: TableHelper<IClient> = new TableHelper(this.columnList, 10, this.httpSvc, this.url);
  constructor(
    public deviceSvc: DeviceService,
    public httpSvc: HttpProxyService,
    public route: RouterWrapperService,
  ) {
    this.deviceSvc.updateDocTitle('MGMT_CLIENT_SUM_DOC_TITLE')
    this.tableSource.loadPage(0)
  }
  revokeClientToken(clientId: string) {
    this.httpSvc.revokeClientToken(clientId).subscribe(result => {
      this.deviceSvc.notify(result)
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