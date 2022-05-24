import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { EntityCommonService } from '../clazz/entity.common-service';
import { IIdBasedEntity } from '../clazz/summary.component';
import { AuthService } from './auth.service';
import { DeviceService } from './device.service';
import { HttpProxyService } from './http-proxy.service';
import { CustomHttpInterceptor } from './interceptors/http.interceptor';
export interface INotification extends IIdBasedEntity {
    title: string,
    descriptions: string[],
    date: number
    type: string
    status: string
}
@Injectable({
    providedIn: 'root'
})
export class NotificationService extends EntityCommonService<INotification, INotification>{
    private SVC_NAME = '/auth-svc';
    private ENTITY_NAME = '/mngmt/notifications';
    entityRepo: string = environment.serverUri + this.SVC_NAME + this.ENTITY_NAME;
    constructor(public authSvc: AuthService, httpProxy: HttpProxyService, interceptor: CustomHttpInterceptor, deviceSvc: DeviceService) {
        super(httpProxy, interceptor, deviceSvc);
    }
    public latestMessage: INotification[] = [];
}
