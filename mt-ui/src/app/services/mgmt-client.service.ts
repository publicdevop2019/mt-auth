import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { EntityCommonService } from '../clazz/entity.common-service';
import { IClient } from '../clazz/client.interface';
import { DeviceService } from './device.service';
import { HttpProxyService } from './http-proxy.service';
import { CustomHttpInterceptor } from './interceptors/http.interceptor';
@Injectable({
  providedIn: 'root'
})
export class MgmtClientService extends EntityCommonService<IClient, IClient>{
  entityRepo: string = environment.serverUri + '/auth-svc/mgmt/clients';
  constructor(private httpProxy: HttpProxyService, interceptor: CustomHttpInterceptor, deviceSvc: DeviceService) {
    super(httpProxy, interceptor, deviceSvc);
  }
  revokeClientToken(clientId: number): void {
    this.httpProxy.revokeClientToken(clientId).subscribe(result => {
      result ? this.interceptor.openSnackbar('OPERATION_SUCCESS_TOKEN') : this.interceptor.openSnackbar('OPERATION_FAILED');
    })
  }
  getDropdownClients(num: number, size: number, query?: string, by?: string, order?: string, headers?: {}) {
    return this.httpProxySvc.readEntityByQuery<IClient>(this.entityRepo+'/dropdown', num, size, query, by, order, headers)
  };
}
