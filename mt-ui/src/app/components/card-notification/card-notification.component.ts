import { Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges } from '@angular/core';
import { IBellNotification, MessageService } from 'src/app/services/message.service';
import { DateTime, ToRelativeUnit } from "luxon";
import { TranslateService } from '@ngx-translate/core';
import { TimeService } from 'src/app/services/time.service';
@Component({
  selector: 'app-card-notification',
  templateUrl: './card-notification.component.html',
  styleUrls: ['./card-notification.component.css']
})
export class CardNotificationComponent implements OnInit {
  @Input() value: IBellNotification;
  @Input() length: number;
  @Input() index: number;
  @Output() onAck = new EventEmitter<IBellNotification>();
  parsedDate: string;
  bypassDefaultNotificationList: string[] = [
    'RAW_ACCESS_RECORD_PROCESSING_WARNING',
    'REJECTED_MSG_EVENT',
    'UNROUTABLE_MSG_EVENT',
    'SYSTEM_VALIDATION_FAILED',
    'NEW_PROJECT_CREATED',
    'JOB_THREAD_STARVING',
    'JOB_STARVING',
  ];
  constructor(public translate: TranslateService, private time: TimeService) {
  }
  ngOnInit(): void {
    this.parsedDate = this.time.getUserFriendlyTimeDisplay(this.value.date)
  }
  dismissMsg(event: Event) {
    event.stopPropagation();
    this.onAck.emit(this.value)
  }
}
