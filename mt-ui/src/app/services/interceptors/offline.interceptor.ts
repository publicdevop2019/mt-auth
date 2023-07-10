import { HttpEvent, HttpHandler, HttpHeaders, HttpInterceptor, HttpRequest, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { delay } from 'rxjs/operators';
import { environment } from 'src/environments/environment';
import { IAuthorizeCode } from '../../clazz/validation/common.interface';
/**
 * use refresh token if call failed
 */
@Injectable()
export class OfflineInterceptor implements HttpInterceptor {
  private DEFAULT_DELAY = 1000;
  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    if (environment.mode === 'offline') {
      if (['delete', 'put', 'post', 'patch'].includes(req.method.toLowerCase())) {
        console.debug('[DEV ONLY] request body')
        console.debug(req.body)
        if (req.url.includes('/authorize'))
          return of(new HttpResponse({ status: 200, body: { authorize_code: 'dummyCode' } as IAuthorizeCode })).pipe(delay(this.DEFAULT_DELAY))
        if (req.url.includes('/oauth/token')) {
          const mockedToken = {
            access_token: 'mockTokenString',
            refresh_token: 'mockTokenString2'
          };
          return of(new HttpResponse({ status: 200, body: mockedToken })).pipe(delay(this.DEFAULT_DELAY));
        }
        if (req.url.includes('/file')) {
          let header = new HttpHeaders();
          header = header.set('location', 'https://img.alicdn.com/imgextra/i3/3191337305/O1CN01X11Ad123pjrtuTsDT_!!3191337305-0-lubanu-s.jpg_430x430q90.jpg')
          return of(new HttpResponse({ status: 200, headers: header })).pipe(delay(this.DEFAULT_DELAY));
        }
        return of(new HttpResponse({ status: 200 })).pipe(delay(this.DEFAULT_DELAY));
      }
      if (['get'].includes(req.method.toLowerCase())) {
        // if (req.url.includes('revoke-tokens/root')) {
        //   return of(new HttpResponse({ status: 200, body: mockRevokeTokens })).pipe(delay(this.DEFAULT_DELAY))
        // }
      }
    }
    return next.handle(req);
  }
}