import { Component, OnInit } from '@angular/core';
import { AuthService } from 'src/app/services/auth.service';
import { DeviceService } from 'src/app/services/device.service';
import { LanguageService } from 'src/app/services/language.service';

@Component({
  selector: 'app-setting',
  templateUrl: './setting.component.html',
  styleUrls: ['./setting.component.css']
})
export class SettingComponent implements OnInit {

  constructor(
    public langSvc: LanguageService, 
    public authSvc: AuthService,
    public deviceSvc: DeviceService,
  ) { 
    this.deviceSvc.updateDocTitle('SETTING_DOC_TITLE')
  }

  ngOnInit() {
  }

}
