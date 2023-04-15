import { AfterContentChecked, ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { HttpProxyService } from './services/http-proxy.service';
import { LanguageService } from './services/language.service';
@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements AfterContentChecked, OnInit {
  constructor(public httpProxy: HttpProxyService, private changeDec: ChangeDetectorRef, public langSvc: LanguageService) {
    this.langSvc.initLangConfig();
  }
  ngAfterContentChecked(): void {
    this.changeDec.detectChanges()
  }
  ngOnInit(): void {
  }

}
