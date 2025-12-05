import { MediaMatcher } from '@angular/cdk/layout';
import { ChangeDetectorRef, Component, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatSidenav } from '@angular/material/sidenav';
import { ActivatedRoute } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { Subscription } from 'rxjs';
import { map } from 'rxjs/operators';
import { IOption } from 'src/app/misc/interface';
import { Logger } from 'src/app/misc/logger';
import { Utility } from 'src/app/misc/utility';
import { NewProjectComponent } from 'src/app/components/new-project/new-project.component';
import { AuthService } from 'src/app/services/auth.service';
import { DeviceService } from 'src/app/services/device.service';
import { HttpProxyService } from 'src/app/services/http-proxy.service';
import { IBellNotification, MessageService } from 'src/app/services/message.service';
import { ProjectService } from 'src/app/services/project.service';
import { RouterWrapperService } from 'src/app/services/router-wrapper';
import { UserMessageService } from 'src/app/services/user-message.service';
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
  menuMgmt: INavElement[] = [
    {
      link: 'mgmt/insights',
      display: 'DASHBOARD',
      icon: 'dashboard',
      params: {
      },
    },
    {
      link: 'mgmt/jobs',
      display: 'JOB_STATUS',
      icon: 'work_history',
      params: {
      },
    },
    {
      link: 'mgmt/projects',
      display: 'PROJECT_DASHBOARD',
      icon: 'blur_on',
      params: {
      },
    },
    {
      link: 'mgmt/clients',
      display: 'CLIENT_DASHBOARD',
      icon: 'apps',
      params: {
      },
    },
    {
      link: 'mgmt/endpoints',
      display: 'SECURITY_PROFILE_DASHBOARD',
      icon: 'mediation',
      params: {
      },
    },
    {
      link: 'mgmt/users',
      display: 'USER_DASHBOARD',
      icon: 'people',
      params: {
      },
    },
    {
      link: 'mgmt/tokens',
      display: 'REVOKE_TOKEN_DASHBOARD',
      icon: 'stars',
      params: {
      },
    },
    {
      link: 'mgmt/proxy-cache',
      display: 'CACHE_DASHBOARD',
      icon: 'cached',
      params: {
      },
    },
    {
      link: 'mgmt/events',
      display: 'EVENT_DASHBOARD',
      icon: 'event_available',
      params: {
      },
    },
    {
      link: 'mgmt/notification',
      display: 'SYSTEM_MESSAGE_DASHBOARD',
      icon: 'email',
      params: {
      },
    },

  ];
  epPermissions = ['API_MGMT']
  cachePermissions = ['API_MGMT']
  corsPermissions = ['API_MGMT']
  clientPermissions = ['CLIENT_MGMT']
  projectPermissions = ['PROJECT_INFO_MGMT']
  userPermissions = ['USER_MGMT']
  rolePermissions = ['ROLE_MGMT']
  adminPermissions = ['PROJECT_INFO_MGMT']
  menuEp: INavElement[] = [
    {
      link: 'market',
      display: 'API_CENTER',
      icon: 'store',
      params: {
      },
    },
    {
      link: 'requests',
      display: 'MY_SUB_REQUEST',
      icon: 'shopping_cart',
      params: {
      },
    },
    {
      link: 'approval',
      display: 'PENDING_SUB_REQUEST',
      icon: 'checklist',
      params: {
      },
    },
    {
      link: 'subscriptions',
      display: 'MY_SUBS',
      icon: 'subscriptions',
      params: {
      },
    },
  ];
  private _mobileQueryListener: () => void;
  switchProjectForm = new FormGroup({
    viewTenantId: new FormControl('', [])
  });
  @ViewChild("snav", { static: true }) snav: MatSidenav;
  name: string;
  constructor(
    public projectSvc: ProjectService,
    public authSvc: AuthService,
    public httpProxySvc: HttpProxyService,
    changeDetectorRef: ChangeDetectorRef,
    media: MediaMatcher,
    public route: ActivatedRoute,
    public router: RouterWrapperService,
    public translate: TranslateService,
    public msgSvc: MessageService,
    public userMsgSvc: UserMessageService,
    public deviceSvc: DeviceService,
    public dialog: MatDialog
  ) {
    this.mobileQuery = media.matchMedia('(max-width: 600px)');
    this._mobileQueryListener = () => changeDetectorRef.detectChanges();
    this.mobileQuery.addListener(this._mobileQueryListener);
  }
  openedHandler(panelName: string) {
    localStorage.setItem(panelName, 'true')
  }

  closedHander(panelName: string) {
    localStorage.removeItem(panelName)
  }
  navExpand(panelName: string) {
    return localStorage.getItem(panelName) === 'true'
  }
  openedSideBar() {
    localStorage.removeItem('close_side_bar')
  }

  closedSideBar() {
    localStorage.setItem('close_side_bar', 'true')
  }
  navSideBar() {
    return localStorage.getItem('close_side_bar') !== 'true'
  }
  ngOnDestroy(): void {
    this.mobileQuery.removeListener(this._mobileQueryListener);
    this.sub.unsubscribe()
  }
  avatar: string | ArrayBuffer;
  sub: Subscription;
  totalProjectsOptions: IOption[] = [];
  ngOnInit() {
    this.projectSvc.findTenantProjects(0, 40).subscribe(next => {
      this.projectSvc.totalProjects = next.data;
      if (this.projectSvc.totalProjects && this.projectSvc.totalProjects.length > 0) {
        Logger.trace("view tenant id {}", this.httpProxySvc.currentUserAuthInfo.viewTenantId)
        this.projectSvc.viewProject = next.data.filter(e => e.id === this.httpProxySvc.currentUserAuthInfo.viewTenantId)[0];
        Logger.debug("view project is {}", this.projectSvc.viewProject)
        this.switchProjectForm.get('viewTenantId').setValue(this.projectSvc.viewProject.id, { emitEvent: false })
        this.totalProjectsOptions = this.projectSvc.totalProjects.map(e => { return { value: e.id, label: e.name } as IOption })

        this.projectSvc.findUiPermission(this.projectSvc.viewProject.id).subscribe(next => {
          this.projectSvc.permissionDetail.next(next);
        })
        if (this.hasAuth()) {
          this.msgSvc.connectToMonitor();
          this.msgSvc.pullUnAckMessage()
        }
      }
    })
    this.userMsgSvc.connectToMonitor();
    this.userMsgSvc.pullUnAckMessage()

    this.httpProxySvc.getMyProfile().subscribe(next => {
      this.name = next.username || next.email || next.mobileNumber
    })
    this.deviceSvc.profileUpdated$.subscribe((() => {
      this.getDisplayName()
    }))
    this.deviceSvc.avatarUpdated$.subscribe((() => {
      this.getAvatar()
    }))
    this.getDisplayName()
    this.getAvatar()
    this.switchProjectForm.get('viewTenantId').valueChanges.subscribe(next => {
      Logger.debug("next view tenant id {}", next)
      this.httpProxySvc.refreshToken(next).subscribe(newToken => {
        this.httpProxySvc.currentUserAuthInfo = newToken;
        this.projectSvc.viewProject = this.projectSvc.totalProjects.filter(e => e.id === this.httpProxySvc.currentUserAuthInfo.viewTenantId)[0];
        this.projectSvc.resetPermissionDetails();
        this.projectSvc.findUiPermission(next).subscribe(next => {
          this.projectSvc.permissionDetail.next(next);
          Logger.debug("view project {}", this.projectSvc.viewProject)
          this.router.navProjectHome();//avoid blank view when previous tenant project open
        })
      })
    })
  }
  getAvatar() {
    this.httpProxySvc.getAvatar().subscribe(blob => {
      Utility.createImageFromBlob(blob, (reader) => {
        this.avatar = reader.result
      })
    })
  }
  getDisplayName() {
    this.httpProxySvc.getMyProfile().subscribe(next => {
      this.name = next.username || next.email || next.mobileNumber
    })
  }
  getPermissionId(name: string[]) {
    return this.projectSvc.permissionDetail.pipe(map(_ => _.permissionInfo.filter(e => name.includes(e.name)).map(e => e.id)))
  }
  doLogout() {
    Utility.logout(undefined, this.httpProxySvc)
  }
  preserveURLQueryParams(input: INavElement) {
    const var0 = this.route.snapshot.queryParams;
    if (this.router.getUrl().includes(input.link)) {
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
    return this.projectSvc.containMainProject()
  }
  hasTenants() {
    return this.projectSvc.hasTenantProjects()
  }
  filterDuplicate(msgs: IBellNotification[]) {
    return msgs.filter((e, i) => msgs.findIndex(ee => ee.id === e.id) === i)
  }
  bellCount(msgs: IBellNotification[]) {
    const count = msgs.filter((e, i) => msgs.findIndex(ee => ee.id === e.id) === i).length
    return count > 99 ? '99+' : new String(count);
  }
  firstLetter(name: string) {
    if (!name) {
      return '';
    }
    return name.substring(0, 1).toUpperCase()
  }
  openNewProject() {
    this.dialog.open(NewProjectComponent, { data: {} });
  }
}
