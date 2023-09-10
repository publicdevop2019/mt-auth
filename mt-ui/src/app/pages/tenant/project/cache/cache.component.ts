import { Component, Inject, OnDestroy } from '@angular/core';
import { MatBottomSheetRef, MAT_BOTTOM_SHEET_DATA } from '@angular/material/bottom-sheet';
import { FormInfoService } from 'mt-form-builder';
import { IDomainContext } from 'src/app/clazz/summary.component';
import { Utility } from 'src/app/misc/utility';
import { Validator } from 'src/app/misc/validator';
import { FORM_CONFIG } from 'src/app/form-configs/cache.config';
import { ICacheProfile } from 'src/app/misc/interface';
import { MyCacheService } from 'src/app/services/my-cache.service';
@Component({
  selector: 'app-cache',
  templateUrl: './cache.component.html',
  styleUrls: ['./cache.component.css']
})
export class CacheComponent implements OnDestroy {
  formId: string='cacheForm';
  allowError: boolean = false;
  changeId: string = Utility.getChangeId();

  constructor(
    public entityService: MyCacheService,
    public fis: FormInfoService,
    @Inject(MAT_BOTTOM_SHEET_DATA) public data: IDomainContext<ICacheProfile>,
    public bottomSheetRef: MatBottomSheetRef<CacheComponent>,
  ) {
    this.fis.init(FORM_CONFIG, this.formId);
    this.fis.formGroups[this.formId].valueChanges.subscribe(() => {
      if (this.allowError) {
        this.validateForm()
      }
    })
    this.fis.formGroups[this.formId].get('allowCache').valueChanges.subscribe(next => {
      if (next === 'yes') {
        this.fis.showIfMatch(this.formId, ['cacheControl', 'vary', 'expires', 'etagValidation']);
      } else {
        this.fis.hideIfNotMatch(this.formId, ['name', 'description', 'allowCache']);
        this.fis.formGroups[this.formId].get('cacheControl').reset([])
        this.fis.formGroups[this.formId].get('etagValidation').reset(false)
        this.fis.formGroups[this.formId].get('etagType').reset(false)
        this.fis.formGroups[this.formId].get('maxAgeValue').reset()
        this.fis.formGroups[this.formId].get('smaxAgeValue').reset()
        this.fis.formGroups[this.formId].get('vary').reset()
        this.fis.formGroups[this.formId].get('expires').reset()
      }
    })
    this.fis.formGroups[this.formId].get('cacheControl').valueChanges.subscribe(next => {
      if (next.includes('max-age')) {
        this.fis.showIfMatch(this.formId, ['maxAgeValue']);
      } else {
        this.fis.hideIfMatch(this.formId, ['maxAgeValue']);
      }
      if (next.includes('s-maxage')) {
        this.fis.showIfMatch(this.formId, ['smaxAgeValue']);
      } else {
        this.fis.hideIfMatch(this.formId, ['smaxAgeValue']);
      }
    })
    this.fis.formGroups[this.formId].get('etagValidation').valueChanges.subscribe(next => {
      if (next) {
        this.fis.showIfMatch(this.formId, ['etagType']);
      } else {
        this.fis.hideIfMatch(this.formId, ['etagType']);
      }
    })
    if (this.data.from) {
      this.fis.formGroups[this.formId].get('id').setValue(this.data.from.id)
      this.fis.formGroups[this.formId].get('name').setValue(this.data.from.name)
      this.fis.formGroups[this.formId].get('description').setValue(this.data.from.description ? this.data.from.description : '')
      this.fis.formGroups[this.formId].get('allowCache').setValue(this.data.from.allowCache ? 'yes' : 'no')
      this.fis.formGroups[this.formId].get('cacheControl').setValue(this.data.from.cacheControl)
      this.fis.formGroups[this.formId].get('maxAgeValue').setValue(this.data.from.maxAge || '')
      this.fis.formGroups[this.formId].get('smaxAgeValue').setValue(this.data.from.smaxAge || '')
      this.fis.formGroups[this.formId].get('etagValidation').setValue(this.data.from.etag)
      this.fis.formGroups[this.formId].get('etagType').setValue(this.data.from.weakValidation)
      this.fis.formGroups[this.formId].get('expires').setValue(this.data.from.expires ? this.data.from.expires : '')
      this.fis.formGroups[this.formId].get('vary').setValue(this.data.from.vary ? this.data.from.vary : '')
    }
  }
  ngOnDestroy(): void {
    this.fis.reset(this.formId)
  }
  convertToPayload(): ICacheProfile {
    let formGroup = this.fis.formGroups[this.formId];
    let cacheControls = (formGroup.get('cacheControl').value as string[])
    let allowCache = formGroup.get('allowCache').value === 'yes';
    return {
      id: formGroup.get('id').value,//value is ignored
      name: formGroup.get('name').value,
      description: formGroup.get('description').value ? formGroup.get('description').value : null,
      allowCache: formGroup.get('allowCache').value === 'yes',
      cacheControl: cacheControls.length > 0 ? cacheControls : null,
      expires: formGroup.get('expires').value ? (+formGroup.get('expires').value) : null,
      maxAge: formGroup.get('maxAgeValue').value ? (+formGroup.get('maxAgeValue').value) : null,
      smaxAge: formGroup.get('smaxAgeValue').value ? (+formGroup.get('smaxAgeValue').value) : null,
      vary: formGroup.get('vary').value ? formGroup.get('vary').value : null,
      etag: allowCache ? formGroup.get('etagValidation').value : null,
      weakValidation: allowCache ? formGroup.get('etagType').value : null,
      version: this.data.from && this.data.from.version
    }
  }
  private validateForm() {
    const fg = this.fis.formGroups[this.formId];
    const var0 = Validator.exist(fg.get('name').value)
    this.fis.updateError(this.formId, 'name', var0.errorMsg)

    const var1 = Validator.exist(fg.get('allowCache').value)
    this.fis.updateError(this.formId, 'allowCache', var1.errorMsg)
    return !var0.errorMsg && !var1.errorMsg
  }

  update() {
    this.allowError = true
    if (this.validateForm()) {
      this.entityService.update(this.data.from.id, this.convertToPayload(), this.changeId)
    }
  }
  create() {
    this.allowError = true
    if (this.validateForm()) {
      this.entityService.create(this.convertToPayload(), this.changeId)
    }
  }
  dismiss(event: MouseEvent) {
    this.bottomSheetRef.dismiss();
    event.preventDefault();
  }
}
