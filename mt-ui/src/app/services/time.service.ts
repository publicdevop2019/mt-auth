import { Injectable } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { DateTime, ToRelativeUnit } from "luxon";
import { LanguageService } from './language.service';
@Injectable({
    providedIn: 'root'
})
export class TimeService {
    constructor(private langSvc: LanguageService) {
    }
    public getUserFriendlyTimeDisplay(date: number): string {
        let resolved: string;
        if (this.langSvc.currentLanguage() === 'zhHans') {
            resolved = 'zh-Hans'
        } else {
            resolved = 'en-Us'
        }
        let resolvedUnit: ToRelativeUnit = 'seconds';
        if (DateTime.fromMillis(date).diffNow('seconds').seconds < -60) {
            resolvedUnit = 'minutes'
        }
        if (DateTime.fromMillis(date).diffNow('minutes').minutes < -60) {
            resolvedUnit = 'hours'
        }
        if (DateTime.fromMillis(date).diffNow('hours').hours < -24) {
            resolvedUnit = 'days'
        }
        if (DateTime.fromMillis(date).diffNow('days').days < -30) {
            resolvedUnit = 'months'
        }
        if (DateTime.fromMillis(date).diffNow('months').months < -12) {
            resolvedUnit = 'years'
        }
        return DateTime.fromMillis(date).setLocale(resolved).toRelativeCalendar({ unit: resolvedUnit });
    }
}
