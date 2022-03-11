import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { NavBarComponent } from './components/nav-bar/nav-bar.component';
import { ApiCenterComponent } from './pages/common/api-center/api-center.component';
import { CacheControlComponent } from './pages/common/proxy-check/proxy-check.component';
import { LoginComponent } from './pages/common/login/login.component';
import { RegistryComponent } from './pages/mgnmt/registry/registry.component';
import { SummaryCacheComponent } from './pages/mgnmt/summary-cache/summary-cache.component';
import { SummaryClientComponent } from './pages/mgnmt/summary-client/summary-client.component';
import { SummaryCorsComponent } from './pages/mgnmt/summary-cors/summary-cors.component';
import { SummaryEndpointComponent } from './pages/mgnmt/summary-endpoint/summary-endpoint.component';
import { MessageCenterComponent } from './pages/mgnmt/summary-message/summary-message.component';
import { SummaryOrgComponent } from './pages/mgnmt/summary-org/summary-org.component';
import { SummaryPermissionComponent } from './pages/mgnmt/summary-permission/summary-permission.component';
import { SummaryPositionComponent } from './pages/mgnmt/summary-position/summary-position.component';
import { SummaryProjectComponent } from './pages/mgnmt/summary-project/summary-project.component';
import { SummaryRevokeTokenComponent } from './pages/mgnmt/summary-revoke-token/summary-revoke-token.component';
import { SummaryRoleComponent } from './pages/mgnmt/summary-role/summary-role.component';
import { SummaryStoredEventAccessComponent } from './pages/mgnmt/summary-stored-event-access/summary-stored-event-access.component';
import { SummaryResourceOwnerComponent } from './pages/mgnmt/summary-user/summary-user.component';
import { MyProfileComponent } from './pages/common/my-profile/my-profile.component';
import { NewProjectComponent } from './pages/common/new-project/new-project.component';
import { NotFoundComponent } from './pages/common/not-found/not-found.component';
import { SettingComponent } from './pages/common/setting/setting.component';
import { AddAdminComponent } from './pages/tenant/add-admin/add-admin.component';
import { MyApisComponent } from './pages/tenant/my-endpoints/my-endpoints.component';
import { MyClientsComponent } from './pages/tenant/my-clients/my-clients.component';
import { MyOrgsComponent } from './pages/tenant/my-orgs/my-orgs.component';
import { MyPermissionsComponent } from './pages/tenant/my-permissions/my-permissions.component';
import { MyPositionsComponent } from './pages/tenant/my-positions/my-positions.component';
import { MyProjectComponent } from './pages/tenant/my-project/my-project.component';
import { MyRolesComponent } from './pages/tenant/my-roles/my-roles.component';
import { MyUsersComponent } from './pages/tenant/my-users/my-users.component';
import { UpdatePwdComponent } from './pages/common/update-pwd/update-pwd.component';
import { WelcomeComponent } from './pages/common/welcome/welcome.component';
import { AuthService } from './services/auth.service';
import { AuthorizeComponent } from './pages/common/authorize/authorize.component';

const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'authorize', component: AuthorizeComponent, canActivate: [AuthService] },
  {
    path: 'dashboard', component: NavBarComponent, canActivateChild: [AuthService],
    children: [
      { path: '', redirectTo: 'welcome', pathMatch: 'full' },
      { path: 'welcome', component: WelcomeComponent },
      { path: 'projects', component: SummaryProjectComponent },
      { path: 'registry', component: RegistryComponent },
      { path: 'clients', component: SummaryClientComponent },
      { path: 'updatePwd', component: UpdatePwdComponent },
      { path: 'resource-owners', component: SummaryResourceOwnerComponent },
      { path: 'api-profiles', component: SummaryEndpointComponent },
      { path: 'cache-profiles', component: SummaryCacheComponent },
      { path: 'role-profiles', component: SummaryRoleComponent },
      { path: 'cors-profiles', component: SummaryCorsComponent },
      { path: 'events-access', component: SummaryStoredEventAccessComponent },
      { path: 'settings', component: SettingComponent},
      { path: 'message-center', component: MessageCenterComponent},
      { path: 'revoke-token', component: SummaryRevokeTokenComponent},
      { path: 'cache-mngr', component: CacheControlComponent},
      { path: 'my-profile', component: MyProfileComponent},
      { path: 'new-project', component: NewProjectComponent},
      { path: 'api-center', component: ApiCenterComponent},
      { path: 'org-profiles', component: SummaryOrgComponent},
      { path: 'permission-profiles', component: SummaryPermissionComponent},
      { path: 'position-profiles', component: SummaryPositionComponent},
      { path: ':id/my-client', component: MyClientsComponent},
      { path: ':id/my-api', component: MyApisComponent},
      { path: ':id/my-permission', component: MyPermissionsComponent},
      { path: ':id/my-role', component: MyRolesComponent},
      { path: ':id/my-org', component: MyOrgsComponent},
      { path: ':id/my-position', component: MyPositionsComponent},
      { path: ':id/my-project', component: MyProjectComponent},
      { path: ':id/my-user', component: MyUsersComponent},
      { path: ':id/add-admin', component: AddAdminComponent},
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
