import { Component } from '@angular/core';
import { TableHelper } from 'src/app/clazz/table-helper';
import { RESOURCE_NAME } from 'src/app/misc/constant';
import { Utility } from 'src/app/misc/utility';
import { DeviceService } from 'src/app/services/device.service';
import { HttpProxyService } from 'src/app/services/http-proxy.service';
import { IBellNotification } from 'src/app/services/message.service';

@Component({
  selector: 'app-summary-message',
  templateUrl: './summary-message.component.html',
  styleUrls: []
})
export class MessageCenterComponent {
  columnList = {
    date: 'DATE',
    title: 'TITLE',
    message: 'MESSAGE',
    traceId: 'TRACE_ID',
  }
  private url = Utility.getMgmtResource(RESOURCE_NAME.MGMT_BELL_NOTIFICATION)
  public tableSource: TableHelper<IBellNotification> = new TableHelper(this.columnList, 10, this.httpSvc, this.url);
  constructor(
    public httpSvc: HttpProxyService,
    private deviceSvc: DeviceService
  ) {
    this.deviceSvc.updateDocTitle('MGMT_MESSAGE_DOC_TITLE')
    this.tableSource.loadPage(0);
  }
  getValidationMsg(mes: string[]) {
    const msg = (mes || []).filter((e, i) => i > 0)
    if (Utility.notEmpty(msg)) {
      return msg.join(", ")
    } else {
      return ''
    }
  }
}
