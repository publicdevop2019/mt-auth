import { Component, OnInit } from '@angular/core';
import { DeviceService } from 'src/app/services/device.service';
@Component({
  selector: 'app-object-detail',
  templateUrl: './object-detail.component.html',
  styleUrls: ['./object-detail.component.css']
})
export class ObjectDetailComponent implements OnInit {
  constructor(private deviceSvc: DeviceService) {
  }
  ngOnInit(): void {
  }
  printJson() {
    let obj = JSON.parse(JSON.stringify(this.deviceSvc.overlayData));
    // let obj = JSON.parse(this.overlaySvc.data);
    if (obj.eventBody) {
      obj.eventBody = JSON.parse(obj.eventBody)
    }
    return JSON.stringify(obj, null, 4)
  }
}
