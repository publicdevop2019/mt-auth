import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { AuthService } from 'src/app/services/auth.service';
import { LanguageService } from 'src/app/services/language.service';

@Component({
  selector: 'app-setting',
  templateUrl: './setting.component.html',
  styleUrls: ['./setting.component.css']
})
export class SettingComponent implements OnInit {

  constructor(public langSvc: LanguageService, public authSvc: AuthService) { }

  ngOnInit() {
  }
  public toggleMode() {
    this.authSvc.advancedMode = !this.authSvc.advancedMode;
  }
  public isAdvancedMode() {
    return this.authSvc.advancedMode;
  }

}
