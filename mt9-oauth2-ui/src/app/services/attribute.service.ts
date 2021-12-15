import { Injectable } from '@angular/core';
import { IQueryProvider } from 'mt-form-builder/lib/classes/template.interface';
import { environment } from 'src/environments/environment';
import { EntityCommonService } from '../clazz/entity.common-service';
import { IBizAttribute } from '../clazz/validation/aggregate/attribute/interfaze-attribute';
import { DeviceService } from './device.service';
import { HttpProxyService } from './http-proxy.service';
import { CustomHttpInterceptor } from './interceptors/http.interceptor';
@Injectable({
  providedIn: 'root'
})
export class AttributeService extends EntityCommonService<IBizAttribute, IBizAttribute> implements IQueryProvider{
  private PRODUCT_SVC_NAME = '/product-svc';
  private ENTITY_NAME = '/attributes';
  entityRepo: string = environment.serverUri + this.PRODUCT_SVC_NAME + this.ENTITY_NAME;
  role: string = 'admin';
  constructor(httpProxy: HttpProxyService, interceptor: CustomHttpInterceptor,deviceSvc:DeviceService) {
    super(httpProxy, interceptor,deviceSvc);
  }
  readByQuery(num: number, size: number, query?: string, by?: string, order?: string,headers?:{}) {
    return this.httpProxySvc.readEntityByQuery<IBizAttribute>(this.entityRepo, this.role, num, size, query, by, order,headers)
};
}
