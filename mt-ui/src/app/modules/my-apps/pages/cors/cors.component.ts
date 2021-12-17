import { AfterViewInit, ChangeDetectorRef, Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { MatBottomSheetRef, MAT_BOTTOM_SHEET_DATA } from '@angular/material/bottom-sheet';
import { FormInfoService } from 'mt-form-builder';
import { IForm } from 'mt-form-builder/lib/classes/template.interface';
import { Aggregate } from 'src/app/clazz/abstract-aggregate';
import { IBottomSheet } from 'src/app/clazz/summary.component';
import { ICorsProfile } from 'src/app/clazz/validation/aggregate/cors/interface-cors';
import { CORSProfileValidator } from 'src/app/clazz/validation/aggregate/cors/validator-cors';
import { ErrorMessage } from 'src/app/clazz/validation/validator-common';
import { ALLOWED_HEADERS_FORM_CONFIG, EXPOSED_HEADERS_FORM_CONFIG, FORM_CONFIG, ORIGIN_FORM_CONFIG } from 'src/app/form-configs/cors.config';
import { CORSProfileService } from 'src/app/services/cors-profile.service';
@Component({
  selector: 'app-cors',
  templateUrl: './cors.component.html',
  styleUrls: ['./cors.component.css']
})
export class CorsComponent extends Aggregate<CorsComponent, ICorsProfile> implements OnInit, AfterViewInit, OnDestroy {
  bottomSheet: IBottomSheet<ICorsProfile>;
  originFormId: string = 'originFormId'
  originFormInfo: IForm = JSON.parse(JSON.stringify(ORIGIN_FORM_CONFIG));
  allowedHeaderFormId: string = 'allowedHeaderFormId'
  allowedHeaderFormInfo: IForm = JSON.parse(JSON.stringify(ALLOWED_HEADERS_FORM_CONFIG));
  exposedHeaderFormId: string = 'exposedHeaderFormId'
  exposedHeaderFormInfo: IForm = JSON.parse(JSON.stringify(EXPOSED_HEADERS_FORM_CONFIG));
  constructor(
    public entityService: CORSProfileService,
    fis: FormInfoService,
    @Inject(MAT_BOTTOM_SHEET_DATA) public data: any,
    bottomSheetRef: MatBottomSheetRef<CorsComponent>,
    cdr: ChangeDetectorRef
  ) {
    super('cors-form', JSON.parse(JSON.stringify(FORM_CONFIG)), new CORSProfileValidator(), bottomSheetRef, data, fis, cdr)

    this.bottomSheet = data;
  }
  ngAfterViewInit(): void {
    if (this.aggregate) {
      this.fis.formGroupCollection[this.formId].get('id').setValue(this.aggregate.id)
      this.fis.formGroupCollection[this.formId].get('name').setValue(this.aggregate.name)
      this.fis.formGroupCollection[this.formId].get('description').setValue(this.aggregate.description)
      this.fis.formGroupCollection[this.formId].get('allowCredentials').setValue(this.aggregate.allowCredentials)
      this.fis.restoreDynamicForm(this.allowedHeaderFormId, this.fis.parsePayloadArr(this.aggregate.allowedHeaders, 'allowedHeaders'), this.aggregate.allowedHeaders.length)
      this.fis.restoreDynamicForm(this.originFormId, this.fis.parsePayloadArr(this.aggregate.allowOrigin, 'allowOrigin'), this.aggregate.allowOrigin.length)
      this.fis.restoreDynamicForm(this.exposedHeaderFormId, this.fis.parsePayloadArr(this.aggregate.exposedHeaders, 'exposedHeaders'), this.aggregate.exposedHeaders.length)

      this.fis.formGroupCollection[this.formId].get('maxAge').setValue(this.aggregate.maxAge)
      this.cdr.markForCheck()
    }
  }
  ngOnDestroy(): void {
    this.cleanUp()
  }
  ngOnInit() {
  }
  convertToPayload(cmpt: CorsComponent): ICorsProfile {
    let formGroup = cmpt.fis.formGroupCollection[cmpt.formId];
    const fg0 = cmpt.fis.formGroupCollection[cmpt.originFormId]
    const fg1 = cmpt.fis.formGroupCollection[cmpt.allowedHeaderFormId]
    const fg2 = cmpt.fis.formGroupCollection[cmpt.exposedHeaderFormId]
    return {
      id: formGroup.get('id').value,
      name: formGroup.get('name').value,
      description: formGroup.get('description').value,
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
