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
import { SummaryOrgComponent } from './pages/mgmt/summary-org/summary-org.component';
import { SummaryPermissionComponent } from './pages/mgmt/summary-permission/summary-permission.component';
import { SummaryPositionComponent } from './pages/mgmt/summary-position/summary-position.component';
import { SummaryProjectComponent } from './pages/mgmt/summary-project/summary-project.component';
import { SummaryRevokeTokenComponent } from './pages/mgmt/summary-revoke-token/summary-revoke-token.component';
import { SummaryRoleComponent } from './pages/mgmt/summary-role/summary-role.component';
import { SummaryStoredEventAccessComponent } from './pages/mgmt/summary-stored-event-access/summary-stored-event-access.component';
import { SummaryResourceOwnerComponent } from './pages/mgmt/summary-user/summary-user.component';
import { MyProfileComponent } from './pages/common/my-profile/my-profile.component';
import { NewProjectComponent } from './pages/common/new-project/new-project.component';
import { NotFoundComponent } from './pages/common/not-found/not-found.component';
import { SettingComponent } from './pages/common/setting/setting.component';
import { MyAdminComponent } from './pages/tenant/project/my-admin/my-admin.component';
import { MyApisComponent } from './pages/tenant/project/my-endpoints/my-endpoints.component';
import { MyClientsComponent } from './pages/tenant/project/my-clients/my-clients.component';
import { MyOrgsComponent } from './pages/tenant/project/my-orgs/my-orgs.component';
import { MyPermissionsComponent } from './pages/tenant/project/my-permissions/my-permissions.component';
import { MyPositionsComponent } from './pages/tenant/project/my-positions/my-positions.component';
import { MyProjectComponent } from './pages/tenant/project/my-project/my-project.component';
import { MyRolesComponent } from './pages/tenant/project/my-roles/my-roles.component';
import { MyUsersComponent } from './pages/tenant/project/my-users/my-users.component';
import { UpdatePwdComponent } from './pages/common/update-pwd/update-pwd.component';
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
import { MySubsComponent } from './pages/tenant/market/my-subs/my-subs.component';
import { MySubReqComponent } from './pages/tenant/market/my-sub-req/my-sub-req.component';
import { PendingSubReqComponent } from './pages/tenant/market/pending-sub-req/pending-sub-req.component';
import { UserNotificationComponent } from './pages/common/user-notification/user-notification.component';
import { ErrorLookupComponent } from './pages/document/error-lookup/error-lookup.component';
import { DashboardComponent } from './pages/mgmt/dashboard/dashboard.component';

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
    path: 'home', component: NavBarComponent, canActivateChild: [AuthService],
    children: [
      { path: '', redirectTo: 'welcome', pathMatch: 'full' },
      { path: 'welcome', component: WelcomeComponent },
      { path: 'projects', component: SummaryProjectComponent },
      { path: 'registry', component: RegistryComponent },
      { path: 'jobs', component: JobComponent },
      { path: 'clients', component: SummaryClientComponent },
      { path: 'updatePwd', component: UpdatePwdComponent },
      { path: 'mgmt-user', component: SummaryResourceOwnerComponent },
      { path: 'api-profiles', component: SummaryEndpointComponent },
      { path: 'role-profiles', component: SummaryRoleComponent },
      { path: 'events-access', component: SummaryStoredEventAccessComponent },
      { path: 'settings', component: SettingComponent },
      { path: 'message-center', component: MessageCenterComponent },
      { path: 'user-notification', component: UserNotificationComponent },
      { path: 'revoke-token', component: SummaryRevokeTokenComponent },
      { path: 'cache-mgmt', component: CacheControlComponent },
      { path: 'my-profile', component: MyProfileComponent },
      { path: 'new-project', component: NewProjectComponent },
      { path: 'api-center', component: ApiCenterComponent },
      { path: 'my-sub-request', component: MySubReqComponent },
      { path: 'pending-sub-request', component: PendingSubReqComponent },
      { path: 'my-subs', component: MySubsComponent },
      { path: 'org-profiles', component: SummaryOrgComponent },
      { path: 'permission-profiles', component: SummaryPermissionComponent },
      { path: 'position-profiles', component: SummaryPositionComponent },
      { path: 'sys-message-center', component: SummaryNotificationComponent },
      { path: 'dashboard', component: DashboardComponent },
      { path: ':id/my-client', component: MyClientsComponent },
      { path: ':id/my-cache', component: MyCacheComponent },
      { path: ':id/my-cors', component: MyCorsComponent },
      { path: ':id/my-api', component: MyApisComponent },
      { path: ':id/my-permission', component: MyPermissionsComponent },
      { path: ':id/my-role', component: MyRolesComponent },
      { path: ':id/my-org', component: MyOrgsComponent },
      { path: ':id/my-position', component: MyPositionsComponent },
      { path: ':id/my-project', component: MyProjectComponent },
      { path: ':id/my-user', component: MyUsersComponent },
      { path: ':id/my-admin', component: MyAdminComponent },
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
