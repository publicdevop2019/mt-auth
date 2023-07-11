import { ChangeDetectorRef, Component, Inject, OnDestroy } from '@angular/core';
import { MatBottomSheetRef, MAT_BOTTOM_SHEET_DATA } from '@angular/material/bottom-sheet';
import { FormInfoService } from 'mt-form-builder';
import { IOption, IQueryProvider } from 'mt-form-builder/lib/classes/template.interface';
import { combineLatest, merge, Observable } from 'rxjs';
import { take } from 'rxjs/operators';
import { IBottomSheet } from 'src/app/clazz/summary.component';
import { IClient } from 'src/app/clazz/client.interface';
import { IEndpoint } from 'src/app/clazz/endpoint.interface';
import { BASIC_FORM_CONFIG, CATALOG_FORM_CONFIG, PERFORMANCE_FORM_CONFIG, SECURE_FORM_CONFIG } from 'src/app/form-configs/create-endpoint.config';
import { MyCacheService } from 'src/app/services/my-cache.service';
import { MyCorsProfileService } from 'src/app/services/my-cors-profile.service';
import { HttpProxyService } from 'src/app/services/http-proxy.service';
import { MyClientService } from 'src/app/services/my-client.service';
import { MyEndpointService } from 'src/app/services/my-endpoint.service';
import { Logger, Utility } from 'src/app/clazz/utility';
import { Validator } from 'src/app/clazz/validator-next-common';
@Component({
  selector: 'app-endpoint',
  templateUrl: './endpoint.component.html',
  styleUrls: ['./endpoint.component.css']
})
export class EndpointComponent implements OnDestroy {
  changeId: string = Utility.getChangeId();
  formId: string = 'endpointNew';
  allowError: boolean = false;
  isExternal: boolean = undefined;
  isShared: boolean = undefined;
  isSecured: boolean = undefined;
  basicFormId: string = 'basicFormInfoId';
  secureFormId: string = 'secureFormInfoId';
  performanceFormId: string = 'performanceFormInfoId';
  performanceWarnning: boolean = false;
  constructor(
    public endpointSvc: MyEndpointService,
    public clientSvc: MyClientService,
    public corsSvc: MyCorsProfileService,
    public cacheSvc: MyCacheService,
    public httpProxySvc: HttpProxyService,
    public fis: FormInfoService,
    @Inject(MAT_BOTTOM_SHEET_DATA) public data: IBottomSheet<IEndpoint>,
    public bottomSheetRef: MatBottomSheetRef<EndpointComponent>,
    cdr: ChangeDetectorRef
  ) {
    clientSvc.setProjectId(this.data.params['projectId'])
    corsSvc.setProjectId(this.data.params['projectId'])
    cacheSvc.setProjectId(this.data.params['projectId'])
    this.fis.queryProvider[this.basicFormId + '_' + 'resourceId'] = this.getClients();
    this.fis.queryProvider[this.secureFormId + '_' + 'corsProfile'] = corsSvc;
    this.fis.queryProvider[this.performanceFormId + '_' + 'cacheProfile'] = cacheSvc;
    this.fis.init(CATALOG_FORM_CONFIG, this.formId)
    this.fis.init(BASIC_FORM_CONFIG, this.basicFormId)
    this.fis.init(SECURE_FORM_CONFIG, this.secureFormId)
    this.fis.init(PERFORMANCE_FORM_CONFIG, this.performanceFormId);
    if (data.context === 'new') {
      merge(
        this.fis.formGroups[this.formId].valueChanges,
        this.fis.formGroups[this.basicFormId].valueChanges,
        this.fis.formGroups[this.secureFormId].valueChanges,
        this.fis.formGroups[this.performanceFormId].valueChanges
      ).subscribe(() => {
        Logger.trace('validating create form')
        if (this.allowError) {
          this.validateCreateForm()
        }
      })
    }
    if (data.context === 'edit') {
      merge(
        this.fis.formGroups[this.formId].valueChanges,
        this.fis.formGroups[this.basicFormId].valueChanges,
        this.fis.formGroups[this.secureFormId].valueChanges,
        this.fis.formGroups[this.performanceFormId].valueChanges
      ).subscribe(() => {
        Logger.trace('validating update form')
        if (this.allowError) {
          this.validateUpdateForm()
        }
      })
    }
    this.fis.formGroups[this.formId].get('isExternal').valueChanges.subscribe((next) => {
      const isExternal: boolean = next === 'yes' ? true : next === 'no' ? false : undefined
      this.isExternal = isExternal;
      if (isExternal === false) {
        this.fis.disableIfMatch(this.formId, ['isSecured', 'isShared'])//internal api does not require user authentication
        this.fis.formGroups[this.formId].get('isSecured').setValue('', { emitEvent: false })
      } else {
        if (!this.data.from) {
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
        return this.httpProxySvc.readEntityByQuery<IClient>(this.clientSvc.entityRepo, num, size, `projectIds:${this.data.params['projectId']},resourceIndicator:1`, by, order, header)
      }
    } as IQueryProvider
  }
  ngOnDestroy(): void {
    this.fis.reset(this.formId)
    this.fis.reset(this.basicFormId)
    this.fis.reset(this.secureFormId)
    this.fis.reset(this.performanceFormId)
  }
  resume(): void {
    if (this.data.from) {
      const var0: Observable<any>[] = [];
      var0.push(this.clientSvc.readEntityByQuery(0, 1, 'id:' + this.data.from.resourceId))
      if (this.data.from.corsProfileId) {
        var0.push(this.corsSvc.readEntityByQuery(0, 1, 'id:' + this.data.from.corsProfileId))
      }
      if (this.data.from.cacheProfileId) {
        var0.push(this.cacheSvc.readEntityByQuery(0, 1, 'id:' + this.data.from.cacheProfileId))
      }
      if (this.data.context !== 'clone') {
        this.fis.disableIfMatch(this.formId, ['isSecured', 'isExternal', 'isShared'])
      }
      combineLatest(var0).pipe(take(1))
        .subscribe(next => {
          let count = 0;
          this.fis.updateOption(this.basicFormId, 'resourceId', next[count].data.map(e => <IOption>{ label: e.name, value: e.id }))
          if (this.data.from.corsProfileId) {
            count++;
            this.fis.updateOption(this.secureFormId, 'corsProfile', next[count].data.map(e => <IOption>{ label: e.name, value: e.id }))
          }
          if (this.data.from.cacheProfileId) {
            count++;
            this.fis.updateOption(this.performanceFormId, 'cacheProfile', next[count].data.map(e => <IOption>{ label: e.name, value: e.id }))
          }
          this.fis.restore(this.basicFormId, this.data.from, true);
          this.fis.formGroups[this.formId].get("isSecured").setValue(this.data.from.secured ? 'yes' : 'no');
          this.fis.formGroups[this.formId].get("isShared").setValue(this.data.from.shared ? 'yes' : 'no');
          this.fis.formGroups[this.formId].get("isExternal").setValue(this.data.from.external ? 'yes' : 'no');
          this.fis.formGroups[this.secureFormId].get("csrf").setValue(this.data.from.csrfEnabled);
          this.fis.formGroups[this.performanceFormId].get("replenishRate").setValue(this.data.from.replenishRate);
          this.fis.formGroups[this.performanceFormId].get("burstCapacity").setValue(this.data.from.burstCapacity);
          this.fis.formGroups[this.basicFormId].get("isWebsocket").setValue(this.data.from.websocket ? 'yes' : 'no');
          if (this.data.from.corsProfileId) {
            this.fis.formGroups[this.secureFormId].get("cors").setValue(true);
            this.fis.formGroups[this.secureFormId].get("corsProfile").setValue(this.data.from.corsProfileId);
          }
          if (this.data.from.cacheProfileId) {
            this.fis.formGroups[this.performanceFormId].get("cacheProfile").setValue(this.data.from.cacheProfileId);
          }
        })

    }
  }
  convertToPayload(): IEndpoint {
    const basicFormGroup = this.fis.formGroups[this.basicFormId];
    const secureFormGroup = this.fis.formGroups[this.secureFormId];
    const perFormGroup = this.fis.formGroups[this.performanceFormId];
    const catalogFormGroup = this.fis.formGroups[this.formId];
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
      version: this.data.from && this.data.from.version
    }
  }
  update() {
    this.allowError = true;
    if (this.validateUpdateForm()) {
      const payload = this.convertToPayload()
      this.endpointSvc.update(this.data.from.id, payload, this.changeId)
    }
  }
  create() {
    this.allowError = true;
    if (this.validateCreateForm()) {
      const payload = this.convertToPayload()
      this.endpointSvc.create(payload, this.changeId)
    }
  }

  private validateCreateForm() {
    const fg = this.fis.formGroups[this.formId];

    const var0 = Validator.exist(fg.get('isExternal').value)
    this.fis.updateError(this.formId, 'isExternal', var0.errorMsg)
    if (fg.get('isExternal').value === 'yes') {
      const var1 = Validator.exist(fg.get('isShared').value)
      this.fis.updateError(this.formId, 'isShared', var1.errorMsg)

      const var2 = Validator.exist(fg.get('isSecured').value)
      this.fis.updateError(this.formId, 'isSecured', var2.errorMsg)
      const var3 = this.validateOthers()
      return !var0.errorMsg && !var1.errorMsg && !var2.errorMsg && var3
    } else {
      this.fis.updateError(this.formId, 'isShared', undefined)
      this.fis.updateError(this.formId, 'isSecured', undefined)
      const result = this.validateOthers()
      return !var0.errorMsg && result
    }
  }

  private validateOthers() {
    Logger.debug('validating others')
    const fg = this.fis.formGroups[this.basicFormId];
    const formId = this.basicFormId
    const var0 = Validator.exist(fg.get('name').value)
    this.fis.updateError(formId, 'name', var0.errorMsg)

    const var1 = Validator.exist(fg.get('resourceId').value)
    this.fis.updateError(formId, 'resourceId', var1.errorMsg)

    const var2 = Validator.exist(fg.get('isWebsocket').value)
    this.fis.updateError(formId, 'isWebsocket', var2.errorMsg)

    const var3 = Validator.exist(fg.get('path').value)
    this.fis.updateError(formId, 'path', var3.errorMsg)

    let method = true;
    let burst = true;
    let capacity = true;
    if (fg.get('isWebsocket').value === 'no') {
      const var4 = Validator.exist(fg.get('method').value)
      this.fis.updateError(formId, 'method', var4.errorMsg)
      method = !var4.errorMsg

      const var5 = Validator.exist(this.fis.formGroups[this.performanceFormId].get('replenishRate').value)
      this.fis.updateError(this.performanceFormId, 'replenishRate', var5.errorMsg)
      burst = !var5.errorMsg

      const var6 = Validator.exist(this.fis.formGroups[this.performanceFormId].get('burstCapacity').value)
      this.fis.updateError(this.performanceFormId, 'burstCapacity', var6.errorMsg)
      capacity = !var6.errorMsg
    } else {
      //reset error
      this.fis.updateError(formId, 'method', undefined)
      this.fis.updateError(formId, 'replenishRate', undefined)
      this.fis.updateError(formId, 'burstCapacity', undefined)
      this.fis.updateError(this.secureFormId, 'corsProfile', undefined)
    }
    let cors = true;
    if (this.fis.formGroups[this.secureFormId].get('cors').value) {
      const var4 = Validator.exist(this.fis.formGroups[this.secureFormId].get('corsProfile').value)
      this.fis.updateError(this.secureFormId, 'corsProfile', var4.errorMsg)
      cors = !var4.errorMsg
    } else {
      this.fis.updateError(this.secureFormId, 'corsProfile', undefined)
    }
    const result = !var0.errorMsg && !var1.errorMsg && !var2.errorMsg && !var3.errorMsg && method && burst && capacity && cors
    Logger.debug('validating others result {}', result)
    return result;
  }

  private validateUpdateForm() {
    return this.validateOthers()
  }

  dismiss(event: MouseEvent) {
    this.bottomSheetRef.dismiss();
    event.preventDefault();
  }
}

