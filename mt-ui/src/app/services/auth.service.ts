import { Injectable } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { Router, CanActivateChild, CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Params } from '@angular/router';
import { Observable, Subject } from 'rxjs';
import { map, shareReplay } from 'rxjs/operators';
import { HttpProxyService, IUser } from './http-proxy.service';

@Injectable({
  providedIn: 'root'
})
export class AuthService implements CanActivateChild, CanActivate {
  public avatarUpdated$: Subject<void> = new Subject();
  private currentUser$: Observable<IUser>;
  public loginFormValue: FormGroup;
  public loginNextUrl: string;
  public mfaId: string;
  public get advancedMode() {
    return localStorage.getItem('advancedMode') === 'true'
  }
  public set advancedMode(next: boolean) {
    localStorage.setItem('advancedMode', next + '')
  }
  public get currentUser() {
    if (!this.currentUser$) {
      this.currentUser$ = this.httpProxy.getMyProfile().pipe(
        map(e => e),
        shareReplay(1)
      );
    }
    return this.currentUser$;
  };
  constructor(private router: Router, private httpProxy: HttpProxyService) { }
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
        this.router.navigate(['/login'], { queryParams: route.queryParams });
      } else {
        /**
         * ignore all qury param if not authorization 
         */
        this.router.navigateByUrl('/login');
      }
      return false;
    } else {
      return true;
    }

  }
}
