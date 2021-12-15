import { MediaMatcher } from '@angular/cdk/layout';
import { ChangeDetectorRef, Component, OnInit, ViewChild } from '@angular/core';
import { MatSidenav } from '@angular/material/sidenav';
import { ActivatedRoute, Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { logout } from 'src/app/clazz/utility';
import { DeviceService } from 'src/app/services/device.service';
import { MessageService } from 'src/app/services/message.service';
export interface INavElement {
  link: string;
  icon?: string;
  display: string;
  params: any
}
export const NAV_LIST: { [index: string]: string } = {
  'clients': ''
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
  menuAuth: INavElement[] = [
    {
      link: 'clients',
      display: 'CLIENT_DASHBOARD',
      icon: 'apps',
      params: {
      },
    },
    {
      link: 'role-profiles',
      display: 'ROLE_DASHBOARD',
      icon: 'person',
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
      link: 'api-profiles',
      display: 'SECURITY_PROFILE_DASHBOARD',
      icon: 'mediation',
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
  menuUser: INavElement[] = [
    {
      link: 'resource-owners',
      display: 'RESOURCE_OWNER_DASHBOARD',
      icon: 'perm_identity',
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
  menuMall: INavElement[] = [
    {
      link: 'products',
      display: 'PRODUCT_DASHBOARD',
      icon: 'storefront',
      params: {
      },
    },
    {
      link: 'skus',
      display: 'SKU_DASHBOARD',
      icon: 'storage',
      params: {
      },
    },
    {
      link: 'catalogs/frontend',
      display: 'CATEGORY_DASHBOARD',
      icon: 'category',
      params: {
      },
    },
    {
      link: 'catalogs/backend',
      display: 'CATEGORY_ADMIN_DASHBOARD',
      icon: 'store',
      params: {
      },
    },
    {
      link: 'attributes',
      display: 'ATTRIBUTE_DASHBOARD',
      icon: 'subject',
      params: {
      },
    },
    {
      link: 'filters',
      display: 'FILTER_DASHBOARD',
      icon: 'filter_list',
      params: {
      },
    },
    {
      link: 'orders',
      display: 'ORDER_DASHBOARD',
      icon: 'assignment',
      params: {
      },
    },
    {
      link: 'tasks',
      display: 'TASK_DASHBOARD',
      icon: 'import_contacts',
      params: {
      },
    },
    {
      link: 'events',
      display: 'EVENT_DASHBOARD',
      icon: 'event_available',
      params: {
      },
    },
    {
      link: 'mall-message-center',
      display: 'MALL_MSG_DASHBOARD',
      icon: 'message',
      params: {
      },
    },
  ];
  menuBbs: INavElement[] = [
    {
      link: 'posts',
      display: 'POSTS',
      icon: 'post_add',
      params: {

      },
    },
    {
      link: 'comments',
      display: 'COMMENTS',
      icon: 'mode_comment',
      params: {

      },
    },
    {
      link: 'reports',
      display: 'REPORTS',
      icon: 'block',
      params: {

      },
    },
    {
      link: 'likess',
      display: 'LIKES',
      icon: 'thumb_up',
      params: {

      },
    },
    {
      link: 'dislikes',
      display: 'DISLIKES',
      icon: 'thumb_down',
      params: {
        state: 'create',
      },
    },
    {
      link: 'notInterested',
      display: 'NOT_INTERESTED',
      icon: 'label_off',
      params: {

      },
    },

  ];
  menuOpt: INavElement[] =[
    {
      link: 'operation-history/auth/client',
      display: 'OPERATION_DASHBOARD_CLIENT',
      icon: 'apps',
      params: {
      },
    },
    {
      link: 'operation-history/auth/user',
      display: 'OPERATION_DASHBOARD_USER',
      icon: 'perm_identity',
      params: {
      },
    },
    {
      link: 'operation-history/auth/endpoint',
      display: 'OPERATION_DASHBOARD_EP',
      icon: 'security',
      params: {
      },
    },
    {
      link: 'operation-history/proxy/token',
      display: 'OPERATION_DASHBOARD_TOKEN',
      icon: 'stars',
      params: {
      },
    },
    {
      link: 'operation-history/mall/product',
      display: 'OPERATION_DASHBOARD_PRODUCT',
      icon: 'storefront',
      params: {
      },
    },
    {
      link: 'operation-history/mall/sku',
      display: 'OPERATION_DASHBOARD_SKU',
      icon: 'storage',
      params: {
      },
    },
    {
      link: 'operation-history/mall/catalog',
      display: 'OPERATION_DASHBOARD_CATALOG',
      icon: 'category',
      params: {
      },
    },
    {
      link: 'operation-history/mall/attribute',
      display: 'OPERATION_DASHBOARD_ATTR',
      icon: 'subject',
      params: {
      },
    },
    {
      link: 'operation-history/mall/filter',
      display: 'OPERATION_DASHBOARD_FILTER',
      icon: 'filter_list',
      params: {
      },
    },
    {
      link: 'operation-history/profile/order',
      display: 'OPERATION_DASHBOARD_ORDER',
      icon: 'assignment',
      params: {
      },
    },
  ]
  private _mobileQueryListener: () => void;
  @ViewChild("snav", { static: true }) snav: MatSidenav;
  constructor(changeDetectorRef: ChangeDetectorRef, media: MediaMatcher, public route: ActivatedRoute, public router: Router, public translate: TranslateService,public deviceSvc:DeviceService,public msgSvc:MessageService) {
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
    this.msgSvc.connectSystemMonitor();
    this.msgSvc.connectMallMonitor();
  }
  doLogout(){
    logout()
  }  
  preserveURLQueryParams(input:INavElement){
    const var0=this.route.snapshot.queryParams;
    if(this.router.url.includes(input.link)){
      return {
        ...input.params,
        ...var0
      }
    }else{
      return {
        ...input.params,
      }
    }
  }
}
