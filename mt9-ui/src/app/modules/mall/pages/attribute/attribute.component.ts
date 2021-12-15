import { ChangeDetectorRef, Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { MatBottomSheetRef, MAT_BOTTOM_SHEET_DATA } from '@angular/material/bottom-sheet';
import { FormInfoService } from 'mt-form-builder';
import { IAddDynamicFormEvent, IForm } from 'mt-form-builder/lib/classes/template.interface';
import { combineLatest, Observable, Subject } from 'rxjs';
import { filter, take } from 'rxjs/operators';
import { Aggregate } from 'src/app/clazz/abstract-aggregate';
import { IBottomSheet } from 'src/app/clazz/summary.component';
import { IBizAttribute } from 'src/app/clazz/validation/aggregate/attribute/interfaze-attribute';
import { AttributeValidator } from 'src/app/clazz/validation/aggregate/attribute/validator-attribute';
import { ErrorMessage } from 'src/app/clazz/validation/validator-common';
import { FORM_CONFIG, FORM_CONFIG_ATTR_VALUE } from 'src/app/form-configs/attribute.config';
import { AttributeService } from 'src/app/services/attribute.service';
interface ISetValueEvent {
  type: 'setvalue'
  id: number,
  formId: string,
  key: string,
  value: string
}
@Component({
  selector: 'app-attribute',
  templateUrl: './attribute.component.html',
  styleUrls: ['./attribute.component.css']
})
export class AttributeComponent extends Aggregate<AttributeComponent, IBizAttribute> implements OnInit, OnDestroy {
  manualSelect = false;
  formIdAttrValue = 'attributesValue';
  formInfoAttrValue: IForm = JSON.parse(JSON.stringify(FORM_CONFIG_ATTR_VALUE));
  private formCreatedOb: Observable<string>;
  private attrFormCreatedOb: Observable<string>;
  constructor(
    public attributeSvc: AttributeService,
    fis: FormInfoService,
    @Inject(MAT_BOTTOM_SHEET_DATA) public data: any,
    bottomSheetRef: MatBottomSheetRef<AttributeComponent>,
    cdr:ChangeDetectorRef
  ) {
    super('attributes', JSON.parse(JSON.stringify(FORM_CONFIG)), new AttributeValidator(), bottomSheetRef, data,fis,cdr);
    this.formCreatedOb = this.fis.formCreated(this.formId);
    this.attrFormCreatedOb = this.fis.formCreated(this.formIdAttrValue);
    combineLatest([this.formCreatedOb]).pipe(take(1)).subscribe(() => {
      this.fis.formGroupCollection[this.formId].get('method').valueChanges.subscribe(next => {
        this.manualSelect = next === 'SELECT';
      });
      if (this.aggregate) {
        this.fis.restore(this.formId, this.aggregate, true);
        this.cdr.markForCheck()
        combineLatest([this.attrFormCreatedOb]).pipe(take(1)).subscribe(() => {
          if (this.aggregate.selectValues && this.aggregate.selectValues.length !== 0) {
            this.fis.restoreDynamicForm(this.formIdAttrValue, this.fis.parsePayloadArr(this.aggregate.selectValues, 'attrValue'), this.aggregate.selectValues.length)
          }
        })
      }
    })
  }

  ngOnDestroy(): void {
    this.cleanUp()
  }
  ngOnInit() {
  }
  convertToPayload(cmpt: AttributeComponent) {
    let formGroup = cmpt.fis.formGroupCollection[cmpt.formId];
    let values = null;
    if (formGroup.get('method').value === 'SELECT' && cmpt.fis.formGroupCollection[cmpt.formIdAttrValue]) {
      let valueSnapshot = cmpt.fis.formGroupCollection[cmpt.formIdAttrValue].value;
      values = Object.keys(valueSnapshot).map(e => valueSnapshot[e] as string);
    }
    return <IBizAttribute>{
      id: formGroup.get('id').value,
      name: formGroup.get('name').value,
      description: formGroup.get('description').value ? formGroup.get('description').value : null,
      method: formGroup.get('method').value,
      selectValues: values,
      type: formGroup.get('type').value,
      version:cmpt.aggregate&&cmpt.aggregate.version
    }
  }
  create() {
    if (this.validateHelper.validate(this.validator, this.convertToPayload, 'adminCreateAttributeCommandValidator', this.fis, this, this.errorMapper))
      this.attributeSvc.create(this.convertToPayload(this), this.changeId)
  }
  update() {
    if (this.validateHelper.validate(this.validator, this.convertToPayload, 'adminUpdateAttributeCommandValidator', this.fis, this, this.errorMapper))
      this.attributeSvc.update(this.aggregate.id, this.convertToPayload(this), this.changeId)
  }

  errorMapper(original: ErrorMessage[], cmpt: AttributeComponent) {
    return original.map(e => {
      if (e.key === 'attributes') {
        return {
          ...e,
          key: 'attributeId',
          formId: cmpt.formId
        }
      } else if (e.key.includes("_valueOption")) {
        let idx = +e.key.split('_')[0];
        return {
          ...e,
          key: cmpt.fis.formGroupCollection_formInfo[cmpt.formIdAttrValue].inputs.find((e, index) => index === idx).key,
          formId: cmpt.formIdAttrValue
        }
      } else {
        return {
          ...e,
          formId: cmpt.formId
        }
      }
    })
  }
}
