import { Component } from '@angular/core';
import { MatBottomSheet } from '@angular/material/bottom-sheet';
import { FormInfoService } from 'mt-form-builder';
import { SummaryEntityComponent } from 'src/app/clazz/summary.component';
import { DeviceService } from 'src/app/services/device.service';
import { IBellNotification, MessageService } from 'src/app/services/message.service';

@Component({
  selector: 'app-summary-message',
  templateUrl: './summary-message.component.html',
  styleUrls: ['./summary-message.component.css']
})
export class MessageCenterComponent extends SummaryEntityComponent<IBellNotification, IBellNotification>{
  public formId = "authMsgTableColumnConfig";
  columnList = {
    date: 'DATE',
    title: 'TITLE',
    message: 'MESSAGE',
  }
  constructor(
    public entitySvc: MessageService,
    public deviceSvc: DeviceService,
    public bottomSheet: MatBottomSheet,
    public fis: FormInfoService,
  ) {
    super(entitySvc, deviceSvc, bottomSheet, fis,-2);
    this.doRefresh();
    this.initTableSetting();
  }
  doRefresh(){
    super.doSearch({value:'',resetPage:false})
  }
}
