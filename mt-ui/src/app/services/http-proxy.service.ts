import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { Observable } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { environment } from 'src/environments/environment';
import { ISumRep } from '../clazz/summary.component';
import { Utility, logout } from '../misc/utility';
import { IEditBooleanEvent } from '../components/editable-boolean/editable-boolean.component';
import { IEditEvent } from '../components/editable-field/editable-field.component';
import { IEditInputListEvent } from '../components/editable-input-multi/editable-input-multi.component';
import { IEditListEvent } from '../components/editable-select-multi/editable-select-multi.component';
import { IAnalysisResult } from '../components/endpoint-analysis-dialog/endpoint-analysis-dialog.component';
import { IMgmtDashboardInfo } from '../pages/mgmt/dashboard/dashboard.component';
import { IJob } from '../pages/mgmt/job/job.component';
import { IRegistryInstance } from '../pages/mgmt/registry/registry.component';
import { IProjectUiPermission } from './project.service';
import { IAuthorizeCode, IAuthorizeParty, IAutoApprove, ICheckSumResponse, IForgetPasswordRequest, IMfaResponse, IPendingUser, ITokenResponse, IUpdatePwdCommand } from '../misc/interface';
import { Logger } from '../misc/logger';
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
}
export interface IUpdateUser {
    countryCode: string
    mobileNumber: string
    username?: string
    language: string
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
            logout()
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
    getRegistryStatus() {
        return this._httpClient.get<IRegistryInstance[]>(environment.serverUri + this.AUTH_SVC_NAME + '/registry')
    }
    checkPorjectReady(projectId: string) {
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
    updateMyProfile(profile: IUpdateUser) {
        return this._httpClient.put<IUpdateUser>(environment.serverUri + this.AUTH_SVC_NAME + '/users/profile', profile);
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
    uploadFile(file: File): Observable<string> {
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
        return this._httpClient.post<ITokenResponse>(environment.serverUri + this.TOKEN_EP, formData, { headers: this._getAuthHeader(false) }).pipe(switchMap(token => this._forgetPwd(this._getToken(token), fg, changeId)))
    };
    resetPwd(fg: IForgetPasswordRequest, changeId: string): Observable<any> {
        const formData = new FormData();
        formData.append('grant_type', 'client_credentials');
        formData.append('scope', 'not_used');
        return this._httpClient.post<ITokenResponse>(environment.serverUri + this.TOKEN_EP, formData, { headers: this._getAuthHeader(false) }).pipe(switchMap(token => this._resetPwd(this._getToken(token), fg, changeId)))
    };
    activate(payload: IPendingUser, changeId: string): Observable<any> {
        const formData = new FormData();
        formData.append('grant_type', 'client_credentials');
        formData.append('scope', 'not_used');
        let headers = this._getAuthHeader(false);
        return this._httpClient.post<ITokenResponse>(environment.serverUri + this.TOKEN_EP, formData, { headers: headers }).pipe(switchMap(token => this._getActivationCode(this._getToken(token), payload, changeId)))
    };
    autoApprove(projectId: string, clientId: string): Observable<boolean> {
        return new Observable<boolean>(e => {
            this._httpClient.get<IAutoApprove>(environment.serverUri + this.AUTH_SVC_NAME + `/projects/${projectId}/clients/${clientId}/autoApprove`).subscribe(next => {
                e.next(next.autoApprove)
            });
        });
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
    login(email: string, pwd: string): Observable<ITokenResponse | IMfaResponse> {
        const formData = new FormData();
        formData.append('grant_type', 'password');
        formData.append('username', email);
        formData.append('password', pwd);
        formData.append('scope', 'not_used');
        return this._httpClient.post<ITokenResponse | IMfaResponse>(environment.serverUri + this.TOKEN_EP, formData, { headers: this._getAuthHeader(true) });
    }
    mfaLogin(loginFG: FormGroup, code: string, id: string): Observable<ITokenResponse | IMfaResponse> {
        const formData = new FormData();
        formData.append('grant_type', 'password');
        formData.append('username', loginFG.get('email').value);
        formData.append('password', loginFG.get('pwd').value);
        formData.append('scope', 'not_used');
        formData.append('mfa_code', code);
        formData.append('mfa_id', id);
        return this._httpClient.post<ITokenResponse | IMfaResponse>(environment.serverUri + this.TOKEN_EP, formData, { headers: this._getAuthHeader(true) });
    }
    register(registerFG: IPendingUser, changeId: string): Observable<any> {
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
    viewEndpointReport(projectId: string, id: string, type: string) {
        return this._httpClient.get<IAnalysisResult>(environment.serverUri + `/auth-svc/projects/${projectId}/endpoints/${id}/report?query=type:${type}`)
    }
    private _getAuthHeader(islogin: boolean, token?: string): HttpHeaders {
        return islogin ? new HttpHeaders().append('Authorization',
            'Basic ' + btoa(environment.loginClientId + ':' + environment.clientSecret)) :
            token ? new HttpHeaders().append('Authorization', 'Bearer ' + token) :
                new HttpHeaders().append('Authorization', 'Basic ' + btoa(environment.registerClientId + ':' + environment.clientSecret));
    }
    private _getToken(res: ITokenResponse): string {
        return res.access_token;
    }
    private _createUser(token: string, registerFG: IPendingUser, changeId: string): Observable<any> {
        let headers = this._getAuthHeader(false, token);
        headers = headers.append("changeId", changeId)
        return this._httpClient.post<any>(environment.serverUri + this.AUTH_SVC_NAME + '/users', registerFG, { headers: headers })
    }
    private _getActivationCode(token: string, payload: IPendingUser, changeId: string): Observable<any> {
        let headers = this._getAuthHeader(false, token);
        headers = headers.append("changeId", changeId)
        return this._httpClient.post<any>(environment.serverUri + this.AUTH_SVC_NAME + '/pending-users', payload, { headers: headers })
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
    private getUserStatusPatch(status: 'LOCK' | 'UNLOCK', ids: string[]): IPatch[] {
        let re: IPatch[] = [];
        ids.forEach(id => {
            let var0: IPatch;
            if (status === "LOCK") {
                var0 = <IPatch>{ op: 'replace', path: "/" + id + '/locked', value: true }
            } else {
                var0 = <IPatch>{ op: 'replace', path: "/" + id + '/locked', value: false }
            }
            re.push(var0)
        })
        return re;
    }
    private getPatchPayload(fieldName: string, fieldValue: IEditEvent): IPatch[] {
        let re: IPatch[] = [];
        let type = undefined;
        if (fieldValue.original) {
            type = 'replace'
        } else {
            type = 'add'
        }
        let startAt = <IPatch>{ op: type, path: "/" + fieldName, value: fieldValue.next }
        re.push(startAt)
        return re;
    }
    private getPatchPayloadAtomicNum(id: string, fieldName: string, fieldValue: IEditEvent): IPatchCommand[] {
        let re: IPatchCommand[] = [];
        let type = undefined;

        if (fieldValue.original >= fieldValue.next) {
            type = 'diff'
        } else {
            type = 'sum'
        }
        let startAt = <IPatchCommand>{ op: type, path: "/" + id + "/" + fieldName, value: Math.abs(+fieldValue.next - +fieldValue.original), expect: 1 }
        re.push(startAt)
        return re;
    }
    private getPatchListPayload(fieldName: string, fieldValue: IEditListEvent): IPatch[] {
        let re: IPatch[] = [];
        let type = 'replace';
        let startAt = <IPatch>{ op: type, path: "/" + fieldName, value: fieldValue.next.map(e => e.value) }
        re.push(startAt)
        return re;
    }
    private getPatchInputListPayload(fieldName: string, fieldValue: IEditInputListEvent): IPatch[] {
        let re: IPatch[] = [];
        let startAt: IPatch;
        if (fieldValue.original) {
            startAt = <IPatch>{ op: 'replace', path: "/" + fieldName, value: fieldValue.next }
        } else {
            startAt = <IPatch>{ op: 'add', path: "/" + fieldName, value: fieldValue.next }
        }
        re.push(startAt)
        return re;
    }
    private getPatchBooleanPayload(fieldName: string, fieldValue: IEditBooleanEvent): IPatch[] {
        let re: IPatch[] = [];
        let type = undefined;
        let startAt: IPatch;
        if (typeof fieldValue.original === 'boolean' && typeof fieldValue.original === 'boolean') {
            type = 'replace'
            startAt = <IPatch>{ op: type, path: "/" + fieldName, value: fieldValue.next }
        } else if (typeof fieldValue.original === 'boolean' && typeof fieldValue.original === 'undefined') {
            type = 'remove'
            startAt = <IPatch>{ op: type, path: "/" + fieldName }
        } else {
            type = 'add'
            startAt = <IPatch>{ op: type, path: "/" + fieldName, value: fieldValue.next }
        }
        re.push(startAt)
        return re;
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
    patchEntityById(entityRepo: string, id: string, fieldName: string, editEvent: IEditEvent, changeId: string) {
        let headerConfig = new HttpHeaders();
        headerConfig = headerConfig.set('Content-Type', 'application/json-patch+json')
        headerConfig = headerConfig.set('changeId', changeId);
        return new Observable<boolean>(e => {
            this._httpClient.patch(entityRepo + '/' + id, this.getPatchPayload(fieldName, editEvent), { headers: headerConfig }).subscribe(next => {
                e.next(true)
            });
        });
    }
    patchEntityAtomicById(entityRepo: string, id: string, fieldName: string, editEvent: IEditEvent, changeId: string) {
        let headerConfig = new HttpHeaders();
        headerConfig = headerConfig.set('Content-Type', 'application/json-patch+json')
        headerConfig = headerConfig.set('changeId', changeId);
        return new Observable<boolean>(e => {
            this._httpClient.patch(entityRepo, this.getPatchPayloadAtomicNum(id, fieldName, editEvent), { headers: headerConfig }).subscribe(next => {
                e.next(true)
            });
        });
    }
    patchEntityListById(entityRepo: string, id: string, fieldName: string, editEvent: IEditListEvent, changeId: string) {
        let headerConfig = new HttpHeaders();
        headerConfig = headerConfig.set('Content-Type', 'application/json-patch+json')
        headerConfig = headerConfig.set('changeId', changeId);
        return new Observable<boolean>(e => {
            this._httpClient.patch(entityRepo + '/' + id, this.getPatchListPayload(fieldName, editEvent), { headers: headerConfig }).subscribe(next => {
                e.next(true)
            });
        });
    }
    patchEntityInputListById(entityRepo: string, id: string, fieldName: string, editEvent: IEditInputListEvent, changeId: string) {
        let headerConfig = new HttpHeaders();
        headerConfig = headerConfig.set('Content-Type', 'application/json-patch+json')
        headerConfig = headerConfig.set('changeId', changeId);
        return new Observable<boolean>(e => {
            this._httpClient.patch(entityRepo + '/' + id, this.getPatchInputListPayload(fieldName, editEvent), { headers: headerConfig }).subscribe(next => {
                e.next(true)
            });
        });
    }
    patchEntityBooleanById(entityRepo: string, id: string, fieldName: string, editEvent: IEditBooleanEvent, changeId: string) {
        let headerConfig = new HttpHeaders();
        headerConfig = headerConfig.set('Content-Type', 'application/json-patch+json')
        headerConfig = headerConfig.set('changeId', changeId);
        if (typeof editEvent.original === 'undefined' && typeof editEvent.next === 'undefined')
            return new Observable<boolean>(e => e.next(true));
        return new Observable<boolean>(e => {
            this._httpClient.patch(entityRepo + '/' + id, this.getPatchBooleanPayload(fieldName, editEvent), { headers: headerConfig }).subscribe(next => {
                e.next(true)
            });
        });
    }
    getMgmtDashboardInfo() {
        return this._httpClient.get<IMgmtDashboardInfo>(environment.serverUri + `/auth-svc/mgmt/dashboard`)
    }
}