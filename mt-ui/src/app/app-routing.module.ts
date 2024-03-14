import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { NavBarComponent } from './components/nav-bar/nav-bar.component';
import { ApiCenterComponent } from './pages/tenant/market/api-center/api-center.component';
import { CacheControlComponent } from './pages/mgmt/proxy-check/proxy-check.component';
import { LoginComponent } from './pages/common/login/login.component';
import { RegistryComponent } from './pages/mgmt/registry/registry.component';
import { MyCacheComponent } from './pages/tenant/project/my-cache/my-cache.component';
import { SummaryClientComponent } from './pages/mgmt/summary-client/summary-client.component';
import { MyCorsComponent } from './pages/tenant/project/my-cors/my-cors.component';
import { SummaryEndpointComponent } from './pages/mgmt/summary-endpoint/summary-endpoint.component';
import { MessageCenterComponent } from './pages/mgmt/summary-message/summary-message.component';
import { SummaryProjectComponent } from './pages/mgmt/summary-project/summary-project.component';
import { SummaryRevokeTokenComponent } from './pages/mgmt/summary-revoke-token/summary-revoke-token.component';
import { SummaryStoredEventAccessComponent } from './pages/mgmt/summary-stored-event-access/summary-stored-event-access.component';
import { SummaryUserComponent } from './pages/mgmt/summary-user/summary-user.component';
import { MyProfileComponent } from './pages/common/my-profile/my-profile.component';
import { NotFoundComponent } from './pages/common/not-found/not-found.component';
import { SettingComponent } from './pages/common/setting/setting.component';
import { MyAdminComponent } from './pages/tenant/project/my-admin/my-admin.component';
import { MyApisComponent } from './pages/tenant/project/my-endpoints/my-endpoints.component';
import { MyClientsComponent } from './pages/tenant/project/my-clients/my-clients.component';
import { MyPermissionsComponent } from './pages/tenant/project/my-permissions/my-permissions.component';
import { MyProjectComponent } from './pages/tenant/project/my-project/my-project.component';
import { MyRolesComponent } from './pages/tenant/project/my-roles/my-roles.component';
import { MyUsersComponent } from './pages/tenant/project/my-users/my-users.component';
import { WelcomeComponent } from './pages/common/welcome/welcome.component';
import { AuthService } from './services/auth.service';
import { AuthorizeComponent } from './pages/common/authorize/authorize.component';
import { JobComponent } from './pages/mgmt/job/job.component';
import { MfaComponent } from './pages/common/mfa/mfa.component';
import { DocumentComponent } from './pages/document/document.component';
import { LunchComponent } from './pages/document/deploy/deploy.component';
import { DesignComponent } from './pages/document/design/design.component';
import { BuildComponent } from './pages/document/build/build.component';
import { SummaryNotificationComponent } from './pages/mgmt/summary-notification/summary-notification.component';
import { MySubscriptionsComponent } from './pages/tenant/market/my-subscriptions/my-subscriptions.component';
import { MyRequestsComponent } from './pages/tenant/market/my-requests/my-requests.component';
import { MyApprovalComponent } from './pages/tenant/market/my-approval/my-approval.component';
import { UserMessageComponent } from './pages/common/user-message/user-message.component';
import { ErrorLookupComponent } from './pages/document/error-lookup/error-lookup.component';
import { DashboardComponent } from './pages/mgmt/dashboard/dashboard.component';
import { ClientComponent } from './pages/tenant/project/client/client.component';
import { EndpointComponent } from './pages/tenant/project/endpoint/endpoint.component';
import { RoleComponent } from './pages/tenant/project/role/role.component';
import { CorsComponent } from './pages/tenant/project/cors/cors.component';
import { CacheComponent } from './pages/tenant/project/cache/cache.component';
import { UserComponent } from './pages/tenant/project/user/user.component';
import { SubscribeRequestComponent } from './pages/tenant/market/subscribe-request/subscribe-request.component';
import { MgmtUserComponent } from './pages/mgmt/mgmt-user/mgmt-user.component';
import { MgmtEndpointComponent } from './pages/mgmt/endpoint/endpoint.component';
import { MgmtClientComponent } from './pages/mgmt/client/client.component';

