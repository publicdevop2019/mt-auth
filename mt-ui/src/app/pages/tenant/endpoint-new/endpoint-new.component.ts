import { ChangeDetectorRef, Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { MatBottomSheetRef, MAT_BOTTOM_SHEET_DATA } from '@angular/material/bottom-sheet';
import { FormInfoService } from 'mt-form-builder';
import { IForm, IOption, IQueryProvider } from 'mt-form-builder/lib/classes/template.interface';
import { combineLatest, Observable } from 'rxjs';
import { take } from 'rxjs/operators';
import { Aggregate } from 'src/app/clazz/abstract-aggregate';
import { noEmptyString } from 'src/app/clazz/common.utility';
import { IBottomSheet } from 'src/app/clazz/summary.component';
import { IClient } from 'src/app/clazz/validation/aggregate/client/interfaze-client';
import { IEndpoint } from 'src/app/clazz/validation/aggregate/endpoint/interfaze-endpoint';
import { EndpointValidator } from 'src/app/clazz/validation/aggregate/endpoint/validator-endpoint';
import { ErrorMessage } from 'src/app/clazz/validation/validator-common';
import { BASIC_FORM_CONFIG, CATALOG_FORM_CONFIG, PERFORMANCE_FORM_CONFIG, SECURE_FORM_CONFIG } from 'src/app/form-configs/create-endpoint.config';
import { CacheService } from 'src/app/services/cache.service';
import { CORSProfileService } from 'src/app/services/cors-profile.service';
import { HttpProxyService } from 'src/app/services/http-proxy.service';
import { MyClientService } from 'src/app/services/my-client.service';
import { MyEndpointService } from 'src/app/services/my-endpoint.service';
@Component({
  selector: 'app-endpoint-new',
  templateUrl: './endpoint-new.component.html',
  styleUrls: ['./endpoint-new.component.css']
})
export class EndpointNewComponent extends Aggregate<EndpointNewComponent, IEndpoint> implements OnInit, OnDestroy {
  bottomSheet: IBottomSheet<IEndpoint>;
  isExternal: boolean = undefined;
  isShared: boolean = undefined;
  isSecured: boolean = undefined;
  basicFormId: string = 'basicFormInfoId';
  basicFormInfo: IForm = JSON.parse(JSON.stringify(BASIC_FORM_CONFIG));
  secureFormId: string = 'secureFormInfoId';
  secureFormInfo: IForm = JSON.parse(JSON.stringify(SECURE_FORM_CONFIG));
  performanceFormId: string = 'performanceFormInfoId';
  performanceFormInfo: IForm = JSON.parse(JSON.stringify(PERFORMANCE_FORM_CONFIG));
  performanceWarnning: boolean = false;
  constructor(
    public endpointSvc: MyEndpointService,
    public clientSvc: MyClientService,
    public corsSvc: CORSProfileService,
    public cacheSvc: CacheService,
    public httpProxySvc: HttpProxyService,
    fis: FormInfoService,
    @Inject(MAT_BOTTOM_SHEET_DATA) public data: any,
    bottomSheetRef: MatBottomSheetRef<EndpointNewComponent>,
    cdr: ChangeDetectorRef
  ) {
    super('endpointNew', JSON.parse(JSON.stringify(CATALOG_FORM_CONFIG)), new EndpointValidator(), bottomSheetRef, data, fis, cdr)
    this.bottomSheet = data;
    clientSvc.setProjectId(this.bottomSheet.params['projectId'])
    this.fis.queryProvider[this.basicFormId + '_' + 'resourceId'] = this.getClients();
    this.fis.queryProvider[this.secureFormId + '_' + 'corsProfile'] = corsSvc;
    this.fis.queryProvider[this.performanceFormId + '_' + 'cacheProfile'] = cacheSvc;
    combineLatest([this.fis.formCreated(this.formId), this.fis.formCreated(this.basicFormId), this.fis.formCreated(this.secureFormId),this.fis.formCreated(this.performanceFormId)])
      .subscribe(() => {
        this.fis.formGroupCollection[this.formId].valueChanges.subscribe((next) => {
          const isExternal: boolean = next['isExternal'] === 'yes' ? true : next['isExternal'] === 'no' ? false : undefined
          const isShared: boolean = next['isShared'] === 'yes' ? true : next['isShared'] === 'no' ? false : undefined
          const isSecured: boolean = next['isSecured'] === 'yes' ? true : next['isSecured'] === 'no' ? false : undefined
          this.isExternal = isExternal;
          this.isShared = isShared;
          this.isSecured = isSecured;
          if (isExternal === false) {
            this.fis.disableIfMatch(this.formId, ['isSecured','isShared'])//internal api does not require user authentication
            this.fis.formGroupCollection[this.formId].get('isSecured').setValue('', { emitEvent: false })
          } else {
            if(!this.aggregate){
              this.fis.enableIfMatch(this.formId, ['isSecured','isShared'])
            }
          }
        })
        this.fis.formGroupCollection[this.basicFormId].get('method').valueChanges.subscribe(next => {
          if ((next as string).toLowerCase() === 'get') {
            this.fis.showIfMatch(this.performanceFormId, ['cacheProfile'])
          } else {
            this.fis.hideIfMatch(this.performanceFormId, ['cacheProfile'])
          }
        })
        this.fis.formGroupCollection[this.basicFormId].get('isWebsocket').valueChanges.subscribe(next => {
          if (next === 'yes') {
            this.fis.hideIfMatch(this.basicFormId, ['method'])
            this.fis.hideIfMatch(this.secureFormId, ['csrf', 'cors'])
            this.fis.hideIfMatch(this.performanceFormId, ['replenishRate', 'burstCapacity'])
            this.performanceWarnning=false;
          } else {
            this.fis.showIfMatch(this.basicFormId, ['method'])
            this.fis.showIfMatch(this.secureFormId, ['csrf', 'cors'])
            this.fis.showIfMatch(this.performanceFormId, ['replenishRate', 'burstCapacity'])
            this.performanceWarnning=true;
          }
        })
        this.fis.formGroupCollection[this.secureFormId].get('cors').valueChanges.subscribe(next => {
          if (next) {
            this.fis.showIfMatch(this.secureFormId, ['corsProfile'])
          } else {
            this.fis.hideIfMatch(this.secureFormId, ['corsProfile'])
          }
        })
        this.resume()
      })
  }
  getClients() {
    return {
      readByQuery: (num: number, size: number, query?: string, by?: string, order?: string, header?: {}) => {
        return this.httpProxySvc.readEntityByQuery<IClient>(this.clientSvc.entityRepo, num, size, `projectIds:${this.bottomSheet.params['projectId']},resourceIndicator:1`, by, order, header)
      }
    } as IQueryProvider
  }
  ngOnDestroy(): void {
    this.cleanUp()
    this.fis.reset(this.basicFormId)
    this.fis.reset(this.secureFormId)
    this.fis.reset(this.performanceFormId)
  }
  resume(): void {
    if (this.aggregate) {
      const var0: Observable<any>[] = [];
      var0.push(this.clientSvc.readEntityByQuery(0, 1, 'id:' + this.aggregate.resourceId))
      if (this.aggregate.corsProfileId) {
        var0.push(this.corsSvc.readEntityByQuery(0, 1, 'id:' + this.aggregate.corsProfileId))
      }
      if (this.aggregate.cacheProfileId) {
        var0.push(this.cacheSvc.readEntityByQuery(0, 1, 'id:' + this.aggregate.cacheProfileId))
      }
      if (this.bottomSheet.context !== 'clone') {
        this.fis.disableIfMatch(this.formId, ['isSecured','isExternal','isShared'])
      }
      combineLatest(var0).pipe(take(1))
        .subscribe(next => {
          let count = 0;
          this.basicFormInfo.inputs.find(e => e.key === 'resourceId').options = next[count].data.map(e => <IOption>{ label: e.name, value: e.id })
          if (this.aggregate.corsProfileId) {
            count++;
            this.secureFormInfo.inputs.find(e => e.key === 'corsProfile').options = next[count].data.map(e => <IOption>{ label: e.name, value: e.id })
          }
          if (this.aggregate.cacheProfileId) {
            count++;
            this.performanceFormInfo.inputs.find(e => e.key === 'cacheProfile').options = next[count].data.map(e => <IOption>{ label: e.name, value: e.id })
          }
          this.fis.restore(this.basicFormId, this.aggregate, true);
          this.fis.formGroupCollection[this.formId].get("isSecured").setValue(this.aggregate.secured?'yes':'no');
          this.fis.formGroupCollection[this.formId].get("isShared").setValue(this.aggregate.shared?'yes':'no');
          this.fis.formGroupCollection[this.formId].get("isExternal").setValue(this.aggregate.external?'yes':'no');
          this.fis.formGroupCollection[this.secureFormId].get("csrf").setValue(this.aggregate.csrfEnabled);
          this.fis.formGroupCollection[this.performanceFormId].get("replenishRate").setValue(this.aggregate.replenishRate);
          this.fis.formGroupCollection[this.performanceFormId].get("burstCapacity").setValue(this.aggregate.burstCapacity);
          this.fis.formGroupCollection[this.basicFormId].get("isWebsocket").setValue(this.aggregate.websocket ? 'yes' : 'no');
          if (this.aggregate.corsProfileId) {
            this.fis.formGroupCollection[this.secureFormId].get("cors").setValue(true);
            this.fis.formGroupCollection[this.secureFormId].get("corsProfile").setValue(this.aggregate.corsProfileId);
          }
          if (this.aggregate.cacheProfileId) {
            this.fis.formGroupCollection[this.performanceFormId].get("cacheProfile").setValue(this.aggregate.cacheProfileId);
          }
          this.cdr.markForCheck()

        })

    }
  }
  ngOnInit() {
  }
  convertToPayload(cmpt: EndpointNewComponent): IEndpoint {
    let basicFormGroup = cmpt.fis.formGroupCollection[cmpt.basicFormId];
    let secureFormGroup = cmpt.fis.formGroupCollection[cmpt.secureFormId];
    let perFormGroup = cmpt.fis.formGroupCollection[cmpt.performanceFormId];
    let catalogFormGroup = cmpt.fis.formGroupCollection[cmpt.formId];
    const secured = catalogFormGroup.get('isSecured').value === 'yes';
    const external = catalogFormGroup.get('isExternal').value === 'yes';
    return {
      id: catalogFormGroup.get('id').value,
      description: basicFormGroup.get('description').value ? basicFormGroup.get('description').value : null,
      name: basicFormGroup.get('name').value,
      resourceId: basicFormGroup.get('resourceId').value,
      path: basicFormGroup.get('path').value,
      method: basicFormGroup.get('method').value,
      secured: secured,
      external: external,
      websocket: basicFormGroup.get('isWebsocket').value === 'yes',
      shared: catalogFormGroup.get('isShared').value === 'yes',
      csrfEnabled: !!secureFormGroup.get('csrf').value,
      corsProfileId: noEmptyString(secureFormGroup.get("corsProfile").value),
      cacheProfileId: basicFormGroup.get('method').value === 'GET' ? noEmptyString(perFormGroup.get("cacheProfile").value) : null,
      replenishRate: +perFormGroup.get("replenishRate").value,
      burstCapacity: +perFormGroup.get("burstCapacity").value,
      version: cmpt.aggregate && cmpt.aggregate.version
    }
  }
  update() {
    if (this.validateHelper.validate(this.validator, this.convertToPayload, 'rootUpdateEndpointCommandValidator', this.fis, this, this.errorMapper))
      this.endpointSvc.update(this.aggregate.id, this.convertToPayload(this), this.changeId)
  }
  create() {
    if (this.validateHelper.validate(this.validator, this.convertToPayload, 'rootCreateEndpointCommandValidator', this.fis, this, this.errorMapper))
      this.endpointSvc.create(this.convertToPayload(this), this.changeId)
  }
  errorMapper(original: ErrorMessage[], cmpt: EndpointNewComponent) {
    return original.map(e => {
      return {
        ...e,
        formId: cmpt.formId
      }
    })
  }
}

