import { Component } from '@angular/core';
import { MatBottomSheet } from '@angular/material/bottom-sheet';
import { ActivatedRoute } from '@angular/router';
import { FormInfoService } from 'mt-form-builder';
import { SummaryEntityComponent } from 'src/app/clazz/summary.component';
import { Utility } from 'src/app/misc/utility';
import { IBellNotification, MessageService } from 'src/app/services/message.service';
import { RouterWrapperService } from 'src/app/services/router-wrapper';

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
    public activated: ActivatedRoute,
    public router: RouterWrapperService,
    public bottomSheet: MatBottomSheet,
    public fis: FormInfoService,
  ) {
    super(entitySvc,activated, router, bottomSheet, fis, -2);
    this.doRefresh();
    this.initTableSetting();
  }
  doRefresh() {
    super.doSearch({ value: '', resetPage: false })
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
