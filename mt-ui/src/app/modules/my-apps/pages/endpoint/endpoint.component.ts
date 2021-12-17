import { AfterViewInit, ChangeDetectorRef, Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { MatBottomSheetRef, MAT_BOTTOM_SHEET_DATA } from '@angular/material/bottom-sheet';
import { FormInfoService } from 'mt-form-builder';
import { IOption } from 'mt-form-builder/lib/classes/template.interface';
import { combineLatest, Observable } from 'rxjs';
import { filter, take } from 'rxjs/operators';
import { Aggregate } from 'src/app/clazz/abstract-aggregate';
import { noEmptyString } from 'src/app/clazz/common.utility';
import { IBottomSheet } from 'src/app/clazz/summary.component';
import { IEndpoint } from 'src/app/clazz/validation/aggregate/endpoint/interfaze-endpoint';
import { EndpointValidator } from 'src/app/clazz/validation/aggregate/endpoint/validator-endpoint';
import { ErrorMessage, hasValue } from 'src/app/clazz/validation/validator-common';
import { FORM_CONFIG } from 'src/app/form-configs/endpoint.config';
import { CacheService } from 'src/app/services/cache.service';
import { ClientService } from 'src/app/services/client.service';
import { CORSProfileService } from 'src/app/services/cors-profile.service';
import { EndpointService } from 'src/app/services/endpoint.service';
import { RoleService } from 'src/app/services/role.service';
import * as UUID from 'uuid/v1';
@Component({
  selector: 'app-endpoint',
  templateUrl: './endpoint.component.html',
  styleUrls: ['./endpoint.component.css']
})
export class EndpointComponent extends Aggregate<EndpointComponent, IEndpoint> implements OnInit, OnDestroy {
  endpointBottomSheetData: IBottomSheet<IEndpoint>;
  constructor(
    public endpointSvc: EndpointService,
    public clientSvc: ClientService,
    public roleSvc: RoleService,
    public corsSvc: CORSProfileService,
    public cacheSvc: CacheService,
    fis: FormInfoService,
    @Inject(MAT_BOTTOM_SHEET_DATA) public data: any,
    bottomSheetRef: MatBottomSheetRef<EndpointComponent>,
    cdr: ChangeDetectorRef
  ) {
    super('securityProfile', JSON.parse(JSON.stringify(FORM_CONFIG)), new EndpointValidator(), bottomSheetRef, data, fis, cdr)
    this.endpointBottomSheetData = data;
    this.fis.queryProvider[this.formId + '_' + 'resourceId'] = clientSvc;
    this.fis.queryProvider[this.formId + '_' + 'corsProfile'] = corsSvc;
    this.fis.queryProvider[this.formId + '_' + 'role'] = roleSvc;
    this.fis.queryProvider[this.formId + '_' + 'cacheProfile'] = cacheSvc;
    this.fis.formCreated(this.formId)
      .subscribe(next => {
        this.fis.formGroupCollection[this.formId].get('secured').valueChanges.subscribe(next=>{
          if(next){
            this.fis.showIfMatch(this.formId,['role'])
          }else{
            this.fis.hideIfMatch(this.formId,['role'])
          }
        })
        this.fis.formGroupCollection[this.formId].get('method').valueChanges.subscribe(next=>{
          if((next as string).toLowerCase()==='get'){
            this.fis.showIfMatch(this.formId,['cacheProfile'])
          }else{
            this.fis.hideIfMatch(this.formId,['cacheProfile'])
          }
        })
        this.fis.formGroupCollection[this.formId].get('isWebsocket').valueChanges.subscribe(next=>{
          if(next==='yes'){
            this.fis.hideIfMatch(this.formId,['csrf', 'cors','method'])
          }else{
            this.fis.showIfMatch(this.formId,['csrf', 'cors','method'])
          }
        })
        this.fis.formGroupCollection[this.formId].get('cors').valueChanges.subscribe(next=>{
          if(next){
            this.fis.showIfMatch(this.formId,['corsProfile'])
          }else{
            this.fis.hideIfMatch(this.formId,['corsProfile'])
          }
        })
        this.resume()
      })
  }
  ngOnDestroy(): void {
    this.cleanUp()
  }
  resume(): void {
    if (this.aggregate) {
      const var0: Observable<any>[] = [];
      var0.push(this.clientSvc.readEntityByQuery(0, 1, 'id:' + this.aggregate.resourceId))
      if (this.aggregate.corsProfileId) {
        var0.push(this.corsSvc.readEntityByQuery(0, 1, 'id:' + this.aggregate.corsProfileId))
      }
      if (this.aggregate.roleId) {
        var0.push(this.roleSvc.readEntityByQuery(0, 1, 'id:' + this.aggregate.roleId))
      }
      if (this.aggregate.cacheProfileId) {
        var0.push(this.cacheSvc.readEntityByQuery(0, 1, 'id:' + this.aggregate.cacheProfileId))
      }
      combineLatest(var0).pipe(take(1))
        .subscribe(next => {
          let count=0;
          this.formInfo.inputs.find(e => e.key === 'resourceId').options = next[count].data.map(e => <IOption>{ label: e.name, value: e.id })
          if (this.aggregate.corsProfileId) {
            count++;
            this.formInfo.inputs.find(e => e.key === 'corsProfile').options = next[count].data.map(e => <IOption>{ label: e.name, value: e.id })
          }
          if (this.aggregate.roleId) {
            count++;
            this.formInfo.inputs.find(e => e.key === 'role').options = next[count].data.map(e => <IOption>{ label: e.name, value: e.id })
          }
          if (this.aggregate.cacheProfileId) {
            count++;
            this.formInfo.inputs.find(e => e.key === 'cacheProfile').options = next[count].data.map(e => <IOption>{ label: e.name, value: e.id })
          }
          this.fis.restore(this.formId, this.aggregate,true);
          this.fis.formGroupCollection[this.formId].get("secured").setValue(this.aggregate.secured);
          this.fis.formGroupCollection[this.formId].get("csrf").setValue(this.aggregate.csrfEnabled);
          this.fis.formGroupCollection[this.formId].get("isWebsocket").setValue(this.aggregate.websocket ? 'yes' : 'no');
          if (this.aggregate.corsProfileId) {
            this.fis.formGroupCollection[this.formId].get("cors").setValue(true);
            this.fis.formGroupCollection[this.formId].get("corsProfile").setValue(this.aggregate.corsProfileId);
          }
          if (this.aggregate.roleId) {
            this.fis.formGroupCollection[this.formId].get("role").setValue(this.aggregate.roleId);
          }
          if (this.aggregate.cacheProfileId) {
            this.fis.formGroupCollection[this.formId].get("cacheProfile").setValue(this.aggregate.cacheProfileId);
          }
          this.cdr.markForCheck()
        })

    }
  }
  ngOnInit() {
  }
  convertToPayload(cmpt: EndpointComponent): IEndpoint {
    let formGroup = cmpt.fis.formGroupCollection[cmpt.formId];
    const secured = !!formGroup.get('secured').value;
    return {
      id: formGroup.get('id').value,
      description: formGroup.get('description').value ? formGroup.get('description').value : null,
      resourceId: formGroup.get('resourceId').value,
      path: formGroup.get('path').value,
      method: formGroup.get('method').value,
      secured: secured,
      websocket: formGroup.get('isWebsocket').value === 'yes',
      csrfEnabled: !!formGroup.get('csrf').value,
      corsProfileId: noEmptyString(formGroup.get("corsProfile").value),
      roleId: noEmptyString(formGroup.get("role").value),
      cacheProfileId: noEmptyString(formGroup.get("cacheProfile").value),
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

