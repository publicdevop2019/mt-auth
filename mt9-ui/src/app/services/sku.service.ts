import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { environment } from 'src/environments/environment';
import { EntityCommonService } from '../clazz/entity.common-service';
import { ISkuNew } from '../clazz/validation/aggregate/product/interfaze-product';
import { DeviceService } from './device.service';
import { HttpProxyService } from './http-proxy.service';
import { CustomHttpInterceptor } from './interceptors/http.interceptor';
@Injectable({
    providedIn: 'root'
})
export class SkuService extends EntityCommonService<ISkuNew, ISkuNew>{
    private AUTH_SVC_NAME = '/product-svc';
    private ENTITY_NAME = '/skus';
    entityRepo: string = environment.serverUri + this.AUTH_SVC_NAME + this.ENTITY_NAME;
    role: string = 'admin';
    constructor(private router: Router, private httpProxy: HttpProxyService, interceptor: CustomHttpInterceptor,deviceSvc:DeviceService) {
        super(httpProxy, interceptor,deviceSvc);
    }
}
