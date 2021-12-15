import { Component, Input, OnChanges, OnInit, Output, SimpleChanges } from '@angular/core';

@Component({
  selector: 'app-card-notification',
  templateUrl: './card-notification.component.html',
  styleUrls: ['./card-notification.component.css']
})
export class CardNotificationComponent implements OnInit {
  @Input() value: string = '';
  public name: string;
  public time: number;
  public orderId: string;
  public skuId: string;
  public template: string='';
  public dtxId: string='';

  constructor() {
  }
  ngOnInit(): void {
    if (this.value) {
      const parsed = JSON.parse(this.value)
      this.time = parsed.date;
      this.name = parsed.name;
      if (parsed.detail.SKU_CHANGE_DETAIL) {
        this.template = 'sku'
        const str:string=JSON.parse(parsed.detail.SKU_CHANGE_DETAIL)[0].path;
        this.skuId = str.split('/')[1];
      } else if (parsed.detail.STACK_TRACE) {
        this.template = 'order'
        this.orderId = parsed.orderId
      } else if (parsed.detail.DTX) {
        this.template = 'dtxFailed'
        this.dtxId = parsed.detail.DTX
      } else {
      }
    }
  }
}
