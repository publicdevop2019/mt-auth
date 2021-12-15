import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { EntityCommonService } from '../clazz/entity.common-service';
import { IIdBasedEntity } from '../clazz/summary.component';
import { DeviceService } from './device.service';
import { HttpProxyService } from './http-proxy.service';
import { CustomHttpInterceptor } from './interceptors/http.interceptor';
export interface IMallMonitorMsg extends IIdBasedEntity {
    date: number;
    name: string;
    detail: any
    orderId: string;
    changeId: string;
}
@Injectable({
    providedIn: 'root'
})
export class MessageMallService extends EntityCommonService<IMallMonitorMsg, IMallMonitorMsg>{
    private SVC_NAME = '/messenger-svc';
    private ENTITY_NAME = '/mallNotifications';
    entityRepo: string = environment.serverUri + this.SVC_NAME + this.ENTITY_NAME;
    role: string = 'admin';
    constructor(httpProxy: HttpProxyService, interceptor: CustomHttpInterceptor,deviceSvc:DeviceService) {
        super(httpProxy, interceptor,deviceSvc);
    }
}
