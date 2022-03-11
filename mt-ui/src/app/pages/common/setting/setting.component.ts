import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-setting',
  templateUrl: './setting.component.html',
  styleUrls: ['./setting.component.css']
})
export class SettingComponent implements OnInit {

  constructor(public translate: TranslateService) { }

  ngOnInit() {
  }
  public toggleLang() {
    if (this.translate.currentLang === 'enUS') {
      this.translate.use('zhHans')
      this.translate.get('DOCUMENT_TITLE').subscribe(
        next => {
          document.title = next
          document.documentElement.lang = 'zh-Hans'
        }
      )
    }
    else {
      this.translate.use('enUS')
      this.translate.get('DOCUMENT_TITLE').subscribe(
        next => {
          document.title = next
          document.documentElement.lang = 'en'
        }
      )
    }
  }

}
