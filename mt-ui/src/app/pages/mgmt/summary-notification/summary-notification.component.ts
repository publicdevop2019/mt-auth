import { Component } from '@angular/core';
import { TableHelper } from 'src/app/clazz/table-helper';
import { RESOURCE_NAME } from 'src/app/misc/constant';
import { INotification } from 'src/app/misc/interface';
import { Utility } from 'src/app/misc/utility';
import { DeviceService } from 'src/app/services/device.service';
import { HttpProxyService } from 'src/app/services/http-proxy.service';

@Component({
  selector: 'app-summary-notification',
  templateUrl: './summary-notification.component.html',
  styleUrls: []
})
export class SummaryNotificationComponent{
  columnList = {
    date: 'DATE',
    title: 'TITLE',
    message: 'MESSAGE',
    type: 'TYPE',
    status: 'STATUS',
  }
  private url = Utility.getMgmtResource(RESOURCE_NAME.MGMT_NOTIFICATION)
  public tableSource: TableHelper<INotification> = new TableHelper(this.columnList, 10, this.httpSvc, this.url);
  constructor(
    public httpSvc: HttpProxyService,
    private deviceSvc: DeviceService
  ) {
    this.deviceSvc.updateDocTitle('MGMT_NOTIFICATION_DOC_TITLE')
    this.tableSource.loadPage(0)
  }
}
