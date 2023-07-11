import { Component, Inject, OnDestroy } from '@angular/core';
import { MatBottomSheetRef, MAT_BOTTOM_SHEET_DATA } from '@angular/material/bottom-sheet';
import { FormInfoService } from 'mt-form-builder';
import { IForm } from 'mt-form-builder/lib/classes/template.interface';
import { IBottomSheet } from 'src/app/clazz/summary.component';
import { Utility } from 'src/app/clazz/utility';
import { ICorsProfile } from 'src/app/clazz/cors.interface';
import { hasValue } from 'src/app/clazz/validator-common';
import { Validator } from 'src/app/clazz/validator-next-common';
import { ALLOWED_HEADERS_FORM_CONFIG, EXPOSED_HEADERS_FORM_CONFIG, FORM_CONFIG, ORIGIN_FORM_CONFIG } from 'src/app/form-configs/cors.config';
import { MyCorsProfileService } from 'src/app/services/my-cors-profile.service';
@Component({
  selector: 'app-cors',
  templateUrl: './cors.component.html',
  styleUrls: ['./cors.component.css']
})
export class CorsComponent implements OnDestroy {
  formId: string='corsForm';
  allowError: boolean = false;
  changeId: string = Utility.getChangeId();

  originFormId: string = 'originFormId'
  originFormInfo: IForm = ORIGIN_FORM_CONFIG;
  allowedHeaderFormId: string = 'allowedHeaderFormId'
  allowedHeaderFormInfo: IForm = ALLOWED_HEADERS_FORM_CONFIG;
  exposedHeaderFormId: string = 'exposedHeaderFormId'
  exposedHeaderFormInfo: IForm = EXPOSED_HEADERS_FORM_CONFIG;
  constructor(
    public entityService: MyCorsProfileService,
    public fis: FormInfoService,
    @Inject(MAT_BOTTOM_SHEET_DATA) public data: IBottomSheet<ICorsProfile>,
    public bottomSheetRef: MatBottomSheetRef<CorsComponent>,
  ) {
    this.fis.init(FORM_CONFIG, this.formId);
    this.fis.init(ORIGIN_FORM_CONFIG, this.originFormId);
    this.fis.init(ALLOWED_HEADERS_FORM_CONFIG, this.allowedHeaderFormId);
    this.fis.init(EXPOSED_HEADERS_FORM_CONFIG, this.exposedHeaderFormId);
    this.fis.formGroups[this.formId].valueChanges.subscribe(() => {
      if (this.allowError) {
        this.validateForm()
      }
    })
    if (this.data.from) {
      this.fis.formGroups[this.formId].get('id').setValue(this.data.from.id)
      this.fis.formGroups[this.formId].get('name').setValue(this.data.from.name)
      this.fis.formGroups[this.formId].get('description').setValue(this.data.from.description || '')
      this.fis.formGroups[this.formId].get('allowCredentials').setValue(this.data.from.allowCredentials)
      this.fis.restoreDynamicForm(this.allowedHeaderFormId, this.fis.parsePayloadArr(this.data.from.allowedHeaders, 'allowedHeaders'), this.data.from.allowedHeaders.length)
      this.fis.restoreDynamicForm(this.originFormId, this.fis.parsePayloadArr(this.data.from.allowOrigin, 'allowOrigin'), this.data.from.allowOrigin.length)
      this.fis.restoreDynamicForm(this.exposedHeaderFormId, this.fis.parsePayloadArr(this.data.from.exposedHeaders, 'exposedHeaders'), this.data.from.exposedHeaders.length)
      this.fis.formGroups[this.formId].get('maxAge').setValue(this.data.from.maxAge)
    }

  }
  ngOnDestroy(): void {
    this.fis.reset(this.formId)
    this.fis.reset(this.originFormId)
    this.fis.reset(this.allowedHeaderFormId)
    this.fis.reset(this.exposedHeaderFormId)
  }
  update() {
    if (this.validateForm()) {
      this.entityService.update(this.data.from.id, this.convertToPayload(), this.changeId)
    }
  }
  create() {
    if (this.validateForm()) {
      this.entityService.create(this.convertToPayload(), this.changeId)
    }
  }
  dismiss(event: MouseEvent) {
    this.bottomSheetRef.dismiss();
    event.preventDefault();
  }
  private convertToPayload(): ICorsProfile {
    let formGroup = this.fis.formGroups[this.formId];
    const fg0 = this.fis.formGroups[this.originFormId]
    const fg1 = this.fis.formGroups[this.allowedHeaderFormId]
    const fg2 = this.fis.formGroups[this.exposedHeaderFormId]
    return {
      id: formGroup.get('id').value,
      name: formGroup.get('name').value,
      description: hasValue(formGroup.get('description').value) ? formGroup.get('description').value : undefined,
      allowCredentials: !!formGroup.get('allowCredentials').value,
      allowOrigin: (Object.values(fg0.value) as string[]).filter(e => e),
      allowedHeaders: (Object.values(fg1.value) as string[]).filter(e => e),
      exposedHeaders: (Object.values(fg2.value) as string[]).filter(e => e),
      maxAge: +formGroup.get('maxAge').value,
      version: this.data.from && this.data.from.version
    }
  }
  private validateForm() {
    const fg = this.fis.formGroups[this.formId];
    const var0 = Validator.exist(fg.get('name').value)
    this.fis.updateError(this.formId, 'name', var0.errorMsg)
    return !var0.errorMsg
  }
}
