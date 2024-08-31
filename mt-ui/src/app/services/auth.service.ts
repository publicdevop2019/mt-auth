import { Injectable } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { CanActivateChild, CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { Observable, Subject } from 'rxjs';
import { map, shareReplay } from 'rxjs/operators';
import { HttpProxyService, IUser } from './http-proxy.service';
import { RouterWrapperService } from './router-wrapper';
import { Logger } from '../misc/logger';
import { IMfaResponse } from '../misc/interface';

@Injectable({
  providedIn: 'root'
})
export class AuthService implements CanActivateChild, CanActivate {
  public loginFormValue: FormGroup;
  public loginNextUrl: string;
  public mfaResponse: IMfaResponse;
  constructor(private router: RouterWrapperService, private httpProxy: HttpProxyService) { }
  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {
    return this.defaultCanActivate(route)
  }
  canActivateChild(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {
    return this.defaultCanActivate(route)
  }
  private defaultCanActivate(route: ActivatedRouteSnapshot): boolean {
    if (this.httpProxy.currentUserAuthInfo === undefined || this.httpProxy.currentUserAuthInfo === null) {
      console.error('no authentication found! redirect to login page')
      if (route.routeConfig.path === 'authorize' && Object.keys(route.queryParams).length != 0) {
        this.router.navLogin({ queryParams: route.queryParams });
      } else {
        this.router.navLoginNoParam();
      }
      return false;
    } else {
      return true;
    }

  }
}
