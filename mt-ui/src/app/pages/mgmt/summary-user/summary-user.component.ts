import { Component } from '@angular/core';
import { ISearchConfig, ISearchEvent } from 'src/app/components/search/search.component';
import { IAuthUser } from 'src/app/misc/interface';
import { Utility } from 'src/app/misc/utility';
import { RouterWrapperService } from 'src/app/services/router-wrapper';
import { TableHelper } from 'src/app/clazz/table-helper';
import { RESOURCE_NAME } from 'src/app/misc/constant';
import { HttpProxyService } from 'src/app/services/http-proxy.service';
import { DeviceService } from 'src/app/services/device.service';
@Component({
  selector: 'app-summary-user',
  templateUrl: './summary-user.component.html',
})
export class SummaryUserComponent {
  columnList = {
    id: 'ID',
    email: 'EMAIL',
    locked: 'LOCKED',
    createdAt: 'CREATE_AT',
    edit: 'EDIT',
    token: 'REVOKE_TOKEN',
  }
  private url = Utility.getMgmtResource(RESOURCE_NAME.MGMT_USERS)
  public tableSource: TableHelper<IAuthUser> = new TableHelper(this.columnList, 10, this.httpSvc, this.url);
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
      searchLabel: 'EMAIL',
      searchValue: 'email',
      type: 'text',
      multiple: {
        delimiter: '.'
      }
    },
  ]
  constructor(
    public deviceSvc: DeviceService,
    public httpSvc: HttpProxyService,
    public route: RouterWrapperService,
  ) {
    this.deviceSvc.updateDocTitle('MGMT_USER_SUM_DOC_TITLE')
  }
  revokeUserToken(id: string) {
    this.httpSvc.revokeUserToken(id).subscribe(result => {
      this.deviceSvc.notify(result)
    })
  }
  viewUser(id: string) {
    this.route.navMgmtUserDetail(id)
  }
  doSearch(config: ISearchEvent) {
    this.tableSource = new TableHelper(this.tableSource.columnConfig, this.tableSource.pageSize, this.httpSvc, this.url, config.value);
    this.tableSource.loadPage(0)
  }
}
