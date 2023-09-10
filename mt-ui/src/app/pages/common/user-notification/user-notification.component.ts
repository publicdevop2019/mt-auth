import { Component, OnInit } from '@angular/core';
import { MatBottomSheet } from '@angular/material/bottom-sheet';
import { FormInfoService } from 'mt-form-builder';
import { SummaryEntityComponent } from 'src/app/clazz/summary.component';
import { DeviceService } from 'src/app/services/device.service';
import { IBellNotification, MessageService } from 'src/app/services/message.service';
import { UserMessageService } from 'src/app/services/user-message.service';

@Component({
  selector: 'app-user-notification',
  templateUrl: './user-notification.component.html',
  styleUrls: ['./user-notification.component.css']
})
export class UserNotificationComponent extends SummaryEntityComponent<IBellNotification, IBellNotification>{
  public formId = "userMsgTableColumnConfig";
  columnList = {
    date: 'DATE',
    title: 'TITLE',
    message: 'MESSAGE',
  }
  constructor(
    public entitySvc: UserMessageService,
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
