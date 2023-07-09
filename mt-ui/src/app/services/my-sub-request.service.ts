import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { EntityCommonService } from '../clazz/entity.common-service';
import { IMySubReq } from '../pages/tenant/market/my-sub-req/my-sub-req.component';
import { DeviceService } from './device.service';
import { HttpProxyService } from './http-proxy.service';
import { CustomHttpInterceptor } from './interceptors/http.interceptor';
import * as UUID from 'uuid/v1';
@Injectable({
  providedIn: 'root'
})
export class MySubRequestService extends EntityCommonService<IMySubReq, IMySubReq>{
  private PRODUCT_SVC_NAME = '/auth-svc';
  private ENTITY_NAME = '/subscriptions/requests';
  entityRepo: string = environment.serverUri + this.PRODUCT_SVC_NAME + this.ENTITY_NAME;
  constructor(public httpProxy: HttpProxyService, interceptor: CustomHttpInterceptor, deviceSvc: DeviceService) {
    super(httpProxy, interceptor, deviceSvc);
  }
  cancelSubRequest(id: string) {
    const changeId = UUID();
    return this.httpProxy.cancelSubRequest(id, changeId)
  }
  approveSubRequest(id: string) {
    const changeId = UUID();
    return this.httpProxy.approveSubRequest(id, changeId)
  }
  rejectSubRequest(id: string, rejectReason: string) {
    const changeId = UUID();
    return this.httpProxy.rejectSubRequest(id, changeId, rejectReason)
  }
}
