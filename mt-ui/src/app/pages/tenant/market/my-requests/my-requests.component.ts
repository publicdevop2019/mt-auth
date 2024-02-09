import { Component } from '@angular/core';
import { ISubRequest } from '../subscribe-request/subscribe-request.component';
import { RouterWrapperService } from 'src/app/services/router-wrapper';
import { TableHelper } from 'src/app/clazz/table-helper';
import { HttpProxyService } from 'src/app/services/http-proxy.service';
import { APP_CONSTANT, RESOURCE_NAME } from 'src/app/misc/constant';
import { Utility, getUrl } from 'src/app/misc/utility';
import { environment } from 'src/environments/environment';
import { BannerService } from 'src/app/services/banner.service';
export interface IMySubReq extends ISubRequest {
  endpointName: string,
  projectName: string,
  rejectionReason: string,
  status: string,
  approvedBy: string,
  createdBy: string,
  updateAt: string,
  createAt: string,
  endpointProjectId: string,
}
@Component({
  selector: 'app-my-requests',
  templateUrl: './my-requests.component.html',
  styleUrls: ['./my-requests.component.css']
})
export class MyRequestsComponent {
  private url = getUrl([environment.serverUri, APP_CONSTANT.MT_AUTH_ACCESS_PATH, RESOURCE_NAME.SUBSCRIPTIONS_REQUEST])
  columnList = {
    id: 'ID',
    projectName: 'SUB_PROJECT_NAME',
    endpointName: 'API_NAME',
    status: 'STATUS',
    rejectionReason: 'REJECTION_REASON',
    update: 'UPDATE_REQUEST',
    cancel: 'CANCEL',
  }
  public tableSource: TableHelper<IMySubReq> = new TableHelper(this.columnList, 10, this.httpSvc, this.url, 'type:my_request');
  constructor(
    public banner: BannerService,
    public router: RouterWrapperService,
    public httpSvc: HttpProxyService,
  ) {
    this.tableSource.loadPage(0)
  }
  cancel(id: string) {
    this.httpSvc.cancelSubRequest(id, Utility.getChangeId()).subscribe(() => {
      this.banner.notify(true)
      this.tableSource.refresh()
    }, () => {
      this.banner.notify(false)
    })
  }
  public edit(id: string) {
    const data = this.tableSource.dataSource.data.find(e => e.id === id)
    this.router.navSubscribeRequestDetail(id, data)
  }
}