import { Component } from '@angular/core';
import { ISubRequest } from '../subscribe-request/subscribe-request.component';
import { RouterWrapperService } from 'src/app/services/router-wrapper';
import { TableHelper } from 'src/app/clazz/table-helper';
import { HttpProxyService } from 'src/app/services/http-proxy.service';
import { APP_CONSTANT, RESOURCE_NAME } from 'src/app/misc/constant';
import { Utility } from 'src/app/misc/utility';
import { environment } from 'src/environments/environment';
import { DeviceService } from 'src/app/services/device.service';
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
  private url = Utility.getUrl([environment.serverUri, APP_CONSTANT.MT_AUTH_ACCESS_PATH, RESOURCE_NAME.SUBSCRIPTIONS_REQUEST])
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
    public deviceSvc: DeviceService,
    public router: RouterWrapperService,
    public httpSvc: HttpProxyService,
  ) {
    this.deviceSvc.updateDocTitle('MY_REQ_DOC_TITLE')
    this.tableSource.loadPage(0)
  }
  cancel(id: string) {
    this.httpSvc.cancelSubRequest(id, Utility.getChangeId()).subscribe(() => {
      this.deviceSvc.notify(true)
      this.tableSource.refresh()
    }, () => {
      this.deviceSvc.notify(false)
    })
  }
  public edit(id: string) {
    const data = this.tableSource.dataSource.data.find(e => e.id === id)
    this.router.navSubscribeRequestDetail(id, data)
  }
}