import { AfterContentChecked, ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { HttpProxyService } from './services/http-proxy.service';
import { MessageService } from './services/message.service';
@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements AfterContentChecked, OnInit {
  title = 'OAuth2-Manager';
  constructor(public httpProxy: HttpProxyService, private changeDec: ChangeDetectorRef, public translate: TranslateService, private msgSvc: MessageService,private router:Router) {
    this.translate.setDefaultLang('zhHans');
    this.translate.use('zhHans')
  }
  ngAfterContentChecked(): void {
    this.changeDec.detectChanges()
  }
  ngOnInit(): void {
  }

}