const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'mfa', component: MfaComponent },
  {
    path: 'docs', component: DocumentComponent,
    children: [
      { path: '', redirectTo: 'deploy', pathMatch: 'full' },
      { path: 'deploy', component: LunchComponent },
      { path: 'design', component: DesignComponent },
      { path: 'build', component: BuildComponent },
      { path: 'error', component: ErrorLookupComponent },
      { path: '**', component: LunchComponent }
    ]
  },
  { path: 'authorize', component: AuthorizeComponent, canActivate: [AuthService] },
  {
    path: '', component: NavBarComponent, canActivateChild: [AuthService],
    children: [
      { path: '', redirectTo: 'welcome', pathMatch: 'full' },
      { path: 'welcome', component: WelcomeComponent },
      
      { path: 'mgmt/insights', component: DashboardComponent },
      { path: 'mgmt/projects', component: SummaryProjectComponent },
      { path: 'mgmt/registry', component: RegistryComponent },
      { path: 'mgmt/jobs', component: JobComponent },
      { path: 'mgmt/clients', component: SummaryClientComponent },
      { path: 'mgmt/clients/:clientId', component: MgmtClientComponent },
      { path: 'mgmt/users', component: SummaryUserComponent },
      { path: 'mgmt/users/:userId', component: MgmtUserComponent },
      { path: 'mgmt/endpoints', component: SummaryEndpointComponent },
      { path: 'mgmt/endpoints/:endpointId', component: MgmtEndpointComponent },
      { path: 'mgmt/events', component: SummaryStoredEventAccessComponent },
      { path: 'mgmt/notification', component: SummaryNotificationComponent },
      { path: 'mgmt/tokens', component: SummaryRevokeTokenComponent },
      { path: 'mgmt/notification/bell', component: MessageCenterComponent },
      { path: 'mgmt/proxy-cache', component: CacheControlComponent },

      { path: 'market', component: ApiCenterComponent },
      { path: 'requests', component: MyRequestsComponent },
      { path: 'approval', component: MyApprovalComponent },
      { path: 'subscriptions', component: MySubscriptionsComponent },
      { path: 'requests/template', component: SubscribeRequestComponent },
      { path: 'requests/:reqId', component: SubscribeRequestComponent },

      { path: 'user/notification/bell', component: UserMessageComponent },
      { path: 'user/setting', component: SettingComponent },
      { path: 'user/profile', component: MyProfileComponent },

      { path: 'projects/:id/clients/template', component: ClientComponent },
      { path: 'projects/:id/clients/:clientId', component: ClientComponent },
      { path: 'projects/:id/endpoints/template', component: EndpointComponent },
      { path: 'projects/:id/endpoints/:epId', component: EndpointComponent },
      { path: 'projects/:id/roles/:roleId', component: RoleComponent },
      { path: 'projects/:id/cache-configs/:configId', component: CacheComponent },
      { path: 'projects/:id/cache-configs/template', component: CacheComponent },
      { path: 'projects/:id/cors-configs/:configId', component: CorsComponent },
      { path: 'projects/:id/users/:userId', component: UserComponent },

      { path: 'projects/:id/insights', component: MyProjectComponent },
      { path: 'projects/:id/clients', component: MyClientsComponent },
      { path: 'projects/:id/endpoints', component: MyApisComponent },
      { path: 'projects/:id/cache-configs', component: MyCacheComponent },
      { path: 'projects/:id/cors-configs', component: MyCorsComponent },
      { path: 'projects/:id/permissions', component: MyPermissionsComponent },
      { path: 'projects/:id/roles', component: MyRolesComponent },
      { path: 'projects/:id/users', component: MyUsersComponent },
      { path: 'projects/:id/admins', component: MyAdminComponent },
      { path: '**', component: NotFoundComponent }
    ]
  },
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: '**', component: LoginComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
