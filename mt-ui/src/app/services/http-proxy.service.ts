import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { Observable } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { environment } from 'src/environments/environment';
import * as UUID from 'uuid/v1';
import { ICheckSumResponse } from '../clazz/common.interface';
import { ISumRep } from '../clazz/summary.component';
import { IForgetPasswordRequest, IPendingResourceOwner, IResourceOwnerUpdatePwd } from '../clazz/validation/aggregate/user/interfaze-user';
import { IAuthorizeCode, IAuthorizeParty, IAutoApprove, IOrder, ITokenResponse } from '../clazz/validation/interfaze-common';
import { hasValue } from '../clazz/validation/validator-common';
import { IEditBooleanEvent } from '../components/editable-boolean/editable-boolean.component';
import { IEditEvent } from '../components/editable-field/editable-field.component';
import { IEditInputListEvent } from '../components/editable-input-multi/editable-input-multi.component';
import { IEditListEvent } from '../components/editable-select-multi/editable-select-multi.component';
import { ICommentSummary } from './comment.service';
import { IPostSummary } from './post.service';
import { IUserReactionResult } from './reaction.service';
export interface IPatch {
    op: string,
    path: string,
    value?: any,
}
export interface IPatchCommand extends IPatch {
    expect: number,
}
export interface IUser{
    id:string
    email:string
    createdAt:string
}
@Injectable({
    providedIn: 'root'
})
export class HttpProxyService {
    inProgress = false;
    refreshInprogress = false;
    private AUTH_SVC_NAME = '/auth-svc';
    private SAGA_SVC_NAME = '/saga-svc';
    private TOKEN_EP = this.AUTH_SVC_NAME + '/oauth/token';
    private PRODUCT_SVC_NAME = '/product-svc';
    private PROFILE_SVC_NAME = '/profile-svc';
    private FILE_UPLOAD_SVC_NAME = '/file-upload-svc';
    private BBS_SVC_NAME = '/bbs-svc';
    private EVENT_SVC_NAME = '/object-svc';
    set currentUserAuthInfo(token: ITokenResponse) {
        if (token === undefined || token === null) {
            localStorage.setItem('jwt', undefined);
        } else {
            localStorage.setItem('jwt', JSON.stringify(token));
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
    sendReloadRequest(changeId: string) {
        let headerConfig = new HttpHeaders();
        headerConfig = headerConfig.set('changeId', changeId)
        return this._httpClient.post(environment.serverUri + this.AUTH_SVC_NAME + '/mngmt/endpoints/event/reload', null, { headers: headerConfig });
    }
    getMyProfile() {
        return this._httpClient.get<IUser>(environment.serverUri + this.AUTH_SVC_NAME + '/users/profile');
    }
    checkSum() {
        return this._httpClient.get<ICheckSumResponse>(environment.serverUri + this.AUTH_SVC_NAME + '/mngmt/proxy/check');
    }
    cancelDtx(repo: string, id: string) {
        return this._httpClient.post(repo + "/" + id + '/cancel', null);
    }
    resolveCancelDtx(repo: string, id: string, reason: string) {
        return this._httpClient.post(repo + "/" + id + '/resolve', { reason: reason });
    }
    retry(repo: string, id: string) {
        return this._httpClient.post(repo + "/" + id + '/retry', null);
    }
    deletePost(id: string): Observable<boolean> {
        return new Observable<boolean>(e => {
            this._httpClient.delete(environment.serverUri + this.BBS_SVC_NAME + '/admin/posts/' + id).subscribe(next => {
                e.next(true)
            });
        });
    };
    deleteComment(id: string): Observable<boolean> {
        return new Observable<boolean>(e => {
            this._httpClient.delete(environment.serverUri + this.BBS_SVC_NAME + '/admin/comments/' + id).subscribe(next => {
                e.next(true)
            });
        });
    };
    rankLikes(pageNum: number, pageSize: number): Observable<IUserReactionResult> {
        return this._httpClient.get<IUserReactionResult>(environment.serverUri + this.BBS_SVC_NAME + '/admin/likes?pageNum=' + pageNum + '&pageSize=' + pageSize + '&sortOrder=DESC');
    };
    rankDisLikes(pageNum: number, pageSize: number): Observable<IUserReactionResult> {
        return this._httpClient.get<IUserReactionResult>(environment.serverUri + this.BBS_SVC_NAME + '/admin/dislikes?pageNum=' + pageNum + '&pageSize=' + pageSize + '&sortOrder=DESC');
    };
    rankReports(pageNum: number, pageSize: number): Observable<IUserReactionResult> {
        return this._httpClient.get<IUserReactionResult>(environment.serverUri + this.BBS_SVC_NAME + '/admin/reports?pageNum=' + pageNum + '&pageSize=' + pageSize + '&sortOrder=DESC');
    };
    rankNotInterested(pageNum: number, pageSize: number): Observable<IUserReactionResult> {
        return this._httpClient.get<IUserReactionResult>(environment.serverUri + this.BBS_SVC_NAME + '/admin/notInterested?pageNum=' + pageNum + '&pageSize=' + pageSize + '&sortOrder=DESC');
    };
    getAllComments(pageNum: number, pageSize: number): Observable<ICommentSummary> {
        return this._httpClient.get<ICommentSummary>(environment.serverUri + this.BBS_SVC_NAME + '/admin/comments?pageNum=' + pageNum + '&pageSize=' + pageSize + '&sortBy=id' + '&sortOrder=asc');
    };
    getAllPosts(pageNum: number, pageSize: number): Observable<IPostSummary> {
        return this._httpClient.get<IPostSummary>(environment.serverUri + this.BBS_SVC_NAME + '/admin/posts?pageNum=' + pageNum + '&pageSize=' + pageSize + '&sortBy=id' + '&sortOrder=asc');
    };
    getOrders(): Observable<IOrder[]> {
        return this._httpClient.get<IOrder[]>(environment.serverUri + this.PROFILE_SVC_NAME + '/orders');
    };
    uploadFile(file: File): Observable<string> {
        return new Observable<string>(e => {
            const formData: FormData = new FormData();
            formData.append('file', file, file.name);
            let headerConfig = new HttpHeaders();
            headerConfig = headerConfig.set('changeId', UUID())
            this._httpClient.post<void>(environment.serverUri + this.FILE_UPLOAD_SVC_NAME + '/files/app', formData, { observe: 'response', headers: headerConfig }).subscribe(next => {
                e.next(next.headers.get('location'));
            });
        })
    };
    updateProductStatus(id: string, status: 'AVAILABLE' | 'UNAVAILABLE', changeId: string) {
        let headerConfig = new HttpHeaders();
        headerConfig = headerConfig.set('Content-Type', 'application/json-patch+json')
        headerConfig = headerConfig.set('changeId', changeId)
        return new Observable<boolean>(e => {
            this._httpClient.patch(environment.serverUri + this.PRODUCT_SVC_NAME + '/products/admin/' + id, this.getTimeValuePatch(status), { headers: headerConfig }).subscribe(next => {
                e.next(true)
            });
        });
    }
    batchUpdateProductStatus(ids: string[], status: 'AVAILABLE' | 'UNAVAILABLE', changeId: string) {
        let headerConfig = new HttpHeaders();
        headerConfig = headerConfig.set('changeId', changeId)
        return new Observable<boolean>(e => {
            this._httpClient.patch(environment.serverUri + this.PRODUCT_SVC_NAME + '/products/admin', this.getTimeValuePatch(status, ids), { headers: headerConfig }).subscribe(next => {
                e.next(true)
            });
        });
    }
    batchUpdateUserStatus(entityRepo: string, ids: string[], status: 'LOCK' | 'UNLOCK', changeId: string) {
        let headerConfig = new HttpHeaders();
        headerConfig = headerConfig.set('changeId', changeId)
        return new Observable<boolean>(e => {
            this._httpClient.patch(entityRepo, this.getUserStatusPatch(status, ids), { headers: headerConfig }).subscribe(next => {
                e.next(true)
            });
        });
    }
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
    activate(payload: IPendingResourceOwner, changeId: string): Observable<any> {
        const formData = new FormData();
        formData.append('grant_type', 'client_credentials');
        formData.append('scope', 'not_used');
        let headers = this._getAuthHeader(false);
        return this._httpClient.post<ITokenResponse>(environment.serverUri + this.TOKEN_EP, formData, { headers: headers }).pipe(switchMap(token => this._getActivationCode(this._getToken(token), payload, changeId)))
    };
    autoApprove(projectId:string,clientId: string): Observable<boolean> {
        return new Observable<boolean>(e => {
            this._httpClient.get<IAutoApprove>(environment.serverUri + this.AUTH_SVC_NAME + `/projects/${projectId}/clients/${clientId}/autoApprove`).subscribe(next => {
                e.next(next.autoApprove)
            });
        });
    };
    revokeResourceOwnerToken(id: string): Observable<boolean> {
        let headerConfig = new HttpHeaders();
        headerConfig = headerConfig.set('changeId', UUID())
        return new Observable<boolean>(e => {
            this._httpClient.post<any>(environment.serverUri + '/auth-svc/mngmt/revoke-tokens', { "id": id, "type": "USER" }, { headers: headerConfig }).subscribe(next => {
                e.next(true)
            });
        });
    }
    revokeClientToken(clientId: number): Observable<boolean> {
        let headerConfig = new HttpHeaders();
        headerConfig = headerConfig.set('changeId', UUID())
        return new Observable<boolean>(e => {
            this._httpClient.post<any>(environment.serverUri + '/auth-svc/mngmt/revoke-tokens', { "id": clientId, "type": "CLIENT" }, { headers: headerConfig }).subscribe(next => {
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
    updateResourceOwnerPwd(resourceOwner: IResourceOwnerUpdatePwd, changeId: string): Observable<boolean> {
        let headerConfig = new HttpHeaders();
        headerConfig = headerConfig.set('changeId', changeId)
        return new Observable<boolean>(e => {
            this._httpClient.put<IResourceOwnerUpdatePwd>(environment.serverUri + this.AUTH_SVC_NAME + '/users/pwd', resourceOwner, { headers: headerConfig }).subscribe(next => {
                e.next(true)
            });
        });
    };
    refreshToken(): Observable<ITokenResponse> {
        const formData = new FormData();
        formData.append('grant_type', 'refresh_token');
        formData.append('refresh_token', this.currentUserAuthInfo.refresh_token);
        formData.append('scope', 'not_used');
        return this._httpClient.post<ITokenResponse>(environment.serverUri + this.TOKEN_EP, formData, { headers: this._getAuthHeader(true) })
    }
    login(loginFG: FormGroup): Observable<ITokenResponse> {
        const formData = new FormData();
        formData.append('grant_type', 'password');
        formData.append('username', loginFG.get('email').value);
        formData.append('password', loginFG.get('pwd').value);
        formData.append('scope', 'not_used');
        return this._httpClient.post<ITokenResponse>(environment.serverUri + this.TOKEN_EP, formData, { headers: this._getAuthHeader(true) });
    }
    register(registerFG: IPendingResourceOwner, changeId: string): Observable<any> {
        const formData = new FormData();
        formData.append('grant_type', 'client_credentials');
        formData.append('scope', 'not_used');
        return this._httpClient.post<ITokenResponse>(environment.serverUri + this.TOKEN_EP, formData, { headers: this._getAuthHeader(false) }).pipe(switchMap(token => this._createUser(this._getToken(token), registerFG, changeId)))
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
    private _createUser(token: string, registerFG: IPendingResourceOwner, changeId: string): Observable<any> {
        let headers = this._getAuthHeader(false, token);
        headers = headers.append("changeId", changeId)
        return this._httpClient.post<any>(environment.serverUri + this.AUTH_SVC_NAME + '/users', registerFG, { headers: headers })
    }
    private _getActivationCode(token: string, payload: IPendingResourceOwner, changeId: string): Observable<any> {
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
        if (hasValue(pageNumer) && hasValue(pageSize)) {
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
    private getTimeValuePatch(status: 'AVAILABLE' | 'UNAVAILABLE', ids?: string[]): IPatch[] {
        let re: IPatch[] = [];
        if (ids && ids.length > 0) {
            ids.forEach(id => {
                if (status === "AVAILABLE") {
                    let startAt = <IPatch>{ op: 'add', path: "/" + id + '/startAt', value: String(Date.now()) }
                    let endAt = <IPatch>{ op: 'add', path: "/" + id + '/endAt' }
                    re.push(startAt, endAt)
                } else {
                    let startAt = <IPatch>{ op: 'add', path: "/" + id + '/startAt' }
                    re.push(startAt)
                }
            })
        } else {
            if (status === "AVAILABLE") {
                let startAt = <IPatch>{ op: 'add', path: '/startAt', value: String(Date.now()) }
                let endAt = <IPatch>{ op: 'add', path: '/endAt' }
                re.push(startAt, endAt)
            } else {
                let startAt = <IPatch>{ op: 'add', path: '/startAt' }
                re.push(startAt)
            }
        }
        return re;
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
    createEntity(entityRepo: string, entity: any, changeId: string): Observable<string> {
        let headerConfig = new HttpHeaders();
        headerConfig = headerConfig.set('changeId', changeId)
        return new Observable<string>(e => {
            this._httpClient.post(entityRepo, entity, { observe: 'response', headers: headerConfig }).subscribe(next => {
                e.next(next.headers.get('location'));
            });
        });
    };
    readEntityById<S>(entityRepo: string, id: string, headers?: {}): Observable<S> {
        let headerConfig = new HttpHeaders();
        headers && Object.keys(headers).forEach(e => {
            headerConfig = headerConfig.set(e, headers[e] + '')
        })
        return this._httpClient.get<S>(entityRepo+ '/' + id, { headers: headerConfig });
    };
    readEntityByQuery<T>(entityRepo: string, num: number, size: number, query?: string, by?: string, order?: string, headers?: {}) {
        let headerConfig = new HttpHeaders();
        headers && Object.keys(headers).forEach(e => {
            headerConfig = headerConfig.set(e, headers[e] + '')
        })
        return this._httpClient.get<ISumRep<T>>(entityRepo  + this.getQueryParam([this.addPrefix(query), this.getPageParam(num, size, by, order)]), { headers: headerConfig })
    }
    addPrefix(query: string): string {
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
            this._httpClient.put(entityRepo  + '/' + id, entity, { headers: headerConfig }).subscribe(next => {
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
}