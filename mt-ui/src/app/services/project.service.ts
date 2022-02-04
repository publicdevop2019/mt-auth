import { Injectable } from '@angular/core';
import { IQueryProvider } from 'mt-form-builder/lib/classes/template.interface';
import { environment } from 'src/environments/environment';
import { EntityCommonService } from '../clazz/entity.common-service';
import { IProject, IProjectSimple } from '../clazz/validation/aggregate/project/interface-project';
import { DeviceService } from './device.service';
import { HttpProxyService } from './http-proxy.service';
import { CustomHttpInterceptor } from './interceptors/http.interceptor';
@Injectable({
  providedIn: 'root'
})
export class ProjectService extends EntityCommonService<IProjectSimple, IProject> implements IQueryProvider {
  private PRODUCT_SVC_NAME = '/auth-svc';
  private ENTITY_NAME = '/projects';
  public totalProjects: IProjectSimple[] = [];
  queryPrefix = undefined;
  entityRepo: string = environment.serverUri + this.PRODUCT_SVC_NAME + this.ENTITY_NAME;
  role: string = '';
  constructor(httpProxy: HttpProxyService, interceptor: CustomHttpInterceptor, deviceSvc: DeviceService) {
    super(httpProxy, interceptor, deviceSvc);
  }
  readByQuery(num: number, size: number, query?: string, by?: string, order?: string, headers?: {}) {
    return this.httpProxySvc.readEntityByQuery<IProjectSimple>(this.entityRepo, this.role, num, size, query, by, order, headers)
  };
  findTenantProjects(num: number, size: number) {
    return this.httpProxySvc.readEntityByQuery<IProjectSimple>(this.entityRepo, '/tenant', num, size)
  };
  readEntityByQuery(num: number, size: number, query?: string, by?: string, order?: string, headers?: {}) {
    return this.httpProxySvc.readEntityByQuery<IProjectSimple>(this.entityRepo, this.role, num, size, query ? (this.queryPrefix + ','+query) : this.queryPrefix, by, order, headers)
  };
  resolveTenantId(num: number, size: number, query?: string, by?: string, order?: string, headers?: {}) {
    return this.httpProxySvc.readEntityByQuery<IProjectSimple>(this.entityRepo, this.role, num, size, query, by, order, headers)
  };
  resolveNameById(id:string){
    return this.totalProjects.find(e=>e.id===id)?.name
  }
}
