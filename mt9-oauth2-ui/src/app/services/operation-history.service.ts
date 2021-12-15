import { Injectable } from '@angular/core';
import { EntityCommonService } from '../clazz/entity.common-service';
import { environment } from 'src/environments/environment';
import { HttpProxyService } from './http-proxy.service';
import { CustomHttpInterceptor } from './interceptors/http.interceptor';
import { DeviceService } from './device.service';
export interface IChangeRecord {
  id: string,
  changeId: string,
  entityType: number,
  // request?: IPatchCommand[],
  requestBody?: {},
  operationType: 'POST' | 'PATCH_BATCH' | 'PATCH_BY_ID' | 'PUT' | 'RESTORE_LAST_VERSION' | 'DELETE_BY_QUERY' | 'EMPTY_OPT' | 'RESTORE_DELETE' | 'CANCEL_CREATE',
  query?: string;
  version: number;
}

export interface IPatchCommand {
  op: string,
  path: string,
  value: Object,
  expect?: number,
}
@Injectable({
  providedIn: 'root'
})
export class OperationHistoryService extends EntityCommonService<IChangeRecord, IChangeRecord>{
  public PRODUCT_SVC_NAME = '';
  public ENTITY_NAME = '/changes';
  entityRepo: string = environment.serverUri + this.PRODUCT_SVC_NAME + this.ENTITY_NAME;
  role: string = 'root';
  constructor(private httpProxy: HttpProxyService, interceptor: CustomHttpInterceptor,deviceSvc:DeviceService) {
    super(httpProxy, interceptor,deviceSvc);
  }
}
