import { MediaMatcher } from '@angular/cdk/layout';
import { ChangeDetectorRef, Component, OnInit, ViewChild } from '@angular/core';
import { MatSidenav } from '@angular/material/sidenav';
import { ActivatedRoute, Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { logout } from 'src/app/clazz/utility';
import { AuthService } from 'src/app/services/auth.service';
import { DeviceService } from 'src/app/services/device.service';
import { HttpProxyService, IUser } from 'src/app/services/http-proxy.service';
import { MessageService } from 'src/app/services/message.service';
import { ProjectService } from 'src/app/services/project.service';
export interface INavElement {
  link: string;
  icon?: string;
  display: string;
  params: any
}

@Component({
  selector: 'app-nav-bar',
  templateUrl: './nav-bar.component.html',
  styleUrls: ['./nav-bar.component.css']
})
export class NavBarComponent implements OnInit {
  msgDetails: boolean = false;
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
    // {
    //   link: 'role-profiles',
    //   display: 'ROLE_DASHBOARD',
    //   icon: 'person',
    //   params: {
    //   },
    // },
    // {
    //   link: 'org-profiles',
    //   display: 'ORG_DASHBOARD',
    //   icon: 'corporate_fare',
    //   params: {
    //   },
    // },
    // {
    //   link: 'permission-profiles',
    //   display: 'PERMISSION_DASHBOARD',
    //   icon: 'policy',
    //   params: {
    //   },
    // },
    // {
    //   link: 'position-profiles',
    //   display: 'POSITION_DASHBOARD',
    //   icon: 'work',
    //   params: {
    //   },
    // },
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
      params: {
      },
    },
    {
      link: 'my-client',
      display: 'MY_CLIENTS',
      icon: 'apps',
      params: {
      },
    },
    {
      link: 'my-api',
      display: 'MY_API',
      icon: 'mediation',
      params: {
      },
    },
    {
      link: 'my-permission',
      display: 'MY_PERMISSION_DASHBOARD',
      icon: 'policy',
      params: {
      },
    },
    {
      link: 'my-role',
      display: 'MY_ROLE_DASHBOARD',
      icon: 'person',
      params: {
      },
    },
    // {
    //   link: 'my-org',
    //   display: 'MY_ORG_DASHBOARD',
    //   icon: 'corporate_fare',
    //   params: {
    //   },
    // },
    // {
    //   link: 'my-position',
    //   display: 'MY_POSITION_DASHBOARD',
    //   icon: 'work',
    //   params: {
    //   },
    // },
    {
      link: 'my-user',
      display: 'MY_USER_DASHBOARD',
      icon: 'people',
      params: {
      },
    },
    // {
    //   link: 'add-admin',
    //   display: 'ADD_ADMIN',
    //   icon: 'admin_panel_settings',
    //   params: {
    //   },
    // },
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
    this.httpProxySvc.getMyProfile().subscribe(next => this.authSvc.currentUser = next)
    this.msgSvc.connectToMonitor();
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
}
