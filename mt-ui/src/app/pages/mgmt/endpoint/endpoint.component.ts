import { ChangeDetectorRef, Component, Inject, OnDestroy } from '@angular/core';
import { MatBottomSheetRef, MAT_BOTTOM_SHEET_DATA } from '@angular/material/bottom-sheet';
import { FormInfoService } from 'mt-form-builder';
import { IOption } from 'mt-form-builder/lib/classes/template.interface';
import { combineLatest, Observable } from 'rxjs';
import { take } from 'rxjs/operators';
import { IBottomSheet } from 'src/app/clazz/summary.component';
import { IMgmtEndpoint } from 'src/app/clazz/endpoint.interface';
import { MGMT_EP_FORM_CONFIG } from 'src/app/form-configs/mgmt-endpoint.config';
import { MyCacheService } from 'src/app/services/my-cache.service';
import { MyCorsProfileService } from 'src/app/services/my-cors-profile.service';
import { EndpointService } from 'src/app/services/endpoint.service';
import { HttpProxyService } from 'src/app/services/http-proxy.service';
import { MgmtClientService } from 'src/app/services/mgmt-client.service';
import { ORIGIN_FORM_CONFIG, ALLOWED_HEADERS_FORM_CONFIG, EXPOSED_HEADERS_FORM_CONFIG } from 'src/app/form-configs/cors.config';
@Component({
  selector: 'mgmt-app-endpoint',
  templateUrl: './endpoint.component.html',
  styleUrls: ['./endpoint.component.css']
})
export class MgmtEndpointComponent implements OnDestroy {
  formId: string = 'mgmtApi';
  originFormId: string = 'originFormId'
  allowedHeaderFormId: string = 'allowedHeaderFormId'
  exposedHeaderFormId: string = 'exposedHeaderFormId'
  constructor(
    public endpointSvc: EndpointService,
    public clientSvc: MgmtClientService,
    public corsSvc: MyCorsProfileService,
    public cacheSvc: MyCacheService,
    public httpProxySvc: HttpProxyService,
    public fis: FormInfoService,
    @Inject(MAT_BOTTOM_SHEET_DATA) public data: IBottomSheet<IMgmtEndpoint>,
    public bottomSheetRef: MatBottomSheetRef<MgmtEndpointComponent>,
  ) {
    this.fis.init(MGMT_EP_FORM_CONFIG, this.formId)
    this.fis.init(ORIGIN_FORM_CONFIG, this.originFormId)
    this.fis.init(ALLOWED_HEADERS_FORM_CONFIG, this.allowedHeaderFormId)
    this.fis.init(EXPOSED_HEADERS_FORM_CONFIG, this.exposedHeaderFormId)
    this.fis.disableForm(this.formId)
    this.fis.disableForm(this.originFormId)
    this.fis.disableForm(this.allowedHeaderFormId)
    this.fis.disableForm(this.exposedHeaderFormId)
    this.resume()
    if (this.data.from.method?.toLowerCase() === 'get') {
      this.fis.showIfMatch(this.formId, ['cacheProfile'])
    } else {
      this.fis.hideIfMatch(this.formId, ['cacheProfile'])
    }
    if (this.data.from.websocket) {
      this.fis.hideIfMatch(this.formId, ['csrf', 'cors', 'method'])
    } else {
      this.fis.showIfMatch(this.formId, ['csrf', 'cors', 'method'])
    }
    if (this.data.from.corsConfig) {
      this.fis.showIfMatch(this.formId, ['corsProfile'])
    } else {
      this.fis.hideIfMatch(this.formId, ['corsProfile'])
    }
    this.fis.disableIfNotMatch(this.formId, [])
  }
  ngOnDestroy(): void {
    this.fis.reset(this.originFormId)
    this.fis.reset(this.allowedHeaderFormId)
    this.fis.reset(this.exposedHeaderFormId)
  }
  resume(): void {
    if (this.data.from) {
      const var0: Observable<any>[] = [];
      var0.push(this.clientSvc.readEntityByQuery(0, 1, 'id:' + this.data.from.resourceId))
      combineLatest(var0).pipe(take(1))
        .subscribe(next => {
          let count = 0;
          const options = next[count].data.map(e => <IOption>{ label: e.name, value: e.id })
          this.fis.updateOption(this.formId, 'resourceId', options)
          this.fis.restore(this.formId, this.data.from, true);

          // for cache
          if (this.data.from.cacheConfig) {
            this.fis.formGroups[this.formId].get('allowCache').setValue(this.data.from.cacheConfig.allowCache ? 'yes' : 'no')
            this.fis.formGroups[this.formId].get('cacheControl').setValue(this.data.from.cacheConfig.cacheControl)
            this.fis.formGroups[this.formId].get('maxAgeValue').setValue(this.data.from.cacheConfig.maxAge || '')
            this.fis.formGroups[this.formId].get('smaxAgeValue').setValue(this.data.from.cacheConfig.smaxAge || '')
            this.fis.formGroups[this.formId].get('etagValidation').setValue(this.data.from.cacheConfig.etag)
            this.fis.formGroups[this.formId].get('etagType').setValue(this.data.from.cacheConfig.weakValidation)
            this.fis.formGroups[this.formId].get('expires').setValue(this.data.from.cacheConfig.expires ? this.data.from.cacheConfig.expires : '')
            this.fis.formGroups[this.formId].get('vary').setValue(this.data.from.cacheConfig.vary ? this.data.from.cacheConfig.vary : '')
            if (this.data.from.cacheConfig.allowCache) {
              this.fis.showIfMatch(this.formId, ['cacheControl', 'maxAgeValue', 'smaxAgeValue', 'etagValidation', 'etagType', 'expires', 'vary'])
            }
          }
          if (this.data.from.corsConfig) {
            this.fis.formGroups[this.formId].get('allowCredentials').setValue(this.data.from.corsConfig.credentials)
            this.fis.formGroups[this.formId].get('corsMaxAge').setValue(this.data.from.corsConfig.maxAge)
            this.fis.restoreDynamicForm(this.allowedHeaderFormId, this.fis.parsePayloadArr(this.data.from.corsConfig.allowedHeaders, 'allowedHeaders'), this.data.from.corsConfig.allowedHeaders.length)
            this.fis.restoreDynamicForm(this.originFormId, this.fis.parsePayloadArr(this.data.from.corsConfig.origin, 'allowOrigin'), this.data.from.corsConfig.origin.length)
            this.fis.restoreDynamicForm(this.exposedHeaderFormId, this.fis.parsePayloadArr(this.data.from.corsConfig.exposedHeaders, 'exposedHeaders'), this.data.from.corsConfig.exposedHeaders.length)
            this.fis.disableIfNotMatch(this.allowedHeaderFormId, [])
            this.fis.disableIfNotMatch(this.originFormId, [])
            this.fis.disableIfNotMatch(this.exposedHeaderFormId, [])
          }

          this.fis.formGroups[this.formId].get("secured").setValue(this.data.from.secured);
          this.fis.formGroups[this.formId].get("csrf").setValue(this.data.from.csrfEnabled);
          this.fis.formGroups[this.formId].get("isWebsocket").setValue(this.data.from.websocket ? 'yes' : 'no');
        })

    }
  }
  dismiss(event: MouseEvent) {
    this.bottomSheetRef.dismiss();
    event.preventDefault();
  }
}


