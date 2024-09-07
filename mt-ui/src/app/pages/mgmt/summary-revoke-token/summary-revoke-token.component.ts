import { Component } from '@angular/core';
import { TableHelper } from 'src/app/clazz/table-helper';
import { ISearchConfig, ISearchEvent } from 'src/app/components/search/search.component';
import { RESOURCE_NAME } from 'src/app/misc/constant';
import { IRevokeToken } from 'src/app/misc/interface';
import { Utility } from 'src/app/misc/utility';
import { DeviceService } from 'src/app/services/device.service';
import { HttpProxyService } from 'src/app/services/http-proxy.service';
@Component({
  selector: 'app-summary-revoke-token',
  templateUrl: './summary-revoke-token.component.html',
  styleUrls: []
})
export class SummaryRevokeTokenComponent {
  searchConfigs: ISearchConfig[] = [
    {
      searchLabel: 'TARGET_ID',
      searchValue: 'targetId',
      type: 'text',
    },
  ]
  columnList = {
    targetId: 'TARGET_ID',
    issuedAt: 'ISSUE_AT',
    type: 'TYPE',
  }
  private url = Utility.getMgmtResource(RESOURCE_NAME.MGMT_REVOKE_TOKEN)
  public tableSource: TableHelper<IRevokeToken> = new TableHelper(this.columnList, 10, this.httpSvc, this.url);
  constructor(
    private httpSvc: HttpProxyService,
    private deviceSvc: DeviceService
  ) {
    this.deviceSvc.updateDocTitle('MGMT_REVOKE_TOKEN_DOC_TITLE')
  }
  doSearch(config: ISearchEvent) {
    this.tableSource = new TableHelper(this.tableSource.columnConfig, this.tableSource.pageSize, this.httpSvc, this.tableSource.url, config.value);
    this.tableSource.loadPage(0)
  }
}
