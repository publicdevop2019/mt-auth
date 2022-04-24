import { MediaMatcher } from '@angular/cdk/layout';
import { ChangeDetectorRef, Component, OnInit, ViewChild } from '@angular/core';
import { MatSidenav } from '@angular/material/sidenav';
import { ActivatedRoute, Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { map } from 'rxjs/operators';
import { logout } from 'src/app/clazz/utility';
import { AuthService } from 'src/app/services/auth.service';
import { DeviceService } from 'src/app/services/device.service';
import { HttpProxyService, IUser } from 'src/app/services/http-proxy.service';
import { MessageService } from 'src/app/services/message.service';
import { IProjectPermission, IProjectPermissionInfo, ProjectService } from 'src/app/services/project.service';
export interface INavElement {
  link: string;
  icon?: string;
  display: string;
  params: any
  authName?: string[]
}
@Component({
  selector: 'app-nav-bar',
  templateUrl: './nav-bar.component.html',
  styleUrls: ['./nav-bar.component.css']
})
export class NavBarComponent implements OnInit {
  menuOpen: boolean = false;
  mobileQuery: MediaQueryList;
  menuAuthMangement: INavElement[] = [
    {
      link: 'registry',
      display: 'REGISTRY_STATUS',
      icon: 'receipt',
      params: {
      },
    },
    {
      link: 'jobs',
      display: 'JOB_STATUS',
      icon: 'work_history',
      params: {
      },
    },
    {
      link: 'projects',
      display: 'PROJECT_DASHBOARD',
      icon: 'blur_on',
      params: {
      },
    },
    {
      link: 'clients',
      display: 'CLIENT_DASHBOARD',
      icon: 'apps',
      params: {
      },
    },
    {
      link: 'api-profiles',
      display: 'SECURITY_PROFILE_DASHBOARD',
      icon: 'mediation',
      params: {
      },
    },
    {
      link: 'resource-owners',
      display: 'RESOURCE_OWNER_DASHBOARD',
      icon: 'people',
      params: {
      },
    },
    {
      link: 'cache-profiles',
      display: 'API_CACHE_DASHBOARD',
      icon: 'local_offer',
      params: {
      },
    },
    {
      link: 'cors-profiles',
      display: 'CORS_DASHBOARD',
      icon: 'share',
      params: {
      },
    },
    {
      link: 'revoke-token',
      display: 'REVOKE_TOKEN_DASHBOARD',
      icon: 'stars',
      params: {
      },
    },
    {
      link: 'cache-mngr',
      display: 'CACHE_DASHBOARD',
      icon: 'cached',
      params: {
      },
    },
    {
      link: 'events-access',
      display: 'EVENT_DASHBOARD',
      icon: 'event_available',
      params: {
      },
    },
    {
      link: 'message-center',
      display: 'MESSAGE_DASHBOARD',
      icon: 'message',
      params: {
      },
    },
  ];
  menuAuth: INavElement[] = [
    {
      link: 'my-project',
      display: 'MY_PROJECT',
      icon: 'blur_on',
      authName: ['VIEW_PROJECT_INFO', 'EDIT_PROJECT_INFO'],
      params: {
      },
    },
    {
      link: 'my-client',
      display: 'MY_CLIENTS',
      icon: 'apps',
      authName: ['CREATE_CLIENT', 'EDIT_CLIENT', 'VIEW_CLIENT'],
      params: {
      },
    },
    {
      link: 'my-api',
      display: 'MY_API',
      icon: 'mediation',
      authName: ['CREATE_API', 'EDIT_API', 'VIEW_API'],
      params: {
      },
    },
    {
      link: 'my-permission',
      display: 'MY_PERMISSION_DASHBOARD',
      icon: 'policy',
      authName: ['CREATE_PERMISSION', 'EDIT_PERMISSION', 'VIEW_PERMISSION'],
      params: {
      },
    },
    {
      link: 'my-role',
      display: 'MY_ROLE_DASHBOARD',
      icon: 'person',
      authName: ['CREATE_ROLE', 'EDIT_ROLE', 'VIEW_ROLE'],
      params: {
      },
    },
    {
      link: 'my-user',
      display: 'MY_USER_DASHBOARD',
      icon: 'people',
      authName: ['EDIT_TENANT_USER', 'VIEW_TENANT_USER'],
      params: {
      },
    },
  ];
  menuMisc: INavElement[] = [
    {
      link: 'updatePwd',
      display: 'UPDATE_PASSWORD',
      icon: 'vpn_key',
      params: {
      },
    },
    {
      link: 'settings',
      display: 'SYSTEM_SETTINGS',
      icon: 'settings',
      params: {
      },
    },
  ];
  menuTop: INavElement[] = [
    {
      link: 'welcome',
      display: 'WELCOME',
      icon: 'dashboard',
      params: {
      },
    },
    {
      link: 'new-project',
      display: 'REGISTER_MY_PROJECT',
      icon: 'blur_on',
      params: {
      },
    },
    {
      link: 'my-profile',
      display: 'MY_PROFILE',
      icon: 'account_circle',
      params: {
      },
    },
    {
      link: 'api-center',
      display: 'API_CENTER',
      icon: 'mediation',
      params: {
      },
    },
  ];
  private _mobileQueryListener: () => void;
  @ViewChild("snav", { static: true }) snav: MatSidenav;
  constructor(
    public projectSvc: ProjectService,
    public authSvc: AuthService,
    public httpProxySvc: HttpProxyService,
    changeDetectorRef: ChangeDetectorRef,
    media: MediaMatcher,
    public route: ActivatedRoute,
    public router: Router,
    public translate: TranslateService,
    public deviceSvc: DeviceService,
    public msgSvc: MessageService
  ) {
    this.mobileQuery = media.matchMedia('(max-width: 600px)');
    this._mobileQueryListener = () => changeDetectorRef.detectChanges();
    this.mobileQuery.addListener(this._mobileQueryListener);
  }
  openedHandler(panelName: string) {
    localStorage.setItem(panelName, 'true')
  }
  closedHander(panelName: string) {
    localStorage.setItem(panelName, 'false')
  }
  navExpand(panelName: string) {
    return localStorage.getItem(panelName) === 'true'
  }
  ngOnDestroy(): void {
    this.mobileQuery.removeListener(this._mobileQueryListener);
  }
  ngOnInit() {
    this.projectSvc.findTenantProjects(0, 40).subscribe(next => {
      this.projectSvc.totalProjects = next.data;
    })
    this.projectSvc.findUIPermission().subscribe(next => {
      this.projectSvc.permissionDetail.next(next.projectPermissionInfo);
    })
    this.msgSvc.connectToMonitor();
  }
  getPermissionId(projectId: string, name: string[]) {
    return this.projectSvc.permissionDetail.pipe(map(_ => _.find(e => e.projectId === projectId)?.permissionInfo.filter(e => name.includes(e.name)).map(e => e.id)))
  }
  doLogout() {
    logout()
  }
  preserveURLQueryParams(input: INavElement) {
    const var0 = this.route.snapshot.queryParams;
    if (this.router.url.includes(input.link)) {
      return {
        ...input.params,
        ...var0
      }
    } else {
      return {
        ...input.params,
      }
    }
  }
  hasAuth() {
    return !!this.projectSvc.totalProjects.find(e => e.id === '0P8HE307W6IO')
  }
  getName() {
    return this.authSvc.currentUser.pipe(map(next => {
      return next.username || next.email
    }))
  }
}
