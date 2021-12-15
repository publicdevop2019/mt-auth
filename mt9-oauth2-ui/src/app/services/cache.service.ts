import { Injectable } from '@angular/core';
import { IIdName, IQueryProvider, ISumRep } from 'mt-form-builder/lib/classes/template.interface';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { EntityCommonService } from '../clazz/entity.common-service';
import { ICacheProfile } from '../clazz/validation/aggregate/cache/interfaze-cache';
import { IRole } from '../clazz/validation/aggregate/role/interface-role';
import { DeviceService } from './device.service';
import { HttpProxyService } from './http-proxy.service';
import { CustomHttpInterceptor } from './interceptors/http.interceptor';
@Injectable({
  providedIn: 'root'
})
export class CacheService extends EntityCommonService<ICacheProfile, ICacheProfile>  implements IQueryProvider {
  private ENTITY_NAME = '/auth-svc/cache-profile';
  entityRepo: string = environment.serverUri + this.ENTITY_NAME;
  role: string = '';
  constructor(httpProxy: HttpProxyService, interceptor: CustomHttpInterceptor,deviceSvc:DeviceService) {
    super(httpProxy, interceptor,deviceSvc);
  }
  readByQuery (num: number, size: number, query?: string, by?: string, order?: string, header?: {}){
    return this.httpProxySvc.readEntityByQuery<ICacheProfile>(this.entityRepo, this.role, num, size,query, by, order, header)
  };
}
