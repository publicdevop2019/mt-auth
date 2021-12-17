import { Component, OnInit } from '@angular/core';
import { MatBottomSheet } from '@angular/material/bottom-sheet';
import { webSocket } from "rxjs/webSocket";
import { SummaryEntityComponent } from 'src/app/clazz/summary.component';
import { DeviceService } from 'src/app/services/device.service';
import { IDetail, MessageService } from 'src/app/services/message.service';

@Component({
  selector: 'app-message-center',
  templateUrl: './message-center.component.html',
  styleUrls: ['./message-center.component.css']
})
export class MessageCenterComponent extends SummaryEntityComponent<IDetail, IDetail>{
  displayedColumns: string[] = ['date','message'];
  constructor(
    public entitySvc: MessageService,
    public deviceSvc: DeviceService,
    public bottomSheet: MatBottomSheet,
  ) {
    super(entitySvc, deviceSvc, bottomSheet, -2);
    this.doRefresh();

  }
  doRefresh(){
    super.doSearch({value:'',resetPage:false})
  }
}
