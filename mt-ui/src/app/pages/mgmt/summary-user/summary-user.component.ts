import { Component, OnDestroy } from '@angular/core';
import { MatBottomSheet } from '@angular/material/bottom-sheet';
import { MatDialog } from '@angular/material/dialog';
import { FormInfoService } from 'mt-form-builder';
import { SummaryEntityComponent } from 'src/app/clazz/summary.component';
import { ISearchConfig, ISearchEvent } from 'src/app/components/search/search.component';
import { UserService } from 'src/app/services/user.service';
import { MgmtUserComponent } from '../mgmt-user/mgmt-user.component';
import { IAuthUser } from 'src/app/misc/interface';
import { Utility } from 'src/app/misc/utility';
import { ActivatedRoute } from '@angular/router';
import { RouterWrapperService } from 'src/app/services/router-wrapper';
import { TableHelper } from 'src/app/clazz/table-helper';
import { RESOURCE_NAME } from 'src/app/misc/constant';
import { HttpProxyService } from 'src/app/services/http-proxy.service';
import { BannerService } from 'src/app/services/banner.service';
import { Logger } from 'src/app/misc/logger';
@Component({
  selector: 'app-summary-user',
  templateUrl: './summary-user.component.html',
})
export class SummaryResourceOwnerComponent {
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
  sheetComponent = MgmtUserComponent;
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
    public bannerSvc: BannerService,
    public httpSvc: HttpProxyService,
    public route: RouterWrapperService,
  ) {
  }
  revokeUserToken(id: string) {
    this.httpSvc.revokeUserToken(id).subscribe(result => {
      this.bannerSvc.notify(result)
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
