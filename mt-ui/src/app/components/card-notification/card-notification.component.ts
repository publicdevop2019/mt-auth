import { Component, Input, OnChanges, OnInit, Output, SimpleChanges } from '@angular/core';
import { IBellNotification, MessageService } from 'src/app/services/message.service';
import { DateTime, ToRelativeUnit } from "luxon";
import { TranslateService } from '@ngx-translate/core';
@Component({
  selector: 'app-card-notification',
  templateUrl: './card-notification.component.html',
  styleUrls: ['./card-notification.component.css']
})
export class CardNotificationComponent implements OnInit {
  @Input() value: IBellNotification;
  @Input() length: number;
  @Input() index: number;
  parsedDate: string;
  constructor(public translate: TranslateService, private msgSvg: MessageService) {
  }
  ngOnInit(): void {
    let resolved: string;
    if (this.translate.currentLang === 'zhHans') {
      resolved = 'zh-Hans'
    } else {
      resolved = 'en-Us'
    }
    let resolvedUnit: ToRelativeUnit = 'seconds';
    if (DateTime.fromMillis(this.value.date).diffNow('seconds').seconds < -60) {
      resolvedUnit = 'minutes'
    }
    if (DateTime.fromMillis(this.value.date).diffNow('minutes').minutes < -60) {
      resolvedUnit = 'hours'
    }
    if (DateTime.fromMillis(this.value.date).diffNow('hours').hours < -24) {
      resolvedUnit = 'days'
    }
    if (DateTime.fromMillis(this.value.date).diffNow('days').days < -30) {
      resolvedUnit = 'months'
    }
    if (DateTime.fromMillis(this.value.date).diffNow('months').months < -12) {
      resolvedUnit = 'years'
    }
    const parsed = DateTime.fromMillis(this.value.date).setLocale(resolved).toRelativeCalendar({ unit: resolvedUnit });
    this.parsedDate = parsed;
  }
  dismissMsg(event: Event) {
    event.stopPropagation();
    this.msgSvg.dismiss(this.value)
  }
}
