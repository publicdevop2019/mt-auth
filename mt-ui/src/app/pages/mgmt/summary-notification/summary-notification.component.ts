import { Component } from '@angular/core';
import { MatBottomSheet } from '@angular/material/bottom-sheet';
import { FormInfoService } from 'mt-form-builder';
import { SummaryEntityComponent } from 'src/app/clazz/summary.component';
import { DeviceService } from 'src/app/services/device.service';
import { INotification, NotificationService } from 'src/app/services/notification.service';

@Component({
  selector: 'app-summary-notification',
  templateUrl: './summary-notification.component.html',
  styleUrls: ['./summary-notification.component.css']
})
export class SummaryNotificationComponent extends SummaryEntityComponent<INotification, INotification>{
  public formId = "notificationTableColumnConfig";
  columnList = {
    date: 'DATE',
    title: 'TITLE',
    message: 'MESSAGE',
    type: 'TYPE',
    status: 'STATUS',
  }
  constructor(
    public entitySvc: NotificationService,
    public deviceSvc: DeviceService,
    public bottomSheet: MatBottomSheet,
    public fis: FormInfoService,
  ) {
    super(entitySvc, deviceSvc, bottomSheet, fis, -2);
    this.doRefresh();
    this.initTableSetting();
  }
  doRefresh() {
    super.doSearch({ value: '', resetPage: false })
  }
}
