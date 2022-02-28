import { Component, Input, OnChanges, OnInit, Output, SimpleChanges } from '@angular/core';
import { INotification } from 'src/app/services/message.service';

@Component({
  selector: 'app-card-notification',
  templateUrl: './card-notification.component.html',
  styleUrls: ['./card-notification.component.css']
})
export class CardNotificationComponent implements OnInit {
  @Input() value: INotification;
  constructor() {
  }
  ngOnInit(): void {
  }
}
