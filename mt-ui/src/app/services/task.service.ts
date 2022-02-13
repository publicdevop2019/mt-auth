import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { EntityCommonService } from '../clazz/entity.common-service';
import { DeviceService } from './device.service';
import { HttpProxyService } from './http-proxy.service';
import { CustomHttpInterceptor } from './interceptors/http.interceptor';
export interface IBizTask {
    id: string,
    status: 'STARTED' | 'SUCCESS' | 'RESOLVED' | 'PENDING',
    changeId: string,
    createdAt: string,
    resolveReason?: string,
    orderId: string,
    statusMap: { [key: string]: 'STARTED' | 'SUCCESS' },
    emptyOptMap: { [key: string]: boolean },
    idMap: { [key: string]: string },
    version: number
    cancelable?: boolean
    retryable?: boolean
}
export type DTX_EVENT_TYPE = '/createOrderDtx' | '/cancelCreateOrderDtx' | '/reserveOrderDtx' | '/cancelReserveOrderDtx' | '/recycleOrderDtx' | '/cancelRecycleOrderDtx'
    | '/confirmOrderPaymentDtx' | '/cancelConfirmOrderPaymentDtx'
    | '/concludeOrderDtx' | '/cancelConcludeOrderDtx' | '/updateOrderAddressDtx' | '/cancelUpdateOrderAddressDtx' | '/invalidOrderDtx' | '/cancelInvalidOrderDtx'
@Injectable({
    providedIn: 'root'
})
export class TaskService extends EntityCommonService<IBizTask, IBizTask> {
    private SVC_NAME = '/saga-svc';
    private ENTITY_NAME: DTX_EVENT_TYPE = '/createOrderDtx';
    entityRepo: string = environment.serverUri + this.SVC_NAME + this.ENTITY_NAME;
    constructor(httpProxy: HttpProxyService, interceptor: CustomHttpInterceptor, deviceSvc: DeviceService) {
        super(httpProxy, interceptor, deviceSvc);
    }
    doCancel(id: string) {
        return this.httpProxySvc.cancelDtx(this.entityRepo, id)
    }
    doResolve(id: string, reason: string) {
        return this.httpProxySvc.resolveCancelDtx(this.entityRepo, id, reason)
    }
    updateEntityName(name: DTX_EVENT_TYPE) {
        this.ENTITY_NAME = name;
        this.entityRepo = environment.serverUri + this.SVC_NAME + this.ENTITY_NAME;
    }
    getEntityName() {
        return this.ENTITY_NAME;
    }
    readCancelEntityByQuery(num: number, size: number, query?: string, by?: string, order?: string, headers?: {}) {
        return this.httpProxySvc.readEntityByQuery<IBizTask>(this.getCancelRepo(this.ENTITY_NAME), num, size, query, by, order, headers)
    };
    getCancelRepo(input: DTX_EVENT_TYPE) {
        if (input === '/createOrderDtx')
            return environment.serverUri + this.SVC_NAME + '/cancelCreateOrderDtx'
        if (input === '/reserveOrderDtx')
            return environment.serverUri + this.SVC_NAME + '/cancelReserveOrderDtx'
        if (input === '/recycleOrderDtx')
            return environment.serverUri + this.SVC_NAME + '/cancelRecycleOrderDtx'
        if (input === '/confirmOrderPaymentDtx')
            return environment.serverUri + this.SVC_NAME + '/cancelConfirmOrderPaymentDtx'
        if (input === '/concludeOrderDtx')
            return environment.serverUri + this.SVC_NAME + '/cancelConcludeOrderDtx'
        if (input === '/updateOrderAddressDtx')
            return environment.serverUri + this.SVC_NAME + '/cancelUpdateOrderAddressDtx'
        if (input === '/invalidOrderDtx')
            return environment.serverUri + this.SVC_NAME + '/cancelInvalidOrderDtx'
        return ''
    }
}
