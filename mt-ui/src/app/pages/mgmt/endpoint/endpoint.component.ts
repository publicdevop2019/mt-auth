import { ChangeDetectorRef, Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { MatBottomSheetRef, MAT_BOTTOM_SHEET_DATA } from '@angular/material/bottom-sheet';
import { FormInfoService } from 'mt-form-builder';
import { IForm, IOption, ISelectControl } from 'mt-form-builder/lib/classes/template.interface';
import { combineLatest, Observable } from 'rxjs';
import { take } from 'rxjs/operators';
import { Aggregate } from 'src/app/clazz/abstract-aggregate';
import { IBottomSheet } from 'src/app/clazz/summary.component';
import { IEndpoint, IMgmtEndpoint } from 'src/app/clazz/validation/endpoint.interface';
import { EndpointValidator } from 'src/app/clazz/validation/aggregate/endpoint/validator-endpoint';
import { ErrorMessage } from 'src/app/clazz/validation/validator-common';
import { MGMT_EP_FORM_CONFIG } from 'src/app/form-configs/mgmt-endpoint.config';
import { MyCacheService } from 'src/app/services/my-cache.service';
import { MyCorsProfileService } from 'src/app/services/my-cors-profile.service';
import { EndpointService } from 'src/app/services/endpoint.service';
import { HttpProxyService } from 'src/app/services/http-proxy.service';
import { MgmtClientService } from 'src/app/services/mgmt-client.service';
import { ORIGIN_FORM_CONFIG, ALLOWED_HEADERS_FORM_CONFIG, EXPOSED_HEADERS_FORM_CONFIG, FORM_CONFIG } from 'src/app/form-configs/cors.config';
@Component({
  selector: 'mgmt-app-endpoint',
  templateUrl: './endpoint.component.html',
  styleUrls: ['./endpoint.component.css']
})
export class MgmtEndpointComponent extends Aggregate<MgmtEndpointComponent, IMgmtEndpoint> implements OnInit, OnDestroy {
  bottomSheet: IBottomSheet<IMgmtEndpoint>;
  originFormId: string = 'originFormId'
  originFormInfo: IForm = this.disableForm(JSON.parse(JSON.stringify(ORIGIN_FORM_CONFIG)));
  allowedHeaderFormId: string = 'allowedHeaderFormId'
  allowedHeaderFormInfo: IForm = this.disableForm(JSON.parse(JSON.stringify(ALLOWED_HEADERS_FORM_CONFIG)));
  exposedHeaderFormId: string = 'exposedHeaderFormId'
  exposedHeaderFormInfo: IForm = this.disableForm(JSON.parse(JSON.stringify(EXPOSED_HEADERS_FORM_CONFIG)));
  constructor(
    public endpointSvc: EndpointService,
    public clientSvc: MgmtClientService,
    public corsSvc: MyCorsProfileService,
    public cacheSvc: MyCacheService,
    public httpProxySvc: HttpProxyService,
    fis: FormInfoService,
    @Inject(MAT_BOTTOM_SHEET_DATA) public data: any,
    bottomSheetRef: MatBottomSheetRef<MgmtEndpointComponent>,
    cdr: ChangeDetectorRef
  ) {
    super('mgmtApi', MGMT_EP_FORM_CONFIG, new EndpointValidator(), bottomSheetRef, data, fis, cdr)
    this.bottomSheet = data;
    this.fis.init(this.formInfo, this.formId)
    this.resume()
    if (this.aggregate.method?.toLowerCase() === 'get') {
      this.fis.showIfMatch(this.formId, ['cacheProfile'])
    } else {
      this.fis.hideIfMatch(this.formId, ['cacheProfile'])
    }
    if (this.aggregate.websocket) {
      this.fis.hideIfMatch(this.formId, ['csrf', 'cors', 'method'])
    } else {
      this.fis.showIfMatch(this.formId, ['csrf', 'cors', 'method'])
    }
    if (this.aggregate.corsConfig) {
      this.fis.showIfMatch(this.formId, ['corsProfile'])
    } else {
      this.fis.hideIfMatch(this.formId, ['corsProfile'])
    }
    this.fis.disableIfNotMatch(this.formId, [])
  }
  ngOnDestroy(): void {
    this.cleanUp()
    this.fis.reset(this.originFormId)
    this.fis.reset(this.allowedHeaderFormId)
    this.fis.reset(this.exposedHeaderFormId)
  }
  resume(): void {
    if (this.aggregate) {
      const var0: Observable<any>[] = [];
      var0.push(this.clientSvc.readEntityByQuery(0, 1, 'id:' + this.aggregate.resourceId))
      combineLatest(var0).pipe(take(1))
        .subscribe(next => {
          let count = 0;
          (this.formInfo.inputs.find(e => e.key === 'resourceId') as ISelectControl).options = next[count].data.map(e => <IOption>{ label: e.name, value: e.id })
          this.fis.restore(this.formId, this.aggregate, true);

          // for cache
          if (this.aggregate.cacheConfig) {
            this.fis.formGroups[this.formId].get('allowCache').setValue(this.aggregate.cacheConfig.allowCache ? 'yes' : 'no')
            this.fis.formGroups[this.formId].get('cacheControl').setValue(this.aggregate.cacheConfig.cacheControl)
            this.fis.formGroups[this.formId].get('maxAgeValue').setValue(this.aggregate.cacheConfig.maxAge || '')
            this.fis.formGroups[this.formId].get('smaxAgeValue').setValue(this.aggregate.cacheConfig.smaxAge || '')
            this.fis.formGroups[this.formId].get('etagValidation').setValue(this.aggregate.cacheConfig.etag)
            this.fis.formGroups[this.formId].get('etagType').setValue(this.aggregate.cacheConfig.weakValidation)
            this.fis.formGroups[this.formId].get('expires').setValue(this.aggregate.cacheConfig.expires ? this.aggregate.cacheConfig.expires : '')
            this.fis.formGroups[this.formId].get('vary').setValue(this.aggregate.cacheConfig.vary ? this.aggregate.cacheConfig.vary : '')
            if (this.aggregate.cacheConfig.allowCache) {
              this.fis.showIfMatch(this.formId, ['cacheControl', 'maxAgeValue', 'smaxAgeValue', 'etagValidation', 'etagType', 'expires', 'vary'])
            }
          }
          if (this.aggregate.corsConfig) {
            this.fis.formGroups[this.formId].get('allowCredentials').setValue(this.aggregate.corsConfig.credentials)
            this.fis.formGroups[this.formId].get('corsMaxAge').setValue(this.aggregate.corsConfig.maxAge)
            this.fis.restoreDynamicForm(this.allowedHeaderFormId, this.fis.parsePayloadArr(this.aggregate.corsConfig.allowedHeaders, 'allowedHeaders'), this.aggregate.corsConfig.allowedHeaders.length)
            this.fis.restoreDynamicForm(this.originFormId, this.fis.parsePayloadArr(this.aggregate.corsConfig.origin, 'allowOrigin'), this.aggregate.corsConfig.origin.length)
            this.fis.restoreDynamicForm(this.exposedHeaderFormId, this.fis.parsePayloadArr(this.aggregate.corsConfig.exposedHeaders, 'exposedHeaders'), this.aggregate.corsConfig.exposedHeaders.length)
            this.fis.disableIfNotMatch(this.allowedHeaderFormId, [])
            this.fis.disableIfNotMatch(this.originFormId, [])
            this.fis.disableIfNotMatch(this.exposedHeaderFormId, [])
          }

          this.fis.formGroups[this.formId].get("secured").setValue(this.aggregate.secured);
          this.fis.formGroups[this.formId].get("csrf").setValue(this.aggregate.csrfEnabled);
          this.fis.formGroups[this.formId].get("isWebsocket").setValue(this.aggregate.websocket ? 'yes' : 'no');
          this.cdr.markForCheck()
        })

    }
  }
  ngOnInit() {
  }
  convertToPayload(cmpt: MgmtEndpointComponent): IEndpoint {
    throw new Error('Method not implemented.');
  }
  update() {
    throw new Error('Method not implemented.');
  }
  create() {
    throw new Error('Method not implemented.');
  }
  errorMapper(original: ErrorMessage[], cmpt: MgmtEndpointComponent): ErrorMessage[] {
    throw new Error('Method not implemented.');
  }
  disableForm(arg0: IForm): IForm {
    arg0.disabled = true;
    return arg0;
  }
}


