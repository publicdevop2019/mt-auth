import { Injectable } from '@angular/core';
import { Observable, ReplaySubject, Subject } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from 'src/environments/environment';
import { EntityCommonService } from '../clazz/entity.common-service';
import { IProjectSimple } from '../clazz/validation/aggregate/project/interface-project';
import { ISubRequest } from '../pages/common/sub-request/sub-request.component';
import { DeviceService } from './device.service';
import { HttpProxyService } from './http-proxy.service';
import { CustomHttpInterceptor } from './interceptors/http.interceptor';
export interface IProjectPermissionInfo {
  projectPermissionInfo: IProjectPermission[]
}
export interface IProjectPermission {
  projectId: string
  permissionInfo: IPermissionInfo[];
}
interface IPermissionInfo {
  name: string
  id: string
}
@Injectable({
  providedIn: 'root'
})
export class SubRequestService extends EntityCommonService<ISubRequest, ISubRequest>{
  private PRODUCT_SVC_NAME = '/auth-svc';
  private ENTITY_NAME = '/subscription/request';
  entityRepo: string = environment.serverUri + this.PRODUCT_SVC_NAME + this.ENTITY_NAME;
  constructor(public httpProxy: HttpProxyService, interceptor: CustomHttpInterceptor, deviceSvc: DeviceService) {
    super(httpProxy, interceptor, deviceSvc);
  }
}
