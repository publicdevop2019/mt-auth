import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { EntityCommonService } from '../clazz/entity.common-service';
import { IIdBasedEntity } from '../clazz/summary.component';
import { DeviceService } from './device.service';
import { HttpProxyService } from './http-proxy.service';
import { CustomHttpInterceptor } from './interceptors/http.interceptor';
export interface IMySubscription extends IIdBasedEntity {
  endpointId: string,
  endpointName: string,
  projectId: string,
  projectName: string,
  replenishRate: number,
  burstCapacity: number,
  endpointStatus: string,
}
@Injectable({
  providedIn: 'root'
})
export class MySubscriptionsService extends EntityCommonService<IMySubscription, IMySubscription>{
  private PRODUCT_SVC_NAME = '/auth-svc';
  private ENTITY_NAME = '/subscriptions';
  entityRepo: string = environment.serverUri + this.PRODUCT_SVC_NAME + this.ENTITY_NAME;
  constructor(public httpProxy: HttpProxyService, interceptor: CustomHttpInterceptor, deviceSvc: DeviceService) {
    super(httpProxy, interceptor, deviceSvc);
  }
}

