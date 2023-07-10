import { ChangeDetectorRef, Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { MatBottomSheetRef, MAT_BOTTOM_SHEET_DATA } from '@angular/material/bottom-sheet';
import { FormInfoService } from 'mt-form-builder';
import { IForm, IOption, IQueryProvider, ISelectControl } from 'mt-form-builder/lib/classes/template.interface';
import { combineLatest, Observable } from 'rxjs';
import { take } from 'rxjs/operators';
import { Aggregate } from 'src/app/clazz/abstract-aggregate';
import { IBottomSheet } from 'src/app/clazz/summary.component';
import { IClient } from 'src/app/clazz/validation/client.interface';
import { IEndpoint } from 'src/app/clazz/validation/endpoint.interface';
import { EndpointValidator } from 'src/app/clazz/validation/aggregate/endpoint/validator-endpoint';
import { ErrorMessage } from 'src/app/clazz/validation/validator-common';
import { BASIC_FORM_CONFIG, CATALOG_FORM_CONFIG, PERFORMANCE_FORM_CONFIG, SECURE_FORM_CONFIG } from 'src/app/form-configs/create-endpoint.config';
import { MyCacheService } from 'src/app/services/my-cache.service';
import { MyCorsProfileService } from 'src/app/services/my-cors-profile.service';
import { HttpProxyService } from 'src/app/services/http-proxy.service';
import { MyClientService } from 'src/app/services/my-client.service';
import { MyEndpointService } from 'src/app/services/my-endpoint.service';
import { Utility } from 'src/app/clazz/utility';
@Component({
  selector: 'app-endpoint',
  templateUrl: './endpoint.component.html',
  styleUrls: ['./endpoint.component.css']
})
export class EndpointComponent extends Aggregate<EndpointComponent, IEndpoint> implements OnInit, OnDestroy {
  bottomSheet: IBottomSheet<IEndpoint>;
  isExternal: boolean = undefined;
  isShared: boolean = undefined;
  isSecured: boolean = undefined;
  basicFormId: string = 'basicFormInfoId';
  basicFormInfo: IForm = BASIC_FORM_CONFIG;
  secureFormId: string = 'secureFormInfoId';
  secureFormInfo: IForm = SECURE_FORM_CONFIG;
  performanceFormId: string = 'performanceFormInfoId';
  performanceFormInfo: IForm = PERFORMANCE_FORM_CONFIG;
  performanceWarnning: boolean = false;
  constructor(
    public endpointSvc: MyEndpointService,
    public clientSvc: MyClientService,
    public corsSvc: MyCorsProfileService,
    public cacheSvc: MyCacheService,
    public httpProxySvc: HttpProxyService,
    fis: FormInfoService,
    @Inject(MAT_BOTTOM_SHEET_DATA) public data: any,
    bottomSheetRef: MatBottomSheetRef<EndpointComponent>,
    cdr: ChangeDetectorRef
  ) {
    super('endpointNew', CATALOG_FORM_CONFIG, new EndpointValidator(), bottomSheetRef, data, fis, cdr)
    this.bottomSheet = data;
    clientSvc.setProjectId(this.bottomSheet.params['projectId'])
    corsSvc.setProjectId(this.bottomSheet.params['projectId'])
    cacheSvc.setProjectId(this.bottomSheet.params['projectId'])
    this.fis.queryProvider[this.basicFormId + '_' + 'resourceId'] = this.getClients();
    this.fis.queryProvider[this.secureFormId + '_' + 'corsProfile'] = corsSvc;
    this.fis.queryProvider[this.performanceFormId + '_' + 'cacheProfile'] = cacheSvc;
    this.fis.init(this.formInfo, this.formId)
    this.fis.init(this.basicFormInfo, this.basicFormId)
    this.fis.init(this.secureFormInfo, this.secureFormId)
    this.fis.init(this.performanceFormInfo, this.performanceFormId)
    this.fis.formGroups[this.formId].get('isExternal').valueChanges.subscribe((next) => {
      console.dir('value is ' + next)
      const isExternal: boolean = next === 'yes' ? true : next === 'no' ? false : undefined
      this.isExternal = isExternal;
      if (isExternal === false) {
        this.fis.disableIfMatch(this.formId, ['isSecured', 'isShared'])//internal api does not require user authentication
        this.fis.formGroups[this.formId].get('isSecured').setValue('', { emitEvent: false })
      } else {
        if (!this.aggregate) {
          this.fis.enableIfMatch(this.formId, ['isSecured', 'isShared'])
        }
      }
    })
    this.fis.formGroups[this.formId].get('isShared').valueChanges.subscribe((next) => {
      const isShared: boolean = next === 'yes' ? true : next === 'no' ? false : undefined
      this.isShared = isShared;
    })
    this.fis.formGroups[this.formId].get('isSecured').valueChanges.subscribe((next) => {
      const isSecured: boolean = next === 'yes' ? true : next === 'no' ? false : undefined
      this.isSecured = isSecured;
    })
    this.fis.formGroups[this.basicFormId].get('method').valueChanges.subscribe(next => {
      if ((next as string).toLowerCase() === 'get') {
        this.fis.showIfMatch(this.performanceFormId, ['cacheProfile'])
      } else {
        this.fis.hideIfMatch(this.performanceFormId, ['cacheProfile'])
      }
    })
    this.fis.formGroups[this.basicFormId].get('isWebsocket').valueChanges.subscribe(next => {
      if (next === 'yes') {
        this.fis.hideIfMatch(this.basicFormId, ['method'])
        this.fis.hideIfMatch(this.secureFormId, ['csrf', 'cors'])
        this.fis.hideIfMatch(this.performanceFormId, ['replenishRate', 'burstCapacity'])
        this.performanceWarnning = false;
      } else {
        this.fis.showIfMatch(this.basicFormId, ['method'])
        this.fis.showIfMatch(this.secureFormId, ['csrf', 'cors'])
        this.fis.showIfMatch(this.performanceFormId, ['replenishRate', 'burstCapacity'])
        this.performanceWarnning = true;
      }
    })
    this.fis.formGroups[this.secureFormId].get('cors').valueChanges.subscribe(next => {
      if (next) {
        this.fis.showIfMatch(this.secureFormId, ['corsProfile'])
      } else {
        this.fis.hideIfMatch(this.secureFormId, ['corsProfile'])
      }
    })
    this.resume()
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
        this.fis.disableIfMatch(this.formId, ['isSecured', 'isExternal', 'isShared'])
      }
      combineLatest(var0).pipe(take(1))
        .subscribe(next => {
          let count = 0;
          (this.basicFormInfo.inputs.find(e => e.key === 'resourceId') as ISelectControl).options = next[count].data.map(e => <IOption>{ label: e.name, value: e.id })
          if (this.aggregate.corsProfileId) {
            count++;
            (this.secureFormInfo.inputs.find(e => e.key === 'corsProfile')as ISelectControl).options = next[count].data.map(e => <IOption>{ label: e.name, value: e.id })
          }
          if (this.aggregate.cacheProfileId) {
            count++;
            (this.performanceFormInfo.inputs.find(e => e.key === 'cacheProfile')as ISelectControl).options = next[count].data.map(e => <IOption>{ label: e.name, value: e.id })
          }
          this.fis.restore(this.basicFormId, this.aggregate, true);
          this.fis.formGroups[this.formId].get("isSecured").setValue(this.aggregate.secured ? 'yes' : 'no');
          this.fis.formGroups[this.formId].get("isShared").setValue(this.aggregate.shared ? 'yes' : 'no');
          this.fis.formGroups[this.formId].get("isExternal").setValue(this.aggregate.external ? 'yes' : 'no');
          this.fis.formGroups[this.secureFormId].get("csrf").setValue(this.aggregate.csrfEnabled);
          this.fis.formGroups[this.performanceFormId].get("replenishRate").setValue(this.aggregate.replenishRate);
          this.fis.formGroups[this.performanceFormId].get("burstCapacity").setValue(this.aggregate.burstCapacity);
          this.fis.formGroups[this.basicFormId].get("isWebsocket").setValue(this.aggregate.websocket ? 'yes' : 'no');
          if (this.aggregate.corsProfileId) {
            this.fis.formGroups[this.secureFormId].get("cors").setValue(true);
            this.fis.formGroups[this.secureFormId].get("corsProfile").setValue(this.aggregate.corsProfileId);
          }
          if (this.aggregate.cacheProfileId) {
            this.fis.formGroups[this.performanceFormId].get("cacheProfile").setValue(this.aggregate.cacheProfileId);
          }
          this.cdr.markForCheck()

        })

    }
  }
  ngOnInit() {
  }
  convertToPayload(cmpt: EndpointComponent): IEndpoint {
    let basicFormGroup = cmpt.fis.formGroups[cmpt.basicFormId];
    let secureFormGroup = cmpt.fis.formGroups[cmpt.secureFormId];
    let perFormGroup = cmpt.fis.formGroups[cmpt.performanceFormId];
    let catalogFormGroup = cmpt.fis.formGroups[cmpt.formId];
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
      corsProfileId: Utility.noEmptyString(secureFormGroup.get("corsProfile").value),
      cacheProfileId: basicFormGroup.get('method').value === 'GET' ? Utility.noEmptyString(perFormGroup.get("cacheProfile").value) : null,
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
  errorMapper(original: ErrorMessage[], cmpt: EndpointComponent) {
    return original.map(e => {
      return {
        ...e,
        formId: cmpt.formId
      }
    })
  }
}

