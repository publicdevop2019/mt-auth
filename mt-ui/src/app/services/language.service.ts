import { Injectable } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
@Injectable({
    providedIn: 'root'
})
export class LanguageService {
    public get storedLang() {
        return localStorage.getItem('storedLang') || 'zhHans';
    }
    public set storedLang(next: string) {
        localStorage.setItem('storedLang', next)
    }
    constructor(public translate: TranslateService) {
        
    }
    public initLangConfig(){
        this.translate.setDefaultLang(this.storedLang);
        this.translate.use(this.storedLang)
    }
    public toggleLang() {
        if (this.translate.currentLang === 'enUS') {
            this.translate.use('zhHans')
            this.storedLang='zhHans'
            this.translate.get('DOCUMENT_TITLE').subscribe(
                next => {
                    document.title = next
                    document.documentElement.lang = 'zh-Hans'
                }
            )
        }
        else {
            this.translate.use('enUS')
            this.storedLang='enUS'
            this.translate.get('DOCUMENT_TITLE').subscribe(
                next => {
                    document.title = next
                    document.documentElement.lang = 'en'
                }
            )
        }
    }
    public currentLanguage() {
        return this.translate.currentLang;
    }
}
