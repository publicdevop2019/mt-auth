import { Component, OnInit } from '@angular/core';
import { DeviceService } from 'src/app/services/device.service';

@Component({
  selector: 'app-not-found',
  templateUrl: './not-found.component.html',
  styleUrls: ['./not-found.component.css']
})
export class NotFoundComponent implements OnInit {

  constructor(
    public deviceSvc: DeviceService,
  ) {
    this.deviceSvc.updateDocTitle('NOT_FOUND_DOC_TITLE')
   }

  ngOnInit(): void {
  }

}
