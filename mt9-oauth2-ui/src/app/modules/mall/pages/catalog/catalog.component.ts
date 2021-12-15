import { ChangeDetectorRef, Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { MatBottomSheetRef, MAT_BOTTOM_SHEET_DATA } from '@angular/material/bottom-sheet';
import { FormInfoService } from 'mt-form-builder';
import { IForm, IOption } from 'mt-form-builder/lib/classes/template.interface';
import { combineLatest, Observable } from 'rxjs';
import { filter, switchMap, take } from 'rxjs/operators';
import { Aggregate } from 'src/app/clazz/abstract-aggregate';
import { CATALOG_TYPE } from 'src/app/clazz/constants';
import { ISumRep } from 'src/app/clazz/summary.component';
import { getLayeredLabel, parseAttributePayload } from 'src/app/clazz/utility';
import { IBizAttribute } from 'src/app/clazz/validation/aggregate/attribute/interfaze-attribute';
import { ICatalog } from 'src/app/clazz/validation/aggregate/catalog/interfaze-catalog';
import { CatalogValidator } from 'src/app/clazz/validation/aggregate/catalog/validator-catalog';
import { ErrorMessage, hasValue } from 'src/app/clazz/validation/validator-common';
import { CATALOG_ATTR_FORM_CONFIG, FORM_CONFIG } from 'src/app/form-configs/catalog.config';
import { AttributeService } from 'src/app/services/attribute.service';
import { CatalogService } from 'src/app/services/catalog.service';

@Component({
  selector: 'app-catalog',
  templateUrl: './catalog.component.html',
  styleUrls: ['./catalog.component.css']
})
export class CatalogComponent extends Aggregate<CatalogComponent, ICatalog> implements OnInit, OnDestroy {
  attrFormId = 'attributes';
  attrFormInfo: IForm = JSON.parse(JSON.stringify(CATALOG_ATTR_FORM_CONFIG));
  private formCreatedOb: Observable<string>;
  private attrFormCreatedOb: Observable<string>;
  constructor(
    public entitySvc: CatalogService,
    public attrSvc: AttributeService,
    fis: FormInfoService,
    cdr: ChangeDetectorRef,
    @Inject(MAT_BOTTOM_SHEET_DATA) public data: any,
    bottomSheetRef: MatBottomSheetRef<CatalogComponent>
  ) {
    super('category', JSON.parse(JSON.stringify(FORM_CONFIG)), new CatalogValidator(), bottomSheetRef, data, fis, cdr);
    this.formCreatedOb = this.fis.formCreated(this.formId);
    this.attrFormCreatedOb = this.fis.formCreated(this.attrFormId);
    this.fis.queryProvider[this.attrFormId + '_' + 'attributeId'] = attrSvc;
    this.fis.queryProvider[this.formId + '_' + 'parentId'] = entitySvc;
    let sub1 = combineLatest([this.formCreatedOb, this.attrFormCreatedOb]).pipe(take(1)).subscribe(() => {
      this.subForCatalogTypeChange(true);
      this.subForAttrFormChange();
      if (this.aggregate ) {
        if (this.aggregate && this.aggregate.attributes) {
          if (hasValue(this.aggregate.parentId)) {
            combineLatest([this.entitySvc.readEntityByQuery(0, 1, 'id:' + this.aggregate.parentId), this.attrSvc.readEntityByQuery(0, this.aggregate.attributes.length, 'id:' + this.aggregate.attributes.map(e => e.split(':')[0]).join('.'))]).pipe(take(1))
              .subscribe(next => {
                this.formInfo.inputs.find(e => e.key === 'parentId').options = next[0].data.map(e => <IOption>{ label: e.name, value: e.id })
                this.resumeForm(next[1]);
                this.cdr.markForCheck()
              })
          } else {
            combineLatest([this.attrSvc.readEntityByQuery(0, this.aggregate.attributes.length, 'id:' + this.aggregate.attributes.map(e => e.split(':')[0]).join('.'))]).pipe(take(1))
            .subscribe(next => {
              this.resumeForm(next[0]);
              this.cdr.markForCheck()
            })
          }
        }
      }
      this.cdr.markForCheck();
    })
    this.subs['combineLatest'] = sub1;
  }
  private resumeForm(next: ISumRep<IBizAttribute>) {
    this.fis.restore(this.formId, this.aggregate, true);
    let var0 = next.data.map(e => <IOption>{ label: e.name, value: e.id });
    this.attrFormInfo.inputs.forEach(e => {
      e.options = var0;
      e.optionOriginal = next.data;
    });
    this.fis.formGroupCollection_template[this.attrFormId] = JSON.parse(JSON.stringify(this.attrFormInfo));
    this.fis.restoreDynamicForm(this.attrFormId, parseAttributePayload(this.aggregate.attributes, next.data), this.aggregate.attributes.length);
  }

  private subForCatalogTypeChange(skipReset: boolean) {
    let sub3 = this.fis.formGroupCollection[this.formId].get('catalogType').valueChanges.subscribe(next => {
      this.formInfo.inputs.find(e => e.key === 'parentId').display = true;
      if (next === 'FRONTEND') {
        this.entitySvc.queryPrefix = CATALOG_TYPE.FRONTEND;
      } else {
        this.entitySvc.queryPrefix = CATALOG_TYPE.BACKEND;
      }
      if (!skipReset) {
        this.fis.formGroupCollection[this.formId].get('parentId').reset();
      }
      this.cdr.markForCheck();
    });
    this.subs['catalogTypeChange'] = sub3;
  }
  private subForAttrFormChange() {
    let sub2 = this.fis.formGroupCollection[this.attrFormId].valueChanges.subscribe(next => {
      Object.keys(next).filter(e => e.includes('attributeId')).forEach(idKey => {
        let selected = (this.attrFormInfo.inputs.find(e => e.key === idKey).optionOriginal)?.find(e => e.id === next[idKey]) as IBizAttribute
        if (selected) {
          let append = idKey.replace('attributeId', '');
          this.attrFormInfo.inputs.find(ee => ee.key === 'attributeValueSelect' + append).display = selected.method === 'SELECT';
          this.attrFormInfo.inputs.find(ee => ee.key === 'attributeValueManual' + append).display = selected.method !== 'SELECT';
          if (selected.method === 'SELECT') {
            this.attrFormInfo.inputs.find(ee => ee.key === 'attributeValueSelect' + append).options = selected.selectValues.map(e => <IOption>{ label: e, value: e })
          }
        }
      })
    });
    this.subs['attrFormChange'] = sub2;
  }
  ngOnDestroy(): void {
    Object.keys(this.subs).forEach(k => { this.subs[k].unsubscribe() })
    this.fis.resetAllExcept(['summaryCatalogCustomerView'])
  }
  ngOnInit() {
  }
  convertToPayload(cmpt: CatalogComponent): ICatalog {
    let formGroup = cmpt.fis.formGroupCollection[cmpt.formId];
    return {
      id: formGroup.get('id').value,
      name: formGroup.get('name').value,
      parentId: formGroup.get('parentId').value,
      attributes: cmpt.hasAttr() ? cmpt.getAttributeAsPayload() : [],
      catalogType: formGroup.get('catalogType').value ? formGroup.get('catalogType').value : '',
      version: cmpt.aggregate && cmpt.aggregate.version
    }
  }
  create() {
    if (this.validateHelper.validate(this.validator, this.convertToPayload, 'adminCreateCatalogCommandValidator', this.fis, this, this.errorMapper))
      this.entitySvc.create(this.convertToPayload(this), this.changeId)
  }
  update() {
    if (this.validateHelper.validate(this.validator, this.convertToPayload, 'adminUpdateCatalogCommandValidator', this.fis, this, this.errorMapper))
      this.entitySvc.update(this.aggregate.id, this.convertToPayload(this), this.changeId)
  }

  errorMapper(original: ErrorMessage[], cmpt: CatalogComponent) {
    return original.map(e => {
      if (e.key === 'attributes') {
        return {
          ...e,
          key: 'attributeId',
          formId: cmpt.attrFormId
        }
      } else {
        return {
          ...e,
          formId: cmpt.formId
        }
      }
    })
  }
  private hasAttr(): boolean {
    let attrFormValue = this.fis.formGroupCollection[this.attrFormId].value;
    return Object.keys(attrFormValue).filter(e => e.includes('attributeId')).filter(idKey => attrFormValue[idKey]).length > 0;
  }
  private getAttributeAsPayload(): string[] {
    let attrFormValue = this.fis.formGroupCollection[this.attrFormId].value;
    return Object.keys(attrFormValue).filter(e => e.includes('attributeId')).map(idKey => {
      let selected = this.attrFormInfo.inputs.find(e => e.key === idKey).optionOriginal.find(e => e.id === attrFormValue[idKey]) as IBizAttribute
      let append = idKey.replace('attributeId', '');
      let attrValue: string;
      if (selected) {
        if (selected.method === 'SELECT') {
          attrValue = this.fis.formGroupCollection[this.attrFormId].get('attributeValueSelect' + append).value;
        } else {
          attrValue = this.fis.formGroupCollection[this.attrFormId].get('attributeValueManual' + append).value;
        }
        return selected.id + ':' + attrValue
      }
    });
  }
}
