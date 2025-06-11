import { Component } from '@angular/core';
import { TableHelper } from 'src/app/clazz/table-helper';
import { RESOURCE_NAME } from 'src/app/misc/constant';
import { Utility } from 'src/app/misc/utility';
import { DeviceService } from 'src/app/services/device.service';
import { HttpProxyService } from 'src/app/services/http-proxy.service';
import { IBellNotification } from 'src/app/services/message.service';

@Component({
  selector: 'app-user-message',
  templateUrl: './user-message.component.html',
  styleUrls: []
})
export class UserMessageComponent{
  columnList = {
    date: 'DATE',
    title: 'TITLE',
    message: 'MESSAGE',
    traceId: 'TRACE_ID',
  }
  private url = Utility.getUserResource(RESOURCE_NAME.USER_BELL_NOTIFICATION)
  public tableSource: TableHelper<IBellNotification> = new TableHelper(this.columnList, 10, this.httpSvc, this.url);
  constructor(
    public httpSvc: HttpProxyService,
    public deviceSvc: DeviceService,
  ) {
    this.deviceSvc.updateDocTitle('USER_MSG_DOC_TITLE')
    this.tableSource.loadPage(0);
  }
}
