import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { Observable } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { environment } from 'src/environments/environment';
import { Utility } from '../misc/utility';
import { IMgmtDashboardInfo } from '../pages/mgmt/dashboard/dashboard.component';
import { IJob } from '../pages/mgmt/job/job.component';
import { IProjectUiPermission } from './project.service';
import { IAuthorizeCode, IAuthorizeParty, ICheckSumResponse, IForgetPasswordRequest, IMfaResponse, IVerificationCodeRequest, ISumRep, ITokenResponse, IUpdatePwdCommand, IAuthorizeClientDetail } from '../misc/interface';
export interface IPatch {
    op: string,
    path: string,
    value?: any,
}
export interface IPatchCommand extends IPatch {
    expect: number,
}
export interface IUser {
    id: string
    email: string
    createdAt: string
    agent: string
    countryCode: string
    mobileNumber: string
    lastLoginAt: number
    ipAddress: string
    username: string
    language: string
    avatarLink: string
    hasPassword: boolean
}
@Injectable({
    providedIn: 'root'
})
export class HttpProxyService {
    public logoutCheck = undefined;
    inProgress = false;
    refreshInprogress = false;
    private AUTH_SVC_NAME = '/auth-svc';
    private TOKEN_EP = this.AUTH_SVC_NAME + '/oauth/token';
    set currentUserAuthInfo(token: ITokenResponse) {
        if (token === undefined || token === null) {
            localStorage.setItem('jwt', undefined);
        } else {
            localStorage.setItem('jwt', JSON.stringify(token));
            this.updateLogoutTimer();
        }
    };

