import { Component, Input, OnChanges, OnInit, Output, SimpleChanges } from '@angular/core';
import { INotification } from 'src/app/services/message.service';
import { DateTime } from "luxon";
import { TranslateService } from '@ngx-translate/core';
@Component({
  selector: 'app-card-notification',
  templateUrl: './card-notification.component.html',
  styleUrls: ['./card-notification.component.css']
})
export class CardNotificationComponent implements OnInit {
  @Input() value: INotification;
  parsedDate: string;
  constructor(public translate: TranslateService) {
  }
  ngOnInit(): void {
    let resolved: string;
    if (this.translate.currentLang === 'zhHans') {
      resolved = 'zh-Hans'
    } else {
      resolved = 'en-Us'
    }
    let resolvedUnit: 'seconds' | 'minutes' = 'seconds';
    if (DateTime.fromMillis(this.value.date).diffNow('seconds').seconds < -60) {
      resolvedUnit = 'minutes'
    }
    const parsed = DateTime.fromMillis(this.value.date).setLocale(resolved).toRelativeCalendar({ unit: resolvedUnit });
    this.parsedDate = parsed;
  }
}
