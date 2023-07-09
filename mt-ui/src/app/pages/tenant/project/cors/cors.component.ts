import { ChangeDetectorRef, Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { MatBottomSheetRef, MAT_BOTTOM_SHEET_DATA } from '@angular/material/bottom-sheet';
import { FormInfoService } from 'mt-form-builder';
import { IForm } from 'mt-form-builder/lib/classes/template.interface';
import { Aggregate } from 'src/app/clazz/abstract-aggregate';
import { IBottomSheet } from 'src/app/clazz/summary.component';
import { ICorsProfile } from 'src/app/clazz/validation/aggregate/cors/interface-cors';
import { CORSProfileValidator } from 'src/app/clazz/validation/aggregate/cors/validator-cors';
import { ErrorMessage, hasValue } from 'src/app/clazz/validation/validator-common';
import { ALLOWED_HEADERS_FORM_CONFIG, EXPOSED_HEADERS_FORM_CONFIG, FORM_CONFIG, ORIGIN_FORM_CONFIG } from 'src/app/form-configs/cors.config';
import { MyCorsProfileService } from 'src/app/services/my-cors-profile.service';
@Component({
  selector: 'app-cors',
  templateUrl: './cors.component.html',
  styleUrls: ['./cors.component.css']
})
export class CorsComponent extends Aggregate<CorsComponent, ICorsProfile> implements OnInit, OnDestroy {
  bottomSheet: IBottomSheet<ICorsProfile>;
  originFormId: string = 'originFormId'
  originFormInfo: IForm = ORIGIN_FORM_CONFIG;
  allowedHeaderFormId: string = 'allowedHeaderFormId'
  allowedHeaderFormInfo: IForm = ALLOWED_HEADERS_FORM_CONFIG;
  exposedHeaderFormId: string = 'exposedHeaderFormId'
  exposedHeaderFormInfo: IForm = EXPOSED_HEADERS_FORM_CONFIG;
  constructor(
    public entityService: MyCorsProfileService,
    fis: FormInfoService,
    @Inject(MAT_BOTTOM_SHEET_DATA) public data: any,
    bottomSheetRef: MatBottomSheetRef<CorsComponent>,
    cdr: ChangeDetectorRef
  ) {
    super('cors-form', FORM_CONFIG, new CORSProfileValidator(), bottomSheetRef, data, fis, cdr)

    this.bottomSheet = data;
    this.fis.init(this.formInfo, this.formId);
    this.fis.init(ORIGIN_FORM_CONFIG, this.originFormId);
    this.fis.init(ALLOWED_HEADERS_FORM_CONFIG, this.allowedHeaderFormId);
    this.fis.init(EXPOSED_HEADERS_FORM_CONFIG, this.exposedHeaderFormId);
    if (this.aggregate) {
      this.fis.formGroups[this.formId].get('id').setValue(this.aggregate.id)
      this.fis.formGroups[this.formId].get('name').setValue(this.aggregate.name)
      this.fis.formGroups[this.formId].get('description').setValue(this.aggregate.description||'')
      this.fis.formGroups[this.formId].get('allowCredentials').setValue(this.aggregate.allowCredentials)
      this.fis.restoreDynamicForm(this.allowedHeaderFormId, this.fis.parsePayloadArr(this.aggregate.allowedHeaders, 'allowedHeaders'), this.aggregate.allowedHeaders.length)
      this.fis.restoreDynamicForm(this.originFormId, this.fis.parsePayloadArr(this.aggregate.allowOrigin, 'allowOrigin'), this.aggregate.allowOrigin.length)
      this.fis.restoreDynamicForm(this.exposedHeaderFormId, this.fis.parsePayloadArr(this.aggregate.exposedHeaders, 'exposedHeaders'), this.aggregate.exposedHeaders.length)
      this.fis.formGroups[this.formId].get('maxAge').setValue(this.aggregate.maxAge)
    }

  }
  ngOnDestroy(): void {
    this.cleanUp()
    this.fis.reset(this.originFormId)
    this.fis.reset(this.allowedHeaderFormId)
    this.fis.reset(this.exposedHeaderFormId)
  }
  ngOnInit() {
  }
  convertToPayload(cmpt: CorsComponent): ICorsProfile {
    let formGroup = cmpt.fis.formGroups[cmpt.formId];
    const fg0 = cmpt.fis.formGroups[cmpt.originFormId]
    const fg1 = cmpt.fis.formGroups[cmpt.allowedHeaderFormId]
    const fg2 = cmpt.fis.formGroups[cmpt.exposedHeaderFormId]
    return {
      id: formGroup.get('id').value,
      name: formGroup.get('name').value,
      description: hasValue(formGroup.get('description').value) ? formGroup.get('description').value : undefined,
      allowCredentials: !!formGroup.get('allowCredentials').value,
      allowOrigin: (Object.values(fg0.value) as string[]).filter(e => e),
      allowedHeaders: (Object.values(fg1.value) as string[]).filter(e => e),
      exposedHeaders: (Object.values(fg2.value) as string[]).filter(e => e),
      maxAge: +formGroup.get('maxAge').value,
      version: cmpt.aggregate && cmpt.aggregate.version
    }
  }
  update() {
    if (this.validateHelper.validate(this.validator, this.convertToPayload, 'update', this.fis, this, this.errorMapper))
      this.entityService.update(this.aggregate.id, this.convertToPayload(this), this.changeId)
  }
  create() {
    if (this.validateHelper.validate(this.validator, this.convertToPayload, 'create', this.fis, this, this.errorMapper)) {
      this.entityService.create(this.convertToPayload(this), this.changeId)
    }
  }
  errorMapper(original: ErrorMessage[], cmpt: CorsComponent) {
    return original.map(e => {
      return {
        ...e,
        formId: cmpt.formId
      }
    })
  }
}