    get currentUserAuthInfo(): ITokenResponse | undefined {
        const jwtTokenStr: string = localStorage.getItem('jwt');
        if (jwtTokenStr !== 'undefined' && jwtTokenStr !== undefined) {
            return <ITokenResponse>JSON.parse(jwtTokenStr)
        } else {
            return undefined;
        }
    }
    // OAuth2 pwd flow
    constructor(private _httpClient: HttpClient) {
    }
    updateLogoutTimer() {
        if (this.logoutCheck) {
            clearInterval(this.logoutCheck)
        }
        const expireAfterSeconds = this.getRefreshExpireTime(this.currentUserAuthInfo)
        if (expireAfterSeconds >= 0) {
            this.logoutCheck = setInterval(() => {
                this.expireCheck().subscribe()
            }, (expireAfterSeconds + 31) * 1000)
        } else {
            Utility.logout()
        }
    }
    clearLogoutCheck() {
        if (this.logoutCheck) {
            clearInterval(this.logoutCheck)
        }
    }
    private getRefreshExpireTime(token: ITokenResponse) {
        const encodedBody = token.refresh_token.split('.')[1];
        const decoded = atob(encodedBody)
        const exp: number = +(JSON.parse(decoded) as any).exp
        return exp - Math.ceil(new Date().getTime() / 1000);
    }
    checkProjectReady(projectId: string) {
        return this._httpClient.get<{ status: boolean }>(environment.serverUri + this.AUTH_SVC_NAME + '/projects/' + projectId + '/ready', { headers: { 'loading': 'false' } })
    }
    getJobStatus() {
        return this._httpClient.get<IJob[]>(environment.serverUri + this.AUTH_SVC_NAME + '/mgmt/jobs', { headers: { 'loading': 'false' } })
    }
    resetValidationJob() {
        return this._httpClient.post<void>(environment.serverUri + this.AUTH_SVC_NAME + '/mgmt/job/validation/reset', null)
    }
    resetJob(id: string) {
        return this._httpClient.post<void>(environment.serverUri + this.AUTH_SVC_NAME + `/mgmt/jobs/${id}/reset`, null)
    }
    sendReloadRequest(changeId: string) {
        let headerConfig = new HttpHeaders();
        headerConfig = headerConfig.set('changeId', changeId)
        return this._httpClient.post(environment.serverUri + this.AUTH_SVC_NAME + '/mgmt/endpoints/event/reload', null, { headers: headerConfig });
    }
    getMyProfile() {
        return this._httpClient.get<IUser>(environment.serverUri + this.AUTH_SVC_NAME + '/users/profile');
    }
    cancelSubRequest(id: string, changeId: string) {
        let headerConfig = new HttpHeaders();
        headerConfig = headerConfig.set('changeId', changeId)
        return this._httpClient.post(environment.serverUri + this.AUTH_SVC_NAME + `/subscriptions/requests/${id}/cancel`, undefined, { headers: headerConfig });
    }
    addAdmin(projectId: string, userId: string, changeId: string) {
        let headerConfig = new HttpHeaders();
        headerConfig = headerConfig.set('changeId', changeId)
        return this._httpClient.post(environment.serverUri + this.AUTH_SVC_NAME + `/projects/${projectId}/admins/${userId}`, undefined, { headers: headerConfig });
    }
    approveSubRequest(id: string, changeId: string) {
        let headerConfig = new HttpHeaders();
        headerConfig = headerConfig.set('changeId', changeId)
        return this._httpClient.post(environment.serverUri + this.AUTH_SVC_NAME + `/subscriptions/requests/${id}/approve`, undefined, { headers: headerConfig });
    }
    rejectSubRequest(id: string, changeId: string, rejectionReason: string) {
        let headerConfig = new HttpHeaders();
        headerConfig = headerConfig.set('changeId', changeId)
        return this._httpClient.post(environment.serverUri + this.AUTH_SVC_NAME + `/subscriptions/requests/${id}/reject`, { rejectionReason: rejectionReason }, { headers: headerConfig });
    }
    checkSum() {
        return this._httpClient.get<ICheckSumResponse>(environment.serverUri + this.AUTH_SVC_NAME + '/mgmt/proxy/check');
    }
    expireCheck() {
        return this._httpClient.get<void>(environment.serverUri + this.AUTH_SVC_NAME + '/expire/check');
    }
    retry(repo: string, id: string) {
        return this._httpClient.post(repo + "/" + id + '/retry', null);
    }
    uploadAvatar(file: File): Observable<string> {
        return new Observable<string>(e => {
            const formData: FormData = new FormData();
            formData.append('file', file, file.name);
            let headerConfig = new HttpHeaders();
            headerConfig = headerConfig.set('changeId', Utility.getChangeId())
            this._httpClient.post<void>(environment.serverUri + this.AUTH_SVC_NAME + '/users/profile/avatar', formData, { observe: 'response', headers: headerConfig }).subscribe(next => {
                e.next(next.headers.get('location'));
            });
        })
    };
    dismissNotification(id: string) {
        return this._httpClient.post(environment.serverUri + `/auth-svc/mgmt/notifications/bell/${id}/ack`, null, { headers: { 'loading': 'false' } })
    }
    dismissUserNotification(id: string) {
        return this._httpClient.post(environment.serverUri + `/auth-svc/user/notifications/bell/${id}/ack`, null, { headers: { 'loading': 'false' } })
    }
    getAvatar() {
        return this._httpClient.get(environment.serverUri + '/auth-svc/users/profile/avatar', { responseType: 'blob', headers: { ignore_400: 'true' } })
    };
    forgetPwd(fg: IForgetPasswordRequest, changeId: string): Observable<any> {
        const formData = new FormData();
        formData.append('grant_type', 'client_credentials');
        formData.append('scope', 'not_used');
        return this._httpClient.post<ITokenResponse>(environment.serverUri + this.TOKEN_EP, formData, { headers: this._getAuthHeader(false) })
            .pipe(switchMap(token => this._forgetPwd(this._getToken(token), fg, changeId)))
    };
    resetPwd(fg: IForgetPasswordRequest, changeId: string): Observable<any> {
        const formData = new FormData();
        formData.append('grant_type', 'client_credentials');
        formData.append('scope', 'not_used');
        return this._httpClient.post<ITokenResponse>(environment.serverUri + this.TOKEN_EP, formData, { headers: this._getAuthHeader(false) })
            .pipe(switchMap(token => this._resetPwd(this._getToken(token), fg, changeId)))
    };
    getCode(payload: IVerificationCodeRequest, changeId: string): Observable<any> {
        const formData = new FormData();
        formData.append('grant_type', 'client_credentials');
        formData.append('scope', 'not_used');
        let headers = this._getAuthHeader(false);
        headers = headers.set('changeId', changeId)
        return this._httpClient.post<ITokenResponse>(environment.serverUri + this.TOKEN_EP, formData, { headers: headers })
            .pipe(switchMap(token => this._getCode(this._getToken(token), payload, changeId)))
    };
    ssoClient(projectId: string, clientId: string): Observable<IAuthorizeClientDetail> {
        return this._httpClient.get<IAuthorizeClientDetail>(environment.serverUri + this.AUTH_SVC_NAME + `/projects/${projectId}/clients/${clientId}/authorize`)
    };
    revokeUserToken(id: string): Observable<boolean> {
        let headerConfig = new HttpHeaders();
        headerConfig = headerConfig.set('changeId', Utility.getChangeId())
        return new Observable<boolean>(e => {
            this._httpClient.post<any>(environment.serverUri + '/auth-svc/mgmt/revoke-tokens', { "id": id, "type": "USER" }, { headers: headerConfig }).subscribe(next => {
                e.next(true)
            });
        });
    }
    lockUser(id: string, locked: boolean): Observable<boolean> {
        let headerConfig = new HttpHeaders();
        headerConfig = headerConfig.set('changeId', Utility.getChangeId())
        return new Observable<boolean>(e => {
            this._httpClient.put<any>(environment.serverUri + '/auth-svc/mgmt/users/' + id, { "locked": locked }, { headers: headerConfig }).subscribe(next => {
                e.next(true)
            });
        });
    }
    revokeClientToken(clientId: string): Observable<boolean> {
        let headerConfig = new HttpHeaders();
        headerConfig = headerConfig.set('changeId', Utility.getChangeId())
        return new Observable<boolean>(e => {
            this._httpClient.post<any>(environment.serverUri + '/auth-svc/mgmt/revoke-tokens', { "id": clientId, "type": "CLIENT" }, { headers: headerConfig }).subscribe(next => {
                e.next(true)
            });
        });
    }
    authorize(authorizeParty: IAuthorizeParty): Observable<IAuthorizeCode> {
        const formData = new FormData();
        formData.append('response_type', authorizeParty.response_type);
        formData.append('client_id', authorizeParty.client_id);
        formData.append('state', authorizeParty.state);
        formData.append('project_id', authorizeParty.projectId);
        formData.append('redirect_uri', authorizeParty.redirect_uri);
        return this._httpClient.post<IAuthorizeCode>(environment.serverUri + this.AUTH_SVC_NAME + '/authorize', formData);
    };
    updateUserPwd(command: IUpdatePwdCommand, changeId: string): Observable<boolean> {
        let headerConfig = new HttpHeaders();
        headerConfig = headerConfig.set('changeId', changeId)
        return new Observable<boolean>(e => {
            this._httpClient.put<IUpdatePwdCommand>(environment.serverUri + this.AUTH_SVC_NAME + '/users/pwd', command, { headers: headerConfig }).subscribe(next => {
                e.next(true)
            });
        });
    };
    refreshToken(nextViewTenantId?: string): Observable<ITokenResponse> {
        const formData = new FormData();
        formData.append('grant_type', 'refresh_token');
        formData.append('view_tenant_id', nextViewTenantId ? nextViewTenantId : (this.currentUserAuthInfo.viewTenantId ? this.currentUserAuthInfo.viewTenantId : ''));
        formData.append('refresh_token', this.currentUserAuthInfo.refresh_token);
        formData.append('scope', 'not_used');
        return this._httpClient.post<ITokenResponse>(environment.serverUri + this.TOKEN_EP, formData, { headers: this._getAuthHeader(true) })
    }
    loginMobilePwd(mobileNumber: string, countryCode: string, pwd: string, changeId: string): Observable<ITokenResponse | IMfaResponse> {
        const formData = new FormData();
        let headers = this._getAuthHeader(true);
        headers = headers.append("changeId", changeId)
        formData.append('grant_type', 'password');
        formData.append('type', 'mobile_w_pwd');
        formData.append('mobile_number', mobileNumber);
        formData.append('country_code', countryCode);
        formData.append('password', pwd);
        formData.append('scope', 'not_used');
        return this._httpClient.post<ITokenResponse | IMfaResponse>(environment.serverUri + this.TOKEN_EP, formData, { headers: headers });
    }
    loginEmailPwd(email: string, pwd: string, changeId: string): Observable<ITokenResponse | IMfaResponse> {
        const formData = new FormData();
        let headers = this._getAuthHeader(true);
        headers = headers.append("changeId", changeId)
        formData.append('grant_type', 'password');
        formData.append('type', 'email_w_pwd');
        formData.append('email', email);
        formData.append('password', pwd);
        formData.append('scope', 'not_used');
        return this._httpClient.post<ITokenResponse | IMfaResponse>(environment.serverUri + this.TOKEN_EP, formData, { headers: headers });
    }
    loginUsernamePwd(username: string, pwd: string, changeId: string): Observable<ITokenResponse | IMfaResponse> {
        const formData = new FormData();
        let headers = this._getAuthHeader(true);
        headers = headers.append("changeId", changeId)
        formData.append('grant_type', 'password');
        formData.append('type', 'username_w_pwd');
        formData.append('username', username);
        formData.append('password', pwd);
        formData.append('scope', 'not_used');
        return this._httpClient.post<ITokenResponse | IMfaResponse>(environment.serverUri + this.TOKEN_EP, formData, { headers: headers });
    }
    loginEmail(email: string, code: string, changeId: string): Observable<ITokenResponse | IMfaResponse> {
        const formData = new FormData();
        let headers = this._getAuthHeader(true);
        headers = headers.append("changeId", changeId)
        formData.append('grant_type', 'password');
        formData.append('email', email);
        formData.append('type', 'email_w_code');
        formData.append('code', code);
        formData.append('scope', 'not_used');
        return this._httpClient.post<ITokenResponse | IMfaResponse>(environment.serverUri + this.TOKEN_EP, formData, { headers: headers });
    }
    loginMobile(mobileNumber: string, countryCode: string, code: string, changeId: string): Observable<ITokenResponse | IMfaResponse> {
        const formData = new FormData();
        let headers = this._getAuthHeader(true);
        headers = headers.append("changeId", changeId)
        formData.append('grant_type', 'password');
        formData.append('type', 'mobile_w_code');
        formData.append('mobile_number', mobileNumber);
        formData.append('country_code', countryCode);
        formData.append('code', code);
        formData.append('scope', 'not_used');
        return this._httpClient.post<ITokenResponse | IMfaResponse>(environment.serverUri + this.TOKEN_EP, formData, { headers: headers });
    }
    mfaLoginMobilePwd(loginFG: FormGroup, code: string, changeId: string): Observable<ITokenResponse | IMfaResponse> {
        const formData = new FormData();
        let headers = this._getAuthHeader(true);
        headers = headers.append("changeId", changeId)
        formData.append('grant_type', 'password');
        formData.append('type', 'mobile_w_pwd');
        formData.append('mobile_number', loginFG.get('pwdMobileNumber').value);
        formData.append('country_code', loginFG.get('pwdCountryCode').value);
        formData.append('password', loginFG.get('pwd').value);
        formData.append('scope', 'not_used');
        formData.append('mfa_code', code);
        return this._httpClient.post<ITokenResponse | IMfaResponse>(environment.serverUri + this.TOKEN_EP, formData, { headers: headers });
    }
    mfaLoginMobilePwdMfaSelect(loginFG: FormGroup, method: string, changeId: string): Observable<IMfaResponse> {
        const formData = new FormData();
        let headers = this._getAuthHeader(true);
        headers = headers.append("changeId", changeId)
        formData.append('grant_type', 'password');
        formData.append('type', 'mobile_w_pwd');
        formData.append('mobile_number', loginFG.get('pwdMobileNumber').value);
        formData.append('country_code', loginFG.get('pwdCountryCode').value);
        formData.append('password', loginFG.get('pwd').value);
        formData.append('scope', 'not_used');
        formData.append('mfa_method', method);
        return this._httpClient.post<IMfaResponse>(environment.serverUri + this.TOKEN_EP, formData, { headers: headers });
    }
    mfaLoginEmailPwd(loginFG: FormGroup, code: string, changeId: string): Observable<ITokenResponse | IMfaResponse> {
        const formData = new FormData();
        let headers = this._getAuthHeader(true);
        headers = headers.append("changeId", changeId)
        formData.append('grant_type', 'password');
        formData.append('type', 'email_w_pwd');
        formData.append('email', loginFG.get('pwdEmailOrUsername').value);
        formData.append('password', loginFG.get('pwd').value);
        formData.append('scope', 'not_used');
        formData.append('mfa_code', code);
        return this._httpClient.post<ITokenResponse | IMfaResponse>(environment.serverUri + this.TOKEN_EP, formData, { headers: headers });
    }
    mfaLoginEmailPwdMfaSelect(loginFG: FormGroup, method: string, changeId: string): Observable<IMfaResponse> {
        const formData = new FormData();
        let headers = this._getAuthHeader(true);
        headers = headers.append("changeId", changeId)
        formData.append('grant_type', 'password');
        formData.append('type', 'email_w_pwd');
        formData.append('email', loginFG.get('pwdEmailOrUsername').value);
        formData.append('password', loginFG.get('pwd').value);
        formData.append('scope', 'not_used');
        formData.append('mfa_method', method);
        return this._httpClient.post<IMfaResponse>(environment.serverUri + this.TOKEN_EP, formData, { headers: headers });
    }
    mfaLoginUsernamePwd(loginFG: FormGroup, code: string, changeId: string): Observable<ITokenResponse | IMfaResponse> {
        const formData = new FormData();
        let headers = this._getAuthHeader(true);
        headers = headers.append("changeId", changeId)
        formData.append('grant_type', 'password');
        formData.append('type', 'username_w_pwd');
        formData.append('username', loginFG.get('pwdEmailOrUsername').value);
        formData.append('password', loginFG.get('pwd').value);
        formData.append('scope', 'not_used');
        formData.append('mfa_code', code);
        return this._httpClient.post<ITokenResponse | IMfaResponse>(environment.serverUri + this.TOKEN_EP, formData, { headers: headers });
    }
    mfaLoginUsernamePwdMfaSelect(loginFG: FormGroup, method: string, changeId: string): Observable<IMfaResponse> {
        const formData = new FormData();
        let headers = this._getAuthHeader(true);
        headers = headers.append("changeId", changeId)
        formData.append('grant_type', 'password');
        formData.append('type', 'username_w_pwd');
        formData.append('username', loginFG.get('pwdEmailOrUsername').value);
        formData.append('password', loginFG.get('pwd').value);
        formData.append('scope', 'not_used');
        formData.append('mfa_method', method);
        return this._httpClient.post<IMfaResponse>(environment.serverUri + this.TOKEN_EP, formData, { headers: headers });
    }
    register(registerFG: IVerificationCodeRequest, changeId: string): Observable<any> {
        const formData = new FormData();
        formData.append('grant_type', 'client_credentials');
        formData.append('scope', 'not_used');
        return this._httpClient.post<ITokenResponse>(environment.serverUri + this.TOKEN_EP, formData, { headers: this._getAuthHeader(false) }).pipe(switchMap(token => this._createUser(this._getToken(token), registerFG, changeId)))
    }
    expireEndpoint(projectId: string, id: string, reason: string, changeId: string) {
        let headerConfig = new HttpHeaders();
        headerConfig = headerConfig.set('changeId', changeId)
        return this._httpClient.post(environment.serverUri + `/auth-svc/projects/${projectId}/endpoints/${id}/expire`, { expireReason: reason }, { headers: headerConfig })
    }
    adddUserRole(projectId: string, userId: string, roleIds: string[], changeId: string) {
        let headerConfig = new HttpHeaders();
        headerConfig = headerConfig.set('changeId', changeId)
        return new Observable<boolean>(e => {
            this._httpClient.post(environment.serverUri + `/auth-svc/projects/${projectId}/users/${userId}/roles`, { roleIds: roleIds }, { headers: headerConfig }).subscribe(next => {
                e.next(true)
            });
        });
    }
    removeUserRole(projectId: string, userId: string, roleId: string, changeId: string) {
        let headerConfig = new HttpHeaders();
        headerConfig = headerConfig.set('changeId', changeId)
        return new Observable<boolean>(e => {
            this._httpClient.delete(environment.serverUri + `/auth-svc/projects/${projectId}/users/${userId}/roles/${roleId}`, { headers: headerConfig }).subscribe(next => {
                e.next(true)
            });
        });
    }
    private _getAuthHeader(islogin: boolean, token?: string): HttpHeaders {
        return islogin ? new HttpHeaders().append('Authorization',
            'Basic ' + btoa(environment.loginClientId + ':' + environment.clientSecret)) :
            token ? new HttpHeaders().append('Authorization', 'Bearer ' + token) :
                new HttpHeaders().append('Authorization', 'Basic ' + btoa(environment.noneLoginClientId + ':' + environment.clientSecret));
    }
    private _getToken(res: ITokenResponse): string {
        return res.access_token;
    }
    private _createUser(token: string, registerFG: IVerificationCodeRequest, changeId: string): Observable<any> {
        let headers = this._getAuthHeader(false, token);
        headers = headers.append("changeId", changeId)
        return this._httpClient.post<any>(environment.serverUri + this.AUTH_SVC_NAME + '/users', registerFG, { headers: headers })
    }
    private _getCode(token: string, payload: IVerificationCodeRequest, changeId: string): Observable<any> {
        let headers = this._getAuthHeader(false, token);
        headers = headers.append("changeId", changeId)
        return this._httpClient.post<any>(environment.serverUri + this.AUTH_SVC_NAME + '/verification-code', payload, { headers: headers })
    }
    private _resetPwd(token: string, registerFG: IForgetPasswordRequest, changeId: string): Observable<any> {
        let headers = this._getAuthHeader(false, token);
        headers = headers.append("changeId", changeId)
        return this._httpClient.post<any>(environment.serverUri + this.AUTH_SVC_NAME + '/users/resetPwd', registerFG, { headers: headers })
    }
    private _forgetPwd(token: string, registerFG: IForgetPasswordRequest, changeId: string): Observable<any> {
        let headers = this._getAuthHeader(false, token);
        headers = headers.append("changeId", changeId)
        return this._httpClient.post<any>(environment.serverUri + this.AUTH_SVC_NAME + '/users/forgetPwd', registerFG, { headers: headers })
    }


