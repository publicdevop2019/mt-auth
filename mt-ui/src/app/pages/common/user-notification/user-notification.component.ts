import { Component } from '@angular/core';
import { MatBottomSheet } from '@angular/material/bottom-sheet';
import { ActivatedRoute } from '@angular/router';
import { FormInfoService } from 'mt-form-builder';
import { SummaryEntityComponent } from 'src/app/clazz/summary.component';
import { IBellNotification } from 'src/app/services/message.service';
import { RouterWrapperService } from 'src/app/services/router-wrapper';
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
    public activated: ActivatedRoute,
    public router: RouterWrapperService,
    public bottomSheet: MatBottomSheet,
    public fis: FormInfoService,
  ) {
    super(entitySvc, activated, router, bottomSheet, fis, -2);
    this.doRefresh();
    this.initTableSetting();
  }
  doRefresh() {
    super.doSearch({ value: '', resetPage: false })
  }
}
