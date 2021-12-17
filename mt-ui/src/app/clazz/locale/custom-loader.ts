import { TranslateLoader } from '@ngx-translate/core';
import { Observable, of } from 'rxjs';
import { enUS } from './en-US';
import { zhHans } from './zh-Hans';
export class CustomLoader implements TranslateLoader {
    getTranslation(lang: string): Observable<any> {
        if (lang === 'zhHans')
            return of(zhHans);
        if (lang === 'enUS')
            return of(enUS);
    }
}