    private getPageParam(pageNumer?: number, pageSize?: number, sortBy?: string, sortOrder?: string): string {
        let var1: string[] = [];
        if (Utility.hasValue(pageNumer) && Utility.hasValue(pageSize)) {
            if (sortBy && sortOrder) {
                var1.push('num:' + pageNumer)
                var1.push('size:' + pageSize)
                var1.push('by:' + sortBy)
                var1.push('order:' + sortOrder)
                return "page=" + var1.join(',')
            } else {
                var1.push('num:' + pageNumer)
                var1.push('size:' + pageSize)
                return "page=" + var1.join(',')
            }
        }
        return ''
    }
    private getQueryParam(params: string[]): string {
        params = params.filter(e => (e !== '') && (e !== null) && (e !== undefined))
        if (params.length > 0)
            return "?" + params.join('&')
        return ""
    }
    createEntity(entityRepo: string, entity: any, changeId: string, headers?: { [key: string]: string }): Observable<string> {
        let headerConfig = new HttpHeaders();
        headerConfig = headerConfig.set('changeId', changeId)
        if (headers) {
            Object.keys(headers).forEach(key => {
                headerConfig = headerConfig.set(key, headers[key])
            })
        }
        return new Observable<string>(e => {
            this._httpClient.post(entityRepo, entity, { observe: 'response', headers: headerConfig }).subscribe(next => {
                e.next(next.headers.get('location'));
            }, error => {
                //return error so it can be caught by upstream
                e.error(error);
            });
        });
    };
    readEntityById<S>(entityRepo: string, id: string, headers?: {}): Observable<S> {
        let headerConfig = new HttpHeaders();
        headers && Object.keys(headers).forEach(e => {
            headerConfig = headerConfig.set(e, headers[e] + '')
        })
        return this._httpClient.get<S>(entityRepo + '/' + id, { headers: headerConfig });
    };
    readEntityByQuery<T>(entityRepo: string, num: number, size: number, query?: string, by?: string, order?: string, headers?: {}) {
        let headerConfig = new HttpHeaders();
        headers && Object.keys(headers).forEach(e => {
            headerConfig = headerConfig.set(e, headers[e] + '')
        })
        return this._httpClient.get<ISumRep<T>>(entityRepo + this.getQueryParam([this.addPrefix(query), this.getPageParam(num, size, by, order)]), { headers: headerConfig })
    }
    getResourceByQuery<T>(resourceUrl: string, num: number, size: number, by?: string, order?: string, headers?: {}) {
        let headerConfig = new HttpHeaders();
        headers && Object.keys(headers).forEach(e => {
            headerConfig = headerConfig.set(e, headers[e] + '')
        })
        return this._httpClient.get<ISumRep<T>>(this.getResourceUrl(resourceUrl, this.getPageParam(num, size, by, order)), { headers: headerConfig })
    }
    private getResourceUrl(resourceUrl: string, pageConfig: string) {
        return environment.serverUri + resourceUrl + (resourceUrl.includes('?') ? '&' + pageConfig : '?' + pageConfig)
    }
    getUIPermission(projectId: string) {
        return this._httpClient.get<IProjectUiPermission>(environment.serverUri + `/auth-svc/projects/${projectId}/permissions/ui`)
    }
    private addPrefix(query: string): string {
        let var0: string = query;
        if (!query) {
            var0 = undefined
        } else {
            var0 = 'query=' + var0;
        }
        return var0
    }
    updateEntity(entityRepo: string, id: string, entity: any, changeId: string): Observable<boolean> {
        let headerConfig = new HttpHeaders();
        headerConfig = headerConfig.set('changeId', changeId)
        return new Observable<boolean>(e => {
            this._httpClient.put(entityRepo + '/' + id, entity, { headers: headerConfig }).subscribe(next => {
                e.next(true)
            });
        });
    };
    updateEntityExt(entityRepo: string, id: string, entity: any, changeId: string) {
        let headerConfig = new HttpHeaders();
        headerConfig = headerConfig.set('changeId', changeId)
        return this._httpClient.put(entityRepo + '/' + id, entity, { headers: headerConfig });
    };
    deleteEntityById(entityRepo: string, id: string, changeId: string): Observable<boolean> {
        let headerConfig = new HttpHeaders();
        headerConfig = headerConfig.set('changeId', changeId)
        return new Observable<boolean>(e => {
            this._httpClient.delete(entityRepo + '/' + id, { headers: headerConfig }).subscribe(next => {
                e.next(true)
            });
        });
    };
    deleteVersionedEntityById(entityRepo: string, id: string, changeId: string, version: number): Observable<boolean> {
        let headerConfig = new HttpHeaders();
        headerConfig = headerConfig.set('changeId', changeId)
        headerConfig = headerConfig.set('version', version + "")
        return new Observable<boolean>(e => {
            this._httpClient.delete(entityRepo + '/' + id, { headers: headerConfig }).subscribe(next => {
                e.next(true)
            });
        });
    };
    deleteEntityByQuery(entityRepo: string, query: string, changeId: string): Observable<boolean> {
        let headerConfig = new HttpHeaders();
        headerConfig = headerConfig.set('changeId', changeId)
        return new Observable<boolean>(e => {
            this._httpClient.delete(entityRepo + '?' + this.addPrefix(query), { headers: headerConfig }).subscribe(next => {
                e.next(true)
            });
        });
    };
    updateProfileLanguage(language: string) {
        return this._httpClient.put(environment.serverUri + this.AUTH_SVC_NAME + '/users/profile/language', { language: language });
    };
    addProfileEmail(email: string, changeId: string) {
        let headerConfig = new HttpHeaders();
        headerConfig = headerConfig.set('changeId', changeId)
        return this._httpClient.post(environment.serverUri + this.AUTH_SVC_NAME + '/users/profile/email', { email: email }, { headers: headerConfig });
    };
    addProfileMobile(countryCode: string, mobileNumber: string, changeId: string) {
        let headerConfig = new HttpHeaders();
        headerConfig = headerConfig.set('changeId', changeId)
        return this._httpClient.post(environment.serverUri + this.AUTH_SVC_NAME + '/users/profile/mobile', { countryCode: countryCode, mobileNumber: mobileNumber }, { headers: headerConfig });
    };
    addProfileUsername(username: string, changeId: string) {
        let headerConfig = new HttpHeaders();
        headerConfig = headerConfig.set('changeId', changeId)
        return this._httpClient.post(environment.serverUri + this.AUTH_SVC_NAME + '/users/profile/username', { username: username }, { headers: headerConfig });
    };
    removeProfileEmail(changeId: string) {
        let headerConfig = new HttpHeaders();
        headerConfig = headerConfig.set('changeId', changeId)
        return this._httpClient.delete(environment.serverUri + this.AUTH_SVC_NAME + '/users/profile/email', { headers: headerConfig });
    };
    removeProfileMobile(changeId: string) {
        let headerConfig = new HttpHeaders();
        headerConfig = headerConfig.set('changeId', changeId)
        return this._httpClient.delete(environment.serverUri + this.AUTH_SVC_NAME + '/users/profile/mobile', { headers: headerConfig });
    };
    removeProfileUsername(changeId: string) {
        let headerConfig = new HttpHeaders();
        headerConfig = headerConfig.set('changeId', changeId)
        return this._httpClient.delete(environment.serverUri + this.AUTH_SVC_NAME + '/users/profile/username', { headers: headerConfig });
    };
    getMgmtDashboardInfo() {
        return this._httpClient.get<IMgmtDashboardInfo>(environment.serverUri + `/auth-svc/mgmt/dashboard`)
    }
}