import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { NavBarComponent } from './components/nav-bar/nav-bar.component';
import { SummaryCommentComponent } from './modules/bbs/pages/summary-comment/summary-comment.component';
import { SummaryDislikeComponent } from './modules/bbs/pages/summary-dislike/summary-dislike.component';
import { SummaryLikeComponent } from './modules/bbs/pages/summary-like/summary-like.component';
import { SummaryNotInterestedComponent } from './modules/bbs/pages/summary-not-interested/summary-not-interested.component';
import { SummaryPostComponent } from './modules/bbs/pages/summary-post/summary-post.component';
import { SummaryReportComponent } from './modules/bbs/pages/summary-report/summary-report.component';
import { OrderComponent } from './modules/mall/pages/order/order.component';
import { SummaryAttributeComponent } from './modules/mall/pages/summary-attribute/summary-attribute.component';
import { SummaryCatalogComponent } from './modules/mall/pages/summary-catalog/summary-catalog.component';
import { SummaryFilterComponent } from './modules/mall/pages/summary-filter/summary-filter.component';
import { SummaryOrderComponent } from './modules/mall/pages/summary-order/summary-order.component';
import { SummaryProductComponent } from './modules/mall/pages/summary-product/summary-product.component';
import { SummaryClientComponent } from './modules/my-apps/pages/summary-client/summary-client.component';
import { SummaryRevokeTokenComponent } from './modules/my-apps/pages/summary-revoke-token/summary-revoke-token.component';
import { SummaryResourceOwnerComponent } from './modules/my-users/pages/summary-resource-owner/summary-resource-owner.component';
import { AuthorizeComponent } from './pages/authorize/authorize.component';
import { LoginComponent } from './pages/login/login.component';
import { OperationHistoryComponent } from './pages/operation-history/operation-history.component';
import { SettingComponent } from './pages/setting/setting.component';
import { UpdatePwdComponent } from './pages/update-pwd/update-pwd.component';
import { AuthService } from './services/auth.service';
import { SummarySkuComponent } from './modules/mall/pages/summary-sku/summary-sku.component';
import { SummaryTaskComponent } from './modules/mall/pages/summary-task/summary-task.component';
import { SummaryEndpointComponent } from './modules/my-apps/pages/summary-endpoint/summary-endpoint.component';
import { CacheControlComponent } from './pages/cache-control/cache-control.component';
import { MessageCenterComponent } from './modules/my-apps/pages/message-center/message-center.component';
import { MessageCenterMallComponent } from './modules/mall/pages/message-center-mall/message-center-mall.component';
import { SummaryStoredEventComponent } from './modules/mall/pages/summary-stored-event/summary-stored-event.component';
import { SummaryRoleComponent } from './modules/my-apps/pages/summary-role/summary-role.component';
import { SummaryCorsComponent } from './modules/my-apps/pages/summary-cors/summary-cors.component';
import { SummaryCacheComponent } from './modules/my-apps/pages/summary-cache/summary-cache.component';
import { SummaryStoredEventAccessComponent } from './modules/my-apps/pages/summary-stored-event-access/summary-stored-event-access.component';

const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'authorize', component: AuthorizeComponent, canActivate: [AuthService] },
  {
    path: 'dashboard', component: NavBarComponent, canActivateChild: [AuthService],
    children: [
      { path: '', redirectTo: 'api-profiles', pathMatch: 'full' },
      { path: 'clients', component: SummaryClientComponent },
      { path: 'updatePwd', component: UpdatePwdComponent },
      { path: 'resource-owners', component: SummaryResourceOwnerComponent },
      { path: 'orders', component: SummaryOrderComponent },
      { path: 'orders/:id', component: OrderComponent },
      { path: 'api-profiles', component: SummaryEndpointComponent },
      { path: 'cache-profiles', component: SummaryCacheComponent },
      { path: 'role-profiles', component: SummaryRoleComponent },
      { path: 'cors-profiles', component: SummaryCorsComponent },
      { path: 'products', component: SummaryProductComponent },
      { path: 'catalogs/:type', component: SummaryCatalogComponent },
      { path: 'filters', component: SummaryFilterComponent },
      { path: 'tasks', component: SummaryTaskComponent },
      { path: 'attributes', component: SummaryAttributeComponent },
      { path: 'posts', component: SummaryPostComponent },
      { path: 'comments', component: SummaryCommentComponent },
      { path: 'reports', component: SummaryReportComponent },
      { path: 'likess', component: SummaryLikeComponent },
      { path: 'events', component: SummaryStoredEventComponent },
      { path: 'events-access', component: SummaryStoredEventAccessComponent },
      { path: 'dislikes', component: SummaryDislikeComponent },
      { path: 'notInterested', component: SummaryNotInterestedComponent },
      { path: 'settings', component: SettingComponent},
      { path: 'operation-history/:type/:entity', component: OperationHistoryComponent},
      { path: 'message-center', component: MessageCenterComponent},
      { path: 'mall-message-center', component: MessageCenterMallComponent},
      { path: 'revoke-token', component: SummaryRevokeTokenComponent},
      { path: 'cache-mngr', component: CacheControlComponent},
      { path: 'skus', component: SummarySkuComponent},
      { path: '**', component: SummaryEndpointComponent }
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
