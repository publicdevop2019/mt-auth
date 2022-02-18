import { HttpEvent, HttpHandler, HttpHeaders, HttpInterceptor, HttpRequest, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { delay } from 'rxjs/operators';
import { mockClient1 } from 'src/app/mocks/mock-client';
import { mockClient } from 'src/app/mocks/mock-clients';
import { mockSP1 } from 'src/app/mocks/mock-endpoint';
import { mockSP } from 'src/app/mocks/mock-endpoints';
import { mockRevokeTokens } from 'src/app/mocks/mock-revoke-tokens';
import { mockResource1 } from 'src/app/mocks/mock-user';
import { mockResourceO } from 'src/app/mocks/mock-users';
import { environment } from 'src/environments/environment';
import { IAuthorizeCode } from '../../clazz/validation/interfaze-common';
import { mockMessage } from '../../mocks/mock-message';
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
        if (req.url.includes('revoke-tokens/root')) {
          return of(new HttpResponse({ status: 200, body: mockRevokeTokens })).pipe(delay(this.DEFAULT_DELAY))
        }
        if (req.url.includes('/endpoints/root/')) {
          return of(new HttpResponse({ status: 200, body: mockSP1 })).pipe(delay(this.DEFAULT_DELAY))
        }
        if (req.url.includes('/endpoints/root')) {
          return of(new HttpResponse({ status: 200, body: mockSP })).pipe(delay(this.DEFAULT_DELAY))
        }
        if (req.url.includes('/systemNotifications/root')) {
          return of(new HttpResponse({ status: 200, body: mockMessage })).pipe(delay(this.DEFAULT_DELAY))
        }
        if (req.url.includes('clients/root/')) {
          return of(new HttpResponse({ status: 200, body: mockClient1 })).pipe(delay(this.DEFAULT_DELAY))
        }
        if (req.url.includes('clients/root')) {
          return of(new HttpResponse({ status: 200, body: mockClient })).pipe(delay(this.DEFAULT_DELAY))
        }
        if (req.url.includes('users/admin/')) {
          return of(new HttpResponse({ status: 200, body: mockResource1 })).pipe(delay(this.DEFAULT_DELAY))
        }
        if (req.url.includes('users/admin')) {
          return of(new HttpResponse({ status: 200, body: mockResourceO })).pipe(delay(this.DEFAULT_DELAY))
        }
      }
    }
    return next.handle(req);
  }
}