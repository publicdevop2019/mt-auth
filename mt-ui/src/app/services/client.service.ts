import { Injectable } from '@angular/core';
import { EntityCommonService } from '../clazz/entity.common-service';
import { HttpProxyService } from './http-proxy.service';
import { CustomHttpInterceptor } from './interceptors/http.interceptor';
import { environment } from 'src/environments/environment';
import { IClient } from '../clazz/validation/aggregate/client/interfaze-client';
import { IIdName, IQueryProvider, ISumRep } from 'mt-form-builder/lib/classes/template.interface';
import { Observable } from 'rxjs';
import { DeviceService } from './device.service';
@Injectable({
  providedIn: 'root'
})
export class ClientService extends EntityCommonService<IClient, IClient> implements IQueryProvider {
  private AUTH_SVC_NAME = '/auth-svc';
  private ENTITY_NAME = '/clients';
  entityRepo: string = environment.serverUri + this.AUTH_SVC_NAME + this.ENTITY_NAME;
  role: string = '';
  supportEvent = false;
  constructor(private httpProxy: HttpProxyService, interceptor: CustomHttpInterceptor,deviceSvc:DeviceService) {
    super(httpProxy, interceptor,deviceSvc);
  }
  revokeClientToken(clientId: number): void {
    this.httpProxy.revokeClientToken(clientId).subscribe(result => {
      result ? this.interceptor.openSnackbar('OPERATION_SUCCESS_TOKEN') : this.interceptor.openSnackbar('OPERATION_FAILED');
    })
  }
  readByQuery(num: number, size: number, query?: string, by?: string, order?: string, header?: {}): Observable<ISumRep<IClient>> {
    return this.httpProxySvc.readEntityByQuery<IClient>(this.entityRepo, this.role, num, size, ["grantedAuthorities:0R8G09CBKU0X", query].filter(e=>e).join(','), by, order, header)
  };
}
