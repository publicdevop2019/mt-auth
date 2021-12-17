import { Component, OnInit } from '@angular/core';
import { OverlayService } from 'src/app/services/overlay.service';
@Component({
  selector: 'app-object-detail',
  templateUrl: './object-detail.component.html',
  styleUrls: ['./object-detail.component.css']
})
export class ObjectDetailComponent implements OnInit {
  constructor(private overlaySvc: OverlayService) {
  }
  ngOnInit(): void {
  }
  printJson() {
    let obj = JSON.parse(JSON.stringify(this.overlaySvc.data));
    // let obj = JSON.parse(this.overlaySvc.data);
    if (obj.eventBody) {
      obj.eventBody = JSON.parse(obj.eventBody)
    }
    return JSON.stringify(obj, null, 4)
  }
}
