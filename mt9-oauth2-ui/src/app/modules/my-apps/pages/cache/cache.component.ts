import { AfterViewInit, ChangeDetectorRef, Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { MatBottomSheetRef, MAT_BOTTOM_SHEET_DATA } from '@angular/material/bottom-sheet';
import { FormInfoService } from 'mt-form-builder';
import { filter, take } from 'rxjs/operators';
import { Aggregate } from 'src/app/clazz/abstract-aggregate';
import { IBottomSheet } from 'src/app/clazz/summary.component';
import { ICacheProfile } from 'src/app/clazz/validation/aggregate/cache/interfaze-cache';
import { CacheValidator } from 'src/app/clazz/validation/aggregate/cache/validator-cache';
import { RoleValidator } from 'src/app/clazz/validation/aggregate/role/validator-role';
import { ErrorMessage } from 'src/app/clazz/validation/validator-common';
import { FORM_CONFIG } from 'src/app/form-configs/cache.config';
import { CacheService } from 'src/app/services/cache.service';
@Component({
  selector: 'app-cache',
  templateUrl: './cache.component.html',
  styleUrls: ['./cache.component.css']
})
export class CacheComponent extends Aggregate<CacheComponent, ICacheProfile> implements OnInit, OnDestroy {
  bottomSheet: IBottomSheet<ICacheProfile>;
  constructor(
    public entityService: CacheService,
    fis: FormInfoService,
    @Inject(MAT_BOTTOM_SHEET_DATA) public data: any,
    bottomSheetRef: MatBottomSheetRef<CacheComponent>,
    cdr: ChangeDetectorRef
  ) {
    super('cache-form', JSON.parse(JSON.stringify(FORM_CONFIG)), new CacheValidator(), bottomSheetRef, data, fis, cdr)
    this.bottomSheet = data;
    this.fis.formCreated(this.formId).subscribe(() => {
      this.fis.formGroupCollection[this.formId].get('allowCache').valueChanges.subscribe(next => {
        if (next === 'yes') {
          this.fis.showIfMatch(this.formId, ['cacheControl', 'vary', 'expires', 'etagValidation']);
        } else {
          this.fis.hideIfNotMatch(this.formId, ['name', 'description', 'allowCache']);
          this.fis.formGroupCollection[this.formId].get('cacheControl').reset([])
          this.fis.formGroupCollection[this.formId].get('etagValidation').reset(false)
          this.fis.formGroupCollection[this.formId].get('etagType').reset(false)
          this.fis.formGroupCollection[this.formId].get('maxAgeValue').reset()
          this.fis.formGroupCollection[this.formId].get('smaxAgeValue').reset()
          this.fis.formGroupCollection[this.formId].get('vary').reset()
          this.fis.formGroupCollection[this.formId].get('expires').reset()
        }
      })
      this.fis.formGroupCollection[this.formId].get('cacheControl').valueChanges.subscribe(next => {
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
      this.fis.formGroupCollection[this.formId].get('etagValidation').valueChanges.subscribe(next => {
        if (next) {
          this.fis.showIfMatch(this.formId, ['etagType']);
        } else {
          this.fis.hideIfMatch(this.formId, ['etagType']);
        }
      })
      if (this.aggregate) {
        this.fis.formGroupCollection[this.formId].get('id').setValue(this.aggregate.id)
        this.fis.formGroupCollection[this.formId].get('name').setValue(this.aggregate.name)
        this.fis.formGroupCollection[this.formId].get('description').setValue(this.aggregate.description ? this.aggregate.description : '')
        this.fis.formGroupCollection[this.formId].get('allowCache').setValue(this.aggregate.allowCache ? 'yes' : 'no')
        this.fis.formGroupCollection[this.formId].get('cacheControl').setValue(this.aggregate.cacheControl)
        this.fis.formGroupCollection[this.formId].get('maxAgeValue').setValue(this.aggregate.maxAge)
        this.fis.formGroupCollection[this.formId].get('smaxAgeValue').setValue(this.aggregate.smaxAge)
        this.fis.formGroupCollection[this.formId].get('etagValidation').setValue(this.aggregate.etag)
        this.fis.formGroupCollection[this.formId].get('etagType').setValue(this.aggregate.weakValidation)
        this.fis.formGroupCollection[this.formId].get('expires').setValue(this.aggregate.expires?this.aggregate.expires:'')
        this.fis.formGroupCollection[this.formId].get('vary').setValue(this.aggregate.vary ? this.aggregate.vary : '')
        this.cdr.markForCheck()
      }
    })
  }
  ngOnDestroy(): void {
    this.cleanUp()
  }
  ngOnInit() {
  }
  convertToPayload(cmpt: CacheComponent): ICacheProfile {
    let formGroup = cmpt.fis.formGroupCollection[cmpt.formId];
    return {
      id: formGroup.get('id').value,//value is ignored
      name: formGroup.get('name').value,
      description: formGroup.get('description').value ? formGroup.get('description').value : null,
      allowCache: formGroup.get('allowCache').value === 'yes',
      cacheControl: formGroup.get('cacheControl').value,
      expires: formGroup.get('expires').value ? (+formGroup.get('expires').value) : null,
      maxAge: formGroup.get('maxAgeValue').value ? (+formGroup.get('maxAgeValue').value) : null,
      smaxAge: formGroup.get('smaxAgeValue').value ? (+formGroup.get('smaxAgeValue').value) : null,
      vary: formGroup.get('vary').value ? formGroup.get('vary').value : null,
      etag: formGroup.get('etagValidation').value,
      weakValidation: formGroup.get('etagType').value,
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
  errorMapper(original: ErrorMessage[], cmpt: CacheComponent) {
    return original.map(e => {
      return {
        ...e,
        formId: cmpt.formId
      }
    })
  }
}
