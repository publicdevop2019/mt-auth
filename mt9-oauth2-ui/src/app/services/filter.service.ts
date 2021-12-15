import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { EntityCommonService } from '../clazz/entity.common-service';
import { IBizFilter } from '../clazz/validation/aggregate/filter/interfaze-filter';
import { DeviceService } from './device.service';
import { HttpProxyService } from './http-proxy.service';
import { CustomHttpInterceptor } from './interceptors/http.interceptor';
@Injectable({
  providedIn: 'root'
})
export class FilterService extends EntityCommonService<IBizFilter, IBizFilter>{
  private PRODUCT_SVC_NAME = '/product-svc';
  private ENTITY_NAME = '/filters';
  entityRepo: string = environment.serverUri + this.PRODUCT_SVC_NAME + this.ENTITY_NAME;
  role: string = 'admin';
  constructor(private httpProxy: HttpProxyService, interceptor: CustomHttpInterceptor,deviceSvc:DeviceService) {
    super(httpProxy, interceptor,deviceSvc);
  }
}
