import { LayoutModule } from '@angular/cdk/layout';
import { OverlayModule } from '@angular/cdk/overlay';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatBadgeModule } from '@angular/material/badge';
import { MatBottomSheetModule } from '@angular/material/bottom-sheet';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatChipsModule } from '@angular/material/chips';
import { ErrorStateMatcher, MatOptionModule, ShowOnDirtyErrorStateMatcher } from '@angular/material/core';
import { MatDialogModule } from '@angular/material/dialog';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatListModule } from '@angular/material/list';
import { MatMenuModule } from '@angular/material/menu';
import { MatTabsModule } from '@angular/material/tabs';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatRadioModule } from '@angular/material/radio';
import { MatSelectModule } from '@angular/material/select';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatSortModule } from '@angular/material/sort';
import { MatStepperModule } from '@angular/material/stepper';
import { MatTableModule } from '@angular/material/table';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatTreeModule } from '@angular/material/tree';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { TranslateLoader, TranslateModule, TranslateService } from '@ngx-translate/core';
import { FormInfoService, MtFormBuilderModule } from 'mt-form-builder';
import { environment } from '../environments/environment';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { CustomLoader } from './clazz/locale/custom-loader';
import { enUS } from './clazz/locale/en-US';
import { zhHans } from './clazz/locale/zh-Hans';
import { BackButtonComponent } from './components/back-button/back-button.component';
import { CardNotificationComponent } from './components/card-notification/card-notification.component';
import { CopyFieldComponent } from './components/copy-field/copy-field.component';
import { DynamicNodeComponent } from './components/dynamic-tree/dynamic-node/dynamic-node.component';
import { DynamicTreeComponent } from './components/dynamic-tree/dynamic-tree.component';
import { EditableBooleanComponent } from './components/editable-boolean/editable-boolean.component';
import { EditableFieldComponent } from './components/editable-field/editable-field.component';
import { EditableInputMultiComponent } from './components/editable-input-multi/editable-input-multi.component';
import { EditablePageSelectMultiComponent } from './components/editable-page-select-multi/editable-page-select-multi.component';
import { EditablePageSelectSingleComponent } from './components/editable-page-select-single/editable-page-select-single.component';
import { EditableSelectMultiComponent } from './components/editable-select-multi/editable-select-multi.component';
import { EditableSelectSingleComponent } from './components/editable-select-single/editable-select-single.component';
import { LazyImageComponent } from './components/lazy-image/lazy-image.component';
import { MsgBoxComponent } from './components/msg-box/msg-box.component';
import { NavBarComponent } from './components/nav-bar/nav-bar.component';
import { ObjectDetailComponent } from './components/object-detail/object-detail.component';
import { OperationConfirmDialogComponent } from './components/operation-confirm-dialog/operation-confirm-dialog.component';
import { ProgressSpinnerComponent } from './components/progress-spinner/progress-spinner.component';
import { SearchComponent } from './components/search/search.component';
import { TableColumnConfigComponent } from './components/table-column-config/table-column-config.component';
import { TreeNodeDirective } from './directive/tree-node.directive';
import { BatchUpdateCorsComponent } from './components/batch-update-cors/batch-update-cors.component';
import { MgmtEndpointComponent } from './pages/mgmt/endpoint/endpoint.component';
import { ApiCenterComponent } from './components/market/api-center/api-center.component';
import { CacheControlComponent } from './pages/mgmt/proxy-check/proxy-check.component';
import { LoginComponent } from './pages/common/login/login.component';
import { MyProfileComponent } from './pages/common/my-profile/my-profile.component';
import { NewProjectComponent } from './pages/common/new-project/new-project.component';
import { NotFoundComponent } from './pages/common/not-found/not-found.component';
import { SettingComponent } from './pages/common/setting/setting.component';
import { MyAdminComponent } from './pages/tenant/project/my-admin/my-admin.component';
import { ClientComponent } from './pages/tenant/project/client/client.component';
import { MyApisComponent } from './pages/tenant/project/my-endpoints/my-endpoints.component';
import { MyClientsComponent } from './pages/tenant/project/my-clients/my-clients.component';
import { MyPermissionsComponent } from './pages/tenant/project/my-permissions/my-permissions.component';
import { MyProjectComponent } from './pages/tenant/project/my-project/my-project.component';
import { MyRolesComponent } from './pages/tenant/project/my-roles/my-roles.component';
import { MyUsersComponent } from './pages/tenant/project/my-users/my-users.component';
import { RoleComponent } from './pages/tenant/project/role/role.component';
import { UserComponent } from './pages/tenant/project/user/user.component';
import { WelcomeComponent } from './pages/common/welcome/welcome.component';
import { AuthService } from './services/auth.service';
import { DeviceService } from './services/device.service';
import { EndpointService } from './services/endpoint.service';
import { HttpProxyService } from './services/http-proxy.service';
import { CsrfInterceptor } from './services/interceptors/csrf.interceptor';
import { DeleteConfirmHttpInterceptor } from './services/interceptors/delete-confirm.interceptor';
import { CustomHttpInterceptor } from './services/interceptors/http.interceptor';
import { LoadingInterceptor } from './services/interceptors/loading.interceptor';
import { OfflineInterceptor } from './services/interceptors/offline.interceptor';
import { RequestIdHttpInterceptor } from './services/interceptors/request-id.interceptor';
import { SameRequestHttpInterceptor } from './services/interceptors/same-request.interceptor';
import { MgmtClientService } from './services/mgmt-client.service';
import { UserService } from './services/user.service';
import { CacheComponent } from './pages/tenant/project/cache/cache.component';
import { MgmtClientComponent } from './pages/mgmt/client/client.component';
import { CorsComponent } from './pages/tenant/project/cors/cors.component';
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
import { MgmtUserComponent } from './pages/mgmt/mgmt-user/mgmt-user.component';
import { RegistryComponent } from './pages/mgmt/registry/registry.component';
import { AuthorizeComponent } from './pages/common/authorize/authorize.component';
import { RequirePermissionDirective } from './directive/require-permission.directive';
import { JobComponent } from './pages/mgmt/job/job.component';
import { MfaComponent } from './pages/common/mfa/mfa.component';
import { DocumentComponent } from './pages/document/document.component';
import { DesignComponent } from './pages/document/design/design.component';
import { LunchComponent } from './pages/document/deploy/deploy.component';
import { BuildComponent } from './pages/document/build/build.component';
import { SummaryNotificationComponent } from './pages/mgmt/summary-notification/summary-notification.component';
import { SubscribeRequestComponent } from './components/market/subscribe-request/subscribe-request.component';
import { MyRequestsComponent } from './components/market/my-requests/my-requests.component';
import { MyApprovalComponent } from './components/market/my-approval/my-approval.component';
import { MySubscriptionsComponent } from './components/market/my-subscriptions/my-subscriptions.component';
import { EnterReasonDialogComponent } from './components/enter-reason-dialog/enter-reason-dialog.component';
import { MatTooltipModule } from '@angular/material/tooltip';
import { EndpointComponent } from './pages/tenant/project/endpoint/endpoint.component';
import { EndpointAnalysisComponent } from './components/endpoint-analysis-dialog/endpoint-analysis-dialog.component';
import { UserNotificationComponent } from './pages/common/user-notification/user-notification.component';
import { ErrorLookupComponent } from './pages/document/error-lookup/error-lookup.component';
import { DashboardComponent } from './pages/mgmt/dashboard/dashboard.component';
import { LanguageService } from './services/language.service';
import { ClientCreateDialogComponent } from './components/client-create-dialog/client-create-dialog.component';
import { EndpointCreateDialogComponent } from './components/endpoint-create-dialog/endpoint-create-dialog.component';
import { PaginatedSelectComponent } from './components/paginated-select/paginated-select.component';
import { RoleCreateDialogComponent } from './components/role-create-dialog/role-create-dialog.component';
import { AddPermissionDialogComponent } from './components/add-permission-dialog/add-permission-dialog.component';
@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    ClientComponent,
    MgmtUserComponent,
    SummaryClientComponent,
    SummaryResourceOwnerComponent,
    NavBarComponent,
    ProgressSpinnerComponent,
    MsgBoxComponent,
    AuthorizeComponent,
    SummaryEndpointComponent,
    BackButtonComponent,
    OperationConfirmDialogComponent,
    SettingComponent,
    EditableFieldComponent,
    CopyFieldComponent,
    LazyImageComponent,
    EditableSelectMultiComponent,
    EditableBooleanComponent,
    EditableSelectSingleComponent,
    EditableInputMultiComponent,
    SummaryRevokeTokenComponent,
    ObjectDetailComponent,
    EditablePageSelectSingleComponent,
    EditablePageSelectMultiComponent,
    CacheControlComponent,
    MessageCenterComponent,
    DynamicTreeComponent,
    DynamicNodeComponent,
    TreeNodeDirective,
    RequirePermissionDirective,
    CardNotificationComponent,
    SearchComponent,
    SummaryRoleComponent,
    RoleComponent,
    CorsComponent,
    MyCorsComponent,
    MyCacheComponent,
    CacheComponent,
    SummaryStoredEventAccessComponent,
    BatchUpdateCorsComponent,
    TableColumnConfigComponent,
    MyProfileComponent,
    NewProjectComponent,
    ApiCenterComponent,
    SummaryOrgComponent,
    SummaryPermissionComponent,
    SummaryPositionComponent,
    MyClientsComponent,
    MyApisComponent,
    MyRolesComponent,
    MyPermissionsComponent,
    MyProjectComponent,
    SummaryProjectComponent,
    MyAdminComponent,
    MyUsersComponent,
    UserComponent,
    NotFoundComponent,
    WelcomeComponent,
    MgmtEndpointComponent,
    MgmtClientComponent,
    RegistryComponent,
    JobComponent,
    MfaComponent,
    DocumentComponent,
    DesignComponent,
    LunchComponent,
    BuildComponent,
    SummaryNotificationComponent,
    SubscribeRequestComponent,
    MyRequestsComponent,
    MyApprovalComponent,
    MySubscriptionsComponent,
    EnterReasonDialogComponent,
    EndpointComponent,
    EndpointAnalysisComponent,
    UserNotificationComponent,
    ErrorLookupComponent,
    DashboardComponent,
    ClientCreateDialogComponent,
    EndpointCreateDialogComponent,
    PaginatedSelectComponent,
    RoleCreateDialogComponent,
    AddPermissionDialogComponent
  ],
  imports: [
    BrowserAnimationsModule,
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    HttpClientModule,
    ReactiveFormsModule,
    MatTabsModule,
    MatFormFieldModule,
    MatInputModule,
    MatIconModule,
    MatButtonModule,
    MatSelectModule,
    MatOptionModule,
    MatCheckboxModule,
    MatRadioModule,
    MatExpansionModule,
    MatTooltipModule,
    MatCardModule,
    MatPaginatorModule,
    MatMenuModule,
    MatTableModule,
    MatToolbarModule,
    MatSidenavModule,
    MatListModule,
    MatSlideToggleModule,
    MatProgressSpinnerModule,
    MatDialogModule,
    MatSnackBarModule,
    MatTreeModule,
    MatStepperModule,
    MatBottomSheetModule,
    LayoutModule,
    MatChipsModule,
    MatSortModule,
    MatAutocompleteModule,
    MtFormBuilderModule,
    OverlayModule,
    MatBadgeModule,
    TranslateModule.forRoot({
      loader: {
        provide: TranslateLoader,
        useClass: CustomLoader
      }
    }),
  ],
  entryComponents: [
    MsgBoxComponent,
    UserComponent,
    ClientComponent,
    MgmtEndpointComponent,
    MgmtClientComponent,
    EndpointComponent,
    MgmtUserComponent,
    OperationConfirmDialogComponent,
    ObjectDetailComponent,
    RoleComponent,
    CorsComponent,
    BatchUpdateCorsComponent,
    SubscribeRequestComponent,
    CacheComponent],
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: SameRequestHttpInterceptor,
      multi: true
    },
    {
      provide: HTTP_INTERCEPTORS,
      useClass: DeleteConfirmHttpInterceptor,
      multi: true
    },
    {
      provide: HTTP_INTERCEPTORS,
      useClass: CustomHttpInterceptor,
      multi: true
    },
    {
      provide: HTTP_INTERCEPTORS,
      useClass: LoadingInterceptor,
      multi: true
    },
    {
      provide: HTTP_INTERCEPTORS,
      useClass: RequestIdHttpInterceptor,
      multi: true
    },
    {
      provide: HTTP_INTERCEPTORS,
      useClass: CsrfInterceptor,
      multi: true
    },
    {
      provide: HTTP_INTERCEPTORS,
      useClass: OfflineInterceptor,
      multi: true
    },
    {
      provide: ErrorStateMatcher,
      useClass: ShowOnDirtyErrorStateMatcher
    },
    HttpProxyService, MgmtClientService, UserService, AuthService, EndpointService, CustomHttpInterceptor, FormInfoService, DeviceService],
  bootstrap: [AppComponent]
})
export class AppModule {
  constructor(private translate: TranslateService, private fis: FormInfoService, private httpSvc: HttpProxyService, private langSvc: LanguageService) {
    if (this.httpSvc.currentUserAuthInfo) {
      this.httpSvc.updateLogoutTimer()
    }
    let lang = this.langSvc.currentLanguage()
    if (lang === 'zhHans')
      this.fis.i18nLabel = zhHans
    if (lang === 'enUS')
      this.fis.i18nLabel = enUS
    this.translate.onLangChange.subscribe((e) => {
      if (e.lang === 'zhHans')
        this.fis.i18nLabel = zhHans
      if (e.lang === 'enUS')
        this.fis.i18nLabel = enUS
    })
  }
}
