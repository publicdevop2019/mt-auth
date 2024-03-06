import { Component } from '@angular/core';
import { EndpointService } from 'src/app/services/endpoint.service';
import { HttpProxyService } from 'src/app/services/http-proxy.service';
import { IMgmtEndpoint } from 'src/app/misc/interface';
import { RouterWrapperService } from 'src/app/services/router-wrapper';
import { FormGroup, FormControl } from '@angular/forms';
import { RESOURCE_NAME } from 'src/app/misc/constant';
import { Utility } from 'src/app/misc/utility';
@Component({
  selector: 'mgmt-app-endpoint',
  templateUrl: './endpoint.component.html',
  styleUrls: []
})
export class MgmtEndpointComponent {
  fg = new FormGroup({
    id: new FormControl({ value: '', disabled: true }),
    projectId: new FormControl(''),
    name: new FormControl(''),
    description: new FormControl(''),
    type: new FormControl(''),
    isWebsocket: new FormControl(''),
    resourceName: new FormControl(''),
    path: new FormControl(''),
    method: new FormControl({ value: '', disabled: true }),
    csrf: new FormControl(''),
    cors: new FormControl(false),
    replenishRate: new FormControl(''),
    burstCapacity: new FormControl(''),

    allowCache: new FormControl(''),
    cacheControl: new FormControl(''),
    maxAgeValue: new FormControl(''),
    smaxAgeValue: new FormControl(''),
    vary: new FormControl(''),
    expires: new FormControl(''),
    etagValidation: new FormControl({ value: false, disabled: false }),
    etagType: new FormControl(''),

    corsMaxAge: new FormControl(''),
    allowCredentials: new FormControl(''),
    allowOrigin: new FormControl(''),
    allowedHeaders: new FormControl(''),
    exposedHeaders: new FormControl(''),
  });

  private url = Utility.getMgmtResource(RESOURCE_NAME.MGMT_ENDPOINTS)
  constructor(
    public endpointSvc: EndpointService,
    public httpProxySvc: HttpProxyService,
    public router: RouterWrapperService,
  ) {
    this.fg.disable()
    this.resume()
  }
  resume(): void {
    const endpointId = this.router.getMgmtEndpointIdFromUrl();
    this.httpProxySvc.readEntityById<IMgmtEndpoint>(this.url, endpointId).subscribe(next => {
      this.fg.patchValue(next);
      this.fg.get("csrf").setValue(next.csrfEnabled);
      this.fg.get("replenishRate").setValue(next.replenishRate);
      this.fg.get("burstCapacity").setValue(next.burstCapacity);
      this.fg.get("isWebsocket").setValue(next.websocket ? 'yes' : 'no');
      if (next.cacheConfig) {
        this.fg.get("allowCache").setValue(next.cacheConfig.allowCache ? 'yes' : 'no');
        this.fg.get("cacheControl").setValue(next.cacheConfig.cacheControl);
        this.fg.get("maxAgeValue").setValue(next.cacheConfig.maxAge);
        this.fg.get("smaxAgeValue").setValue(next.cacheConfig.smaxAge);
        this.fg.get("vary").setValue(next.cacheConfig.vary);
        this.fg.get("expires").setValue(next.cacheConfig.expires);
        this.fg.get("etagType").setValue(next.cacheConfig.etag);
      }
      if (next.corsConfig) {
        this.fg.get('allowCredentials').setValue(next.corsConfig.credentials)
        this.fg.get('allowedHeaders').setValue(next.corsConfig.allowedHeaders.join(','))
        this.fg.get('allowOrigin').setValue(next.corsConfig.origin.join(','))
        this.fg.get('exposedHeaders').setValue(next.corsConfig.exposedHeaders.join(','))
        this.fg.get('corsMaxAge').setValue(next.corsConfig.maxAge)
      }
    })
  }
  getIcon() {
    if (this.fg.get('type').value === 'PROTECTED_NONE_SHARED_API') {
      return 'verified_user'
    } else if (this.fg.get('type').value === 'PROTECTED_SHARED_API') {
      return 'share'
    } else if (this.fg.get('type').value === 'PUBLIC_API') {
      return 'lock_open'
    } else {
      return 'visibility_off'
    }
  }
}


