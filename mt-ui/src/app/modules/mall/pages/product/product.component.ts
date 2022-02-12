import { ChangeDetectorRef, Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { MatBottomSheetRef, MAT_BOTTOM_SHEET_DATA } from '@angular/material/bottom-sheet';
import { FormInfoService } from 'mt-form-builder';
import { IAddDynamicFormEvent, IForm, IOption, ISetValueEvent } from 'mt-form-builder/lib/classes/template.interface';
import { combineLatest, Observable, Subject } from 'rxjs';
import { filter, switchMap, take } from 'rxjs/operators';
import { Aggregate } from 'src/app/clazz/abstract-aggregate';
import { CATALOG_TYPE } from 'src/app/clazz/constants';
import { IBottomSheet, ISumRep } from 'src/app/clazz/summary.component';
import { getLabel, getLayeredLabel, parseAttributePayload } from 'src/app/clazz/utility';
import { IBizAttribute } from 'src/app/clazz/validation/aggregate/attribute/interfaze-attribute';
import { ICatalog } from 'src/app/clazz/validation/aggregate/catalog/interfaze-catalog';
import { IAttrImage, IProductDetail, IProductOption, IProductOptions, ISku } from 'src/app/clazz/validation/aggregate/product/interfaze-product';
import { ProductValidator } from 'src/app/clazz/validation/aggregate/product/validator-product';
import { ErrorMessage, hasValue } from 'src/app/clazz/validation/validator-common';
import { ATTR_GEN_FORM_CONFIG } from 'src/app/form-configs/attribute-general-dynamic.config';
import { ATTR_PROD_FORM_CONFIG } from 'src/app/form-configs/attribute-product-dynamic.config';
import { ATTR_SALES_FORM_CONFIG } from 'src/app/form-configs/attribute-sales-dynamic.config';
import { ATTR_SALE_FORM_CONFIG_IMAGE, FORM_CONFIG, FORM_CONFIG_IMAGE, FORM_CONFIG_OPTIONS } from 'src/app/form-configs/product.config';
import { AttributeService } from 'src/app/services/attribute.service';
import { CatalogService } from 'src/app/services/catalog.service';
import { HttpProxyService } from 'src/app/services/http-proxy.service';
import { ProductService } from 'src/app/services/product.service';
import { environment } from 'src/environments/environment';
interface IProductSimplePublic {
  imageUrlSmall: string;
  name: string;
  description: string;
  lowestPrice: number;
  totalSales: number;
  id: number;
}
interface IProductSkuPublic {
  attributesSales: string[];
  price: number;
  storage: number;
}
interface IProductDetailPublic extends IProductSimplePublic {
  imageUrlLarge?: string[];
  selectedOptions?: IProductOptions[];
  specification?: string[];
  skus: IProductSkuPublic[],
  storage?: number,
  attrIdMap: { [key: number]: string }
  attributeSaleImages?: IAttrImage[]
}
/**
 * @description this experimental class has resume fn remove, instead, it's completely rely on event replay
 * 
 */
@Component({
  selector: 'app-product-exp',
  templateUrl: './product.component.html',
  styleUrls: ['./product.component.css']
})
export class ProductComponent extends Aggregate<ProductComponent, IProductDetail> implements OnInit, OnDestroy {
  productBottomSheet: IBottomSheet<IProductDetail>;
  salesFormIdTempId = 'attrSalesFormChild';
  attrProdFormId = 'attributesProd';
  attrProdFormInfo: IForm = JSON.parse(JSON.stringify(ATTR_PROD_FORM_CONFIG));
  attrSalesFormId = 'attributeSales';
  attrSalesFormInfo: IForm = JSON.parse(JSON.stringify(ATTR_SALES_FORM_CONFIG));
  attrGeneralFormId = 'attributesGeneral';
  attrGeneralFormInfo: IForm = JSON.parse(JSON.stringify(ATTR_GEN_FORM_CONFIG));
  imageAttrSaleFormId = 'productAttrSaleImage';
  imageAttrSaleChildFormId = 'imageChildForm';
  imageAttrSaleFormInfo: IForm = JSON.parse(JSON.stringify(ATTR_SALE_FORM_CONFIG_IMAGE));
  imageFormId = 'product_image';
  imageFormInfo: IForm = JSON.parse(JSON.stringify(FORM_CONFIG_IMAGE));
  optionFormId = 'product_option';
  optionFormInfo: IForm = JSON.parse(JSON.stringify(FORM_CONFIG_OPTIONS));
  public attrList: IBizAttribute[];
  public catalogs: ISumRep<ICatalog>;
  private udpateSkusOriginalCopy: ISku[];
  private formCreatedOb: Observable<string>;
  private prodFormCreatedOb: Observable<string>;
  private salesFormCreatedOb: Observable<string>;
  private genFormCreatedOb: Observable<string>;
  private salesFormIdTempFormCreatedOb: Observable<string>;
  private imgAttrSaleFormCreatedOb: Observable<string>;
  private imageAttrSaleChildFormCreatedOb: Observable<string>;
  public hasSku: boolean = false;
  public previewFlag: boolean = false;
  private keys = ['storageActual', 'storageOrder', 'price', 'sales']
  private keys2 = ['storage_OrderIncreaseBy', 'storage_OrderDecreaseBy', 'storage_ActualIncreaseBy', 'storage_ActualDecreaseBy']
  constructor(
    public productSvc: ProductService,
    private httpProxy: HttpProxyService,
    fis: FormInfoService,
    private categorySvc: CatalogService,
    public attrSvc: AttributeService,
    @Inject(MAT_BOTTOM_SHEET_DATA) public data: any, // keep as any is needed
    bottomSheetRef: MatBottomSheetRef<ProductComponent>,
    cdr: ChangeDetectorRef
  ) {
    super('product', JSON.parse(JSON.stringify(FORM_CONFIG)), new ProductValidator('product'), bottomSheetRef, data, fis, cdr);
    this.productBottomSheet = data;
    this.formCreatedOb = this.fis.formCreated(this.formId);
    this.prodFormCreatedOb = this.fis.formCreated(this.attrProdFormId);
    this.salesFormCreatedOb = this.fis.formCreated(this.attrSalesFormId);
    this.genFormCreatedOb = this.fis.formCreated(this.attrGeneralFormId);
    this.imgAttrSaleFormCreatedOb = this.fis.formCreated(this.imageAttrSaleFormId);
    this.salesFormIdTempFormCreatedOb = this.fis.formCreated(this.salesFormIdTempId);
    this.imageAttrSaleChildFormCreatedOb = this.fis.formCreated(this.imageAttrSaleChildFormId);
    combineLatest([this.categorySvc.readEntityByQuery(0, 1000, CATALOG_TYPE.BACKEND), this.formCreatedOb]).pipe(take(1)).subscribe(next => {
      if (next[0].data) {
        this.catalogs = next[0];
        this.formInfo.inputs[1].options = next[0].data.filter(ee => this.isLeafNode(next[0].data, ee)).map(e => <IOption>{ label: getLayeredLabel(e, next[0].data), value: String(e.id) });
        this.cdr.markForCheck()
      }
      if (this.productBottomSheet.context !== 'new') {
        /** @note start of can be removed if product created manually*/
        this.fis.restore(this.formId, this.aggregate);
        this.fis.formGroupCollection[this.formId].get('startAtDate').setValue(this.aggregate.startAt ? new Date(this.aggregate.startAt) : '', { emitEvent: false })
        this.fis.formGroupCollection[this.formId].get('startAtTime').setValue(this.aggregate.startAt ? this._getTime(new Date(this.aggregate.startAt)) : '', { emitEvent: false })
        this.fis.formGroupCollection[this.formId].get('endAtDate').setValue(this.aggregate.endAt ? new Date(this.aggregate.endAt) : '', { emitEvent: false })
        this.fis.formGroupCollection[this.formId].get('endAtTime').setValue(this.aggregate.endAt ? this._getTime(new Date(this.aggregate.endAt)) : '', { emitEvent: false })
        /** @note end of can be removed if product created manually*/
        this.formInfo.inputs.find(e => e.key === 'status').display = false;
        this.formInfo.inputs.find(e => e.key === 'startAtDate').display = true;
        this.formInfo.inputs.find(e => e.key === 'startAtTime').display = true;
      } else {
        let sub4 = this.fis.formGroupCollection[this.formId].get('status').valueChanges.subscribe(next => {
          if (next === 'AVAILABLE') {
            this.fis.formGroupCollection[this.formId].get('startAtDate').setValue(new Date(), { emitEvent: false })
            this.fis.formGroupCollection[this.formId].get('startAtTime').setValue('00:00:00', { emitEvent: false })
            this.formInfo.inputs.find(e => e.key === 'startAtDate').display = false;
            this.formInfo.inputs.find(e => e.key === 'startAtTime').display = false;
          } else {
            this.fis.formGroupCollection[this.formId].get('startAtDate').setValue(null, { emitEvent: false })
            this.fis.formGroupCollection[this.formId].get('startAtTime').setValue('', { emitEvent: false })
            this.formInfo.inputs.find(e => e.key === 'startAtDate').display = true;
            this.formInfo.inputs.find(e => e.key === 'startAtTime').display = true;
          }
        });
        this.subs[this.formId + '_status'] = sub4;
      }
      let sub = this.fis.formGroupCollection[this.formId].get('selectBackendCatalog').valueChanges.subscribe(next => {
        this._loadAttributes(this.catalogs.data.find(e => e.id === next))
      })
      this.subs[this.formId + '_selectBackendCatalog'] = sub;
      let sub2 = this.fis.formGroupCollection[this.formId].get('hasSku').valueChanges.subscribe(next => {
        if (next === 'YES') {
          this.hasSku = true;
          this.formInfo.inputs.filter(e => this.keys.includes(e.key)).forEach(e => e.display = false);
          this.formInfo.inputs.filter(e => this.keys2.includes(e.key)).forEach(e => e.display = false);
        } else {
          this.hasSku = false;
          this.formInfo.inputs.filter(e => this.keys.includes(e.key)).forEach(e => e.display = true);
        }
      })
      this.subs[this.formId + '_hasSku'] = sub2;
    })
    let sub1 = this.attrSvc.readEntityByQuery(0, 1000).pipe(switchMap((next) => {//@todo use paginated select component
      // load attribute first then initialize form
      this._updateFormInfoOptions(next.data);
      this.attrList = next.data;
      this.cdr.markForCheck() // this is required to initialize all forms
      return combineLatest([this.prodFormCreatedOb, this.genFormCreatedOb])
    })).subscribe(() => {
      if (this.productBottomSheet.context === 'new') {
        let sub = this.salesFormIdTempFormCreatedOb.subscribe(() => {
          this._subChangeForForm(this.salesFormIdTempId);
        })
        this.subs[this.formId + '_salesFormIdTempFormCreatedOb'] = sub;
      } else {
        /** @note start of can be removed if product created manually*/
        if (this.aggregate.skus.filter(e => this._hasEmptyAttrSales(e)).length === 0) {
          // use sku form
          this.udpateSkusOriginalCopy = JSON.parse(JSON.stringify(this.aggregate.skus))
          this.formInfo.inputs.filter(e => this.keys.includes(e.key)).forEach(e => e.display = false);
          this.fis.formGroupCollection[this.formId].get('hasSku').setValue('YES', { emitEvent: false });
          this.hasSku = true;
          this.salesFormCreatedOb.subscribe(() => {
            this._updateAndSubSalesForm(this.aggregate.skus);
          });
        } else {
          // use no sku form
          this.formInfo.inputs.filter(e => this.keys.includes(e.key)).forEach(e => e.display = true);
          this.fis.formGroupCollection[this.formId].get('hasSku').setValue('NO', { emitEvent: false });
          this.hasSku = false;
          this.fis.restore(this.formId, this.aggregate.skus[0]);
          this._disabledAttrSalesForm(this.fis.formGroupCollection_formInfo[this.formId]);
          this._displayStorageChangeInputs(this.fis.formGroupCollection_formInfo[this.formId]);
        }
        if (this.aggregate.attributesProd) {
          this._subChangeForForm(this.attrProdFormId);
          this.updateValueForForm(this.aggregate.attributesProd, this.attrProdFormId);
        }
        if (this.aggregate.attributesGen) {
          this._subChangeForForm(this.attrGeneralFormId);
          this.updateValueForForm(this.aggregate.attributesGen, this.attrGeneralFormId);
        }
        if (this.aggregate.imageUrlLarge && this.aggregate.imageUrlLarge.length !== 0) {
          this.fis.restoreDynamicForm(this.imageFormId, this.fis.parsePayloadArr(this.aggregate.imageUrlLarge, 'imageUrl'), this.aggregate.imageUrlLarge.length)
        }
        if (this.aggregate.selectedOptions && this.aggregate.selectedOptions.length !== 0) {
          this.fis.restoreDynamicForm(this.optionFormId, this.fis.parsePayloadArr(this.aggregate.selectedOptions.map(e => e.title), 'productOption'), this.aggregate.selectedOptions.length);
          this.aggregate.selectedOptions.forEach((option, index) => {
            if (index === 0) {
              //for child form
              let childFormId = 'optionForm'
              this._updateChildFormProductOption(option, childFormId);
              //for child form
            } else {
              let childFormId = 'optionForm_' + (index - 1);
              let childFormCreated = this.fis.formCreated(childFormId);
              childFormCreated.subscribe(() => {
                this._updateChildFormProductOption(option, childFormId);
              })
            }
          });
        }
        if (this.aggregate.attributeSaleImages && this.aggregate.attributeSaleImages.length !== 0) {
          this.imgAttrSaleFormCreatedOb.subscribe(() => {
            let attrs = this.aggregate.attributeSaleImages.map(e => e.attributeSales);
            this._subChangeForForm(this.imageAttrSaleFormId);
            this.updateValueForForm(attrs, this.imageAttrSaleFormId);
            this.imageAttrSaleChildFormCreatedOb.subscribe(() => {
              this.aggregate.attributeSaleImages.forEach((e, index) => {
                if (index === 0) {
                  this.fis.restoreDynamicForm(this.imageAttrSaleChildFormId, this.fis.parsePayloadArr(e.imageUrls, 'imageUrl'), e.imageUrls.length)
                  this.cdr.markForCheck()
                } else {
                  let formId = this.imageAttrSaleChildFormId + '_' + (index - 1);
                  let childFormCreated = this.fis.formCreated(formId);
                  childFormCreated.subscribe(() => {
                    this.fis.restoreDynamicForm(formId, this.fis.parsePayloadArr(e.imageUrls, 'imageUrl'), e.imageUrls.length)
                    this.cdr.markForCheck()
                  });
                }
              })
            })
          })
        }
        /** @note end of can be removed if product created manually*/
      }
      this.salesFormCreatedOb.subscribe(() => {
        this._subChangeForForm(this.imageAttrSaleFormId);
        // when add new child form sub for value chage if no sub
        let sub2 = this.fis.formGroupCollection[this.attrSalesFormId].valueChanges.subscribe(next => {
          Object.keys(next).filter(e => e.includes(this.salesFormIdTempId)).forEach(childrenFormId => {
            if (!this.subs[childrenFormId + '_valueChange']) {
              let childFormCreated = this.fis.formCreated(childrenFormId);
              childFormCreated.subscribe(() => {
                this._subChangeForForm(childrenFormId);
              })
            }
          })
        });
        this.subs[this.attrSalesFormId + '_valueChange'] = sub2;
      })
      let sub3 = this.fis.$uploadFile.subscribe(next => {
        this._uploadFile(next.files, next.formId, next.key);
        if (next.formId === this.formId && next.key === 'imageUrlSmall') {
          this.validateHelper.validate(this.validator, this.convertToPayload, 'CREATE', this.fis, this, this.errorMapper)
        }
      })
      this.subs['fileupload'] = sub3;
      this._subChangeForForm(this.attrProdFormId);
      this._subChangeForForm(this.attrGeneralFormId);
    })
    this.subs['getAttributeList_http'] = sub1;
  }
  private _getTime(arg0: Date): string {
    let hour = arg0.getUTCHours() + '';
    if (arg0.getUTCHours() < 10)
      hour = '0' + arg0.getUTCHours()
    let minutes = arg0.getUTCMinutes() + '';
    if (arg0.getUTCMinutes() < 10)
      minutes = '0' + arg0.getUTCMinutes()
    let sec = arg0.getUTCSeconds() + '';
    if (arg0.getUTCSeconds() < 10)
      sec = '0' + arg0.getUTCSeconds()
    return hour + ':' + minutes + ':' + sec
  }
  private _updateChildFormProductOption(option: IProductOptions, childFormId: string) {
    let value = this.fis.parsePayloadArr(option.options.map(e => e.optionValue), 'optionValue');
    let value2 = this.fis.parsePayloadArr(option.options.map(e => e.priceVar), 'optionPriceChange');
    Object.assign(value, value2)
    this.fis.restoreDynamicForm(childFormId, value, option.options.length);
  }
  private _updateAndSubSalesForm(skus: ISku[]) {
    let value = this.fis.parsePayloadArr(skus.map(e => e.storageOrder), 'storageOrder');
    let value2 = this.fis.parsePayloadArr(skus.map(e => e.storageActual), 'storageActual');
    let value3 = this.fis.parsePayloadArr(skus.map(e => e.price), 'price');
    let value4 = this.fis.parsePayloadArr(skus.map(e => e.sales), 'sales');
    Object.assign(value, value2)
    Object.assign(value, value3)
    Object.assign(value, value4)
    this.fis.restoreDynamicForm(this.attrSalesFormId, value, skus.length);
    skus.forEach((sku, index) => {
      if (index === 0) {
        //start of child form
        let formInfo = this.attrSalesFormInfo.inputs.find(e => e.form !== null && e.form !== undefined).form;
        this.salesFormIdTempFormCreatedOb.subscribe(() => {
          this._subChangeForForm(this.salesFormIdTempId);
          this.updateValueForForm(sku.attributesSales, this.salesFormIdTempId);
        })
        if (this.productBottomSheet.context !== 'clone') {
          this._disabledAttrSalesChildForm(formInfo);
        }
        //end of child form
      } else {
        //start of child form
        let formId = this.salesFormIdTempId + '_' + (index - 1);

        let childFormCreated = this.fis.formCreated(formId);
        childFormCreated.subscribe(() => {
          let formInfo = this.fis.formGroupCollection_formInfo[formId];
          this._subChangeForForm(formId);
          this.updateValueForForm(sku.attributesSales, formId);
          if (this.productBottomSheet.context !== 'clone') {
            this._disabledAttrSalesChildForm(formInfo);
          }
        });
        //end of child form
      }
    });
    if (this.productBottomSheet.context !== 'clone') {
      this._displayStorageChangeInputs(this.fis.formGroupCollection_formInfo[this.attrSalesFormId]);
      this._disabledAttrSalesForm(this.fis.formGroupCollection_formInfo[this.attrSalesFormId]);
    }
  }
  private _disabledAttrSalesForm(formInfo: IForm) {
    let var0 = ['storageOrder', 'storageActual', 'sales']
    formInfo.inputs.filter(e => var0.filter(ee => e.key.includes(ee)).length > 0).forEach(e => e.disabled = true);
  }
  private _disabledAttrSalesChildForm(formInfo: IForm) {
    formInfo.inputs.forEach(e => e.disabled = true);
    formInfo.disabled = true;
  }
  private updateValueForForm(attrs: string[], formId: string) {
    this.fis.restoreDynamicForm(formId, parseAttributePayload(attrs, this.attrList), attrs.length);
  }
  doPreview() {
    this.previewFlag = !this.previewFlag;
  }
  private _displayStorageChangeInputs(arg0: IForm) {
    let var0 = ['storage_OrderIncreaseBy', 'storage_OrderDecreaseBy', 'storage_ActualIncreaseBy', 'storage_ActualDecreaseBy']
    arg0.inputs.filter(e => var0.filter(ee => e.key.includes(ee)).length > 0).forEach(e => e.display = true);
  }
  private _disabledStorageSalesForm(formInfo: IForm) {
    let var0 = ['storageOrder', 'storageActual', 'sales']
    formInfo.inputs.filter(e => var0.filter(ee => e.key.includes(ee)).length > 0).forEach(e => e.disabled = true);
  }
  private _hasEmptyAttrSales(e: ISku): boolean {
    return e.attributesSales.length === 0 || (e.attributesSales.length === 1 && e.attributesSales[0] === '')
  }
  /**
   * @description update formInfo first then initialize form, so add template can be correct
   * @param attrs 
   */
  private _updateFormInfoOptions(attrs: IBizAttribute[]) {
    this.attrProdFormInfo.inputs[0].options = attrs.filter(e => e.type === 'PROD_ATTR').map(e => <IOption>{ label: getLabel(e), value: e.id });
    this.attrGeneralFormInfo.inputs[0].options = attrs.filter(e => e.type === 'GEN_ATTR').map(e => <IOption>{ label: getLabel(e), value: e.id });
    this.attrSalesFormInfo.inputs.find(e => e.form !== null && e.form !== undefined).form.inputs[0].options = attrs.filter(e => e.type === 'SALES_ATTR').map(e => <IOption>{ label: getLabel(e), value: e.id });
    this.imageAttrSaleFormInfo.inputs[0].options = attrs.filter(e => e.type === 'SALES_ATTR').map(e => <IOption>{ label: getLabel(e), value: e.id });
  }
  ngOnDestroy(): void {
    this.cleanUp()
  }
  ngOnInit() {

  }
  private isLeafNode(catalogs: ICatalog[], catalog: ICatalog): boolean {
    return catalogs.filter(node => node.parentId === catalog.id).length == 0
  }
  private _parseDate(value: Date, time: string): number {
    let split: string[] = time.split(':');
    let hoursInSec = (+split[0]) * 60 * 60
    let minuteInSec = (+split[1]) * 60
    let totalSec = hoursInSec + minuteInSec + (+split[2])
    return value.getTime() + totalSec * 1000

  }
  private _uploadFile(files: FileList, formId: string, ctrlName: string) {
    this.httpProxy.uploadFile(files.item(0)).subscribe(next => {
      if (next.includes('http')) {
        this.fis.formGroupCollection[formId].get(ctrlName).setValue(next)
      } else {
        this.fis.formGroupCollection[formId].get(ctrlName).setValue(environment.serverUri + '/file-upload-svc/files/public/' + next)
      }
      this.cdr.detectChanges();
    })
  }
  public _loadAttributes(attr: ICatalog) {
    let tags: string[] = [];
    tags.push(...attr.attributes);
    while (hasValue(attr.parentId)) {
      let nextId = attr.parentId;
      attr = this.catalogs.data.find(e => e.id === nextId);
      tags.push(...attr.attributes);
    }
    this.fis.formGroupCollection[this.formId].get('attributesKey').setValue(tags, { emitEvent: false });
  }
  private hasAttr(formId: string): boolean {
    let attrFormValue = this.fis.formGroupCollection[formId].value;
    return Object.keys(attrFormValue).filter(e => e.includes('attributeId')).filter(idKey => attrFormValue[idKey]).length > 0;
  }
  private getAddedAttrs(formId: string): string[] {
    if (!this.fis.formGroupCollection[formId]) {
      return []
    }
    let attrFormValue = this.fis.formGroupCollection[formId].value;
    return Object.keys(attrFormValue).filter(e => e.includes('attributeId')).map(idKey => {
      let selected = this.attrList.find(e => e.id === attrFormValue[idKey]);
      if (selected) {
        let append = idKey.replace('attributeId', '');
        let attrValue: string;
        if (selected.method === 'SELECT') {
          attrValue = this.fis.formGroupCollection[formId].get('attributeValueSelect' + append).value;
        } else {
          attrValue = this.fis.formGroupCollection[formId].get('attributeValueManual' + append).value;
        }
        return selected.id + ':' + attrValue
      }
    }).filter(e => e);
  }
  private getAddedAttrsForCtrl(ctrlName: string): string {
    let selected = this.attrList.find(e => e.id === this.fis.formGroupCollection[this.imageAttrSaleFormId].get(ctrlName).value);
    let append = ctrlName.replace('attributeId', '');
    let attrValue: string;
    if (selected.method === 'SELECT') {
      attrValue = this.fis.formGroupCollection[this.imageAttrSaleFormId].get('attributeValueSelect' + append).value;
    } else {
      attrValue = this.fis.formGroupCollection[this.imageAttrSaleFormId].get('attributeValueManual' + append).value;
    }
    return selected.id + ':' + attrValue
  }
  private hasAttrsForCtrl(): boolean {
    let attrFormValue = this.fis.formGroupCollection[this.imageAttrSaleFormId] && this.fis.formGroupCollection[this.imageAttrSaleFormId].value;
    if (attrFormValue)
      return Object.keys(attrFormValue).filter(e => e.includes('attributeId')).filter(idKey => attrFormValue[idKey]).length > 0;
    return false;
  }
  private _subChangeForForm(formId: string) {
    if (!this.subs[formId + '_valueChange']) {
      let sub = this.fis.formGroupCollection[formId].valueChanges.subscribe(next => {
        Object.keys(next).filter(e => e.includes('attributeId')).forEach(idKey => {
          let selected = this.attrList.find(e => e.id === next[idKey]);
          if (selected) {
            let append = idKey.replace('attributeId', '');
            this.fis.formGroupCollection_formInfo[formId].inputs.find(ee => ee.key === 'attributeValueSelect' + append).display = selected.method === 'SELECT';
            this.fis.formGroupCollection_formInfo[formId].inputs.find(ee => ee.key === 'attributeValueManual' + append).display = selected.method !== 'SELECT';
            if (selected.method === 'SELECT') {
              this.fis.formGroupCollection_formInfo[formId].inputs.find(ee => ee.key === 'attributeValueSelect' + append).options = selected.selectValues.map(e => <IOption>{ label: e, value: e })
            }
          }
        });
      });
      this.subs[formId + '_valueChange'] = sub;
    }
  }
  private _checkInput(key: string, formId: string) {
    if (this.fis.formGroupCollection[formId]) {
      if (!hasValue(this.fis.formGroupCollection[formId].get(key).value)) {
        this.fis.formGroupCollection_formInfo[formId].inputs.find(e => e.key === key).errorMsg = "REQUIRED";
        if (!this.subs['checkInput_' + key]) {
          let sub = this.fis.formGroupCollection[formId].get(key).valueChanges.subscribe(next => {
            if (!hasValue(next)) {
              this.fis.formGroupCollection_formInfo[formId].inputs.find(e => e.key === key).errorMsg = "REQUIRED";
            } else {
              this.fis.formGroupCollection_formInfo[formId].inputs.find(e => e.key === key).errorMsg = undefined
            }
          })
          this.subs['checkInput_' + key] = sub;
        }
      } else {
        this.fis.formGroupCollection_formInfo[formId].inputs.find(e => e.key === key).errorMsg = undefined
      }
    }
  }
  requiredInput(key: string, formId: string) {
    return hasValue(this.fis.formGroupCollection[formId].get(key).value)
  }
  create() {
    this._checkInput('status', this.formId)
    this._checkInput('hasSku', this.formId)
    if (this.hasSku) {
      Object.keys(this.fis.formGroupCollection_formInfo).filter(e => e.includes('attrSalesFormChild')).forEach(e => {
        this.fis.formGroupCollection_formInfo[e].inputs.forEach(ee => {
          if (ee.key.includes('attributeId')) {
            this._checkInput(ee.key, e)
          } else if (ee.key.includes('attributeValueSelect')) {
            this._checkInput(ee.key, e)
          } else if (ee.key.includes('attributeValueManual')) {
            this._checkInput(ee.key, e)
          } else {

          }

        })
      })
    }
    if (this.validateHelper.validate(this.validator, this.convertToPayload, 'adminCreateProductCommandValidator', this.fis, this, this.errorMapper)) {
      if (this.hasSku) {
        if (this.requiredInput('status', this.formId)
          && this.requiredInput('hasSku', this.formId)
          && this.checkInputs()
        ) {
          this.productSvc.create(this.convertToPayload(this), this.changeId);
        }
      } else {
        if (this.requiredInput('status', this.formId)
          && this.requiredInput('hasSku', this.formId)
        ) {
          this.productSvc.create(this.convertToPayload(this), this.changeId);
        }
      }
    }
  }
  checkInputs(): boolean {
    let output = true;
    Object.keys(this.fis.formGroupCollection_formInfo).forEach(formId => {
      this.fis.formGroupCollection_formInfo[formId].inputs.forEach(ee => {
        if (ee.display && ee.errorMsg) {
          output = false;
        }
      })
    })
    return output;
  }
  errorMapper(original: ErrorMessage[], cmpt: ProductComponent) {
    let next = cmpt.pareseOptionFormError(original, cmpt);
    let next2 = cmpt.pareseSkuFormError(next, cmpt);
    let next3 = cmpt.parseProductFormError(next2, cmpt);
    return next3
  }
  public pareseOptionFormError(original: ErrorMessage[], cmpt: ProductComponent) {
    if (original.some(e => e.formId === cmpt.optionFormId)) {
      return original.map(e => {
        if (e.formId === cmpt.optionFormId) {
          if (e.key.includes('optionValue')) {
            let var0 = e.key.split('_')[0] === '0' ? '' : "_" + (+e.key.split('_')[0] - 1);
            let var1 = e.key.split('_')[1] === '0' ? '' : "_" + (+e.key.split('_')[1] - 1);
            return {
              ...e,
              key: 'optionValue' + var1,
              formId: 'optionForm' + var0
            }
          } else {
            return e
          }
        } else {
          return e
        }
      })
    } else {
      return original
    }
  }
  public parseProductFormError(original: ErrorMessage[], cmpt: ProductComponent) {

    if (original.some(e => e.key === 'attributesKey')) {
      let next = original.map(e => {
        if (['attributesKey'].includes(e.key)) {
          return {
            ...e,
            key: 'selectBackendCatalog'
          }
        } else {
          return e
        }
      })
      return next
    } else {
      return original;
    }
  }
  public pareseSkuFormError(original: ErrorMessage[], cmpt: ProductComponent) {
    if (!cmpt.hasSku) {
      let next = original.map(e => {
        if (['0_sales', '0_price', '0_storageActual', '0_storageOrder', '0_decreaseActualStorage', '0_decreaseOrderStorage', '0_increaseActualStorage', '0_increaseOrderStorage'].includes(e.key)) {
          return {
            ...e,
            key: e.key.replace('0_', '')
          }
        } else {
          return e
        }
      })
      return next
    } else {
      let next = original.map(e => {
        if (e.key.includes('_sales')) {
          return {
            ...e,
            key: (e.key.split('_')[1] + "_" + e.key.split('_')[0]) === 'sales_0' ? 'sales' : e.key.split('_')[1] + "_" + (+e.key.split('_')[0] - 1),
            formId: this.attrSalesFormId
          }
        } else if (e.key.includes('_price')) {
          return {
            ...e,
            key: (e.key.split('_')[1] + "_" + e.key.split('_')[0]) === 'price_0' ? 'price' : e.key.split('_')[1] + "_" + (+e.key.split('_')[0] - 1),
            formId: this.attrSalesFormId
          }
        } else if (e.key.includes('_storageActual')) {
          return {
            ...e,
            key: ('storageActual' + "_" + e.key.split('_')[0]) === 'storageActual_0' ? 'storageActual' : 'storageActual' + "_" + (+e.key.split('_')[0] - 1),
            formId: this.attrSalesFormId
          }
        } else if (e.key.includes('_storageOrder')) {
          return {
            ...e,
            key: ('storageOrder' + "_" + e.key.split('_')[0]) === 'storageOrder_0' ? 'storageOrder' : 'storageOrder' + "_" + (+e.key.split('_')[0] - 1),
            formId: this.attrSalesFormId
          }
        } else if (e.key.includes('_increaseOrderStorage')) {
          return {
            ...e,
            key: ('storage_OrderIncreaseBy' + "_" + e.key.split('_')[0]) === 'storage_OrderIncreaseBy_0' ? 'storage_OrderIncreaseBy' : 'storage_OrderIncreaseBy' + "_" + (+e.key.split('_')[0] - 1),
            formId: this.attrSalesFormId
          }
        } else if (e.key.includes('_increaseActualStorage')) {
          return {
            ...e,
            key: ('storage_ActualIncreaseBy' + "_" + e.key.split('_')[0]) === 'storage_ActualIncreaseBy_0' ? 'storage_ActualIncreaseBy' : 'storage_ActualIncreaseBy' + "_" + (+e.key.split('_')[0] - 1),
            formId: this.attrSalesFormId
          }
        } else if (e.key.includes('_decreaseOrderStorage')) {
          return {
            ...e,
            key: ('storage_OrderDecreaseBy' + "_" + e.key.split('_')[0]) === 'storage_OrderDecreaseBy_0' ? 'storage_OrderDecreaseBy' : 'storage_OrderDecreaseBy' + "_" + (+e.key.split('_')[0] - 1),
            formId: this.attrSalesFormId
          }
        } else if (e.key.includes('_decreaseActualStorage')) {
          return {
            ...e,
            key: ('storage_ActualDecreaseBy' + "_" + e.key.split('_')[0]) === 'storage_ActualDecreaseBy_0' ? 'storage_ActualDecreaseBy' : 'storage_ActualDecreaseBy' + "_" + (+e.key.split('_')[0] - 1),
            formId: this.attrSalesFormId
          }
        } else {
          return e
        }
      })

      return next;
    }
  }
  update() {
    if (this.validateHelper.validate(this.validator, this.convertToPayload, 'adminUpdateProductCommandValidator', this.fis, this, this.errorMapper))
      this.productSvc.update(this.aggregate.id, this.convertToPayload(this), this.changeId)
  }
  parseProductForm() {
    let beforeParse = this.convertToPayload(this);
    let var1: IProductSkuPublic[] = [];
    let skuIds = new Set<string>();
    let lowestPrice = 0;
    beforeParse.skus.forEach(e => {
      e.attributesSales.forEach(ee => {
        skuIds.add(ee.split(":")[0])
      });
      var1.push(<IProductSkuPublic>{
        attributesSales: e.attributesSales,
        storage: e.storageOrder,
        price: e.price
      });
      if (lowestPrice === 0)
        lowestPrice = e.price
      if (lowestPrice > e.price)
        lowestPrice = e.price
    })
    let parsedIdMap = {};
    skuIds.forEach(id => {
      parsedIdMap[id] = this.attrList.find(e => e.id.toString() === id).name
    })
    let afterParse = <IProductDetailPublic>{
      name: beforeParse.name,
      imageUrlSmall: beforeParse.imageUrlSmall,
      imageUrlLarge: beforeParse.imageUrlLarge,
      description: beforeParse.description,
      attributeSaleImages: beforeParse.attributeSaleImages,
      selectedOptions: beforeParse.selectedOptions,
      skus: var1,
      attrIdMap: parsedIdMap,
      lowestPrice: lowestPrice
    }
    return afterParse;
  }
  convertToPayload(cmpt: ProductComponent): IProductDetail {
    let formGroup = cmpt.fis.formGroupCollection[cmpt.formId];
    let valueSnapshot = cmpt.fis.formGroupCollection[cmpt.imageFormId].value;
    let imagesUrl = Object.keys(valueSnapshot).map(e => valueSnapshot[e] as string).filter(e => e !== '');
    let selectedOptions: IProductOptions[] = [];
    Object.keys(cmpt.fis.formGroupCollection[cmpt.optionFormId].controls).filter(e => e.indexOf('productOption') > -1).forEach((ctrlName) => {
      let var1 = <IProductOptions>{};
      var1.title = cmpt.fis.formGroupCollection[cmpt.optionFormId].get(ctrlName).value;
      var1.options = [];
      let fg = cmpt.fis.formGroupCollection['optionForm' + ctrlName.replace('productOption', '')];
      var1.options = Object.keys(fg.controls).filter(e => e.indexOf('optionValue') > -1).map(e => {
        return <IProductOption>{
          optionValue: fg.get(e).value,
          priceVar: fg.get(e.replace('optionValue', 'optionPriceChange')).value,
        }
      });
      selectedOptions.push(var1)
    });
    let skusCalc: ISku[] = [];
    if (cmpt.hasSku && cmpt.fis.formGroupCollection[cmpt.attrSalesFormId]) {
      Object.keys(cmpt.fis.formGroupCollection[cmpt.attrSalesFormId].controls).filter(e => e.indexOf('storageOrder') > -1).forEach((ctrlName) => {
        let var1 = <ISku>{};
        let suffix = ctrlName.replace('storageOrder', '');
        var1.attributesSales = cmpt.getAddedAttrs(cmpt.salesFormIdTempId + suffix);
        var1.price = +cmpt.fis.formGroupCollection[cmpt.attrSalesFormId].get('price' + suffix)?.value;
        if (cmpt.productBottomSheet.context === 'new' || cmpt.productBottomSheet.context === 'clone') {
          //create
          var1.storageOrder = +cmpt.fis.formGroupCollection[cmpt.attrSalesFormId].get('storageOrder' + suffix)?.value;
          var1.storageActual = +cmpt.fis.formGroupCollection[cmpt.attrSalesFormId].get('storageActual' + suffix)?.value;
          var1.sales = +cmpt.fis.formGroupCollection[cmpt.attrSalesFormId].get('sales' + suffix)?.value;
        } else if (cmpt.aggregate && cmpt.udpateSkusOriginalCopy.find(e => JSON.stringify(e.attributesSales.sort()) === JSON.stringify(var1.attributesSales.sort()))) {
          let var11 = cmpt.fis.formGroupCollection[cmpt.attrSalesFormId].get('storage_OrderIncreaseBy' + suffix).value;
          if (var11)
            var1.increaseOrderStorage = +var11;
          let var12 = cmpt.fis.formGroupCollection[cmpt.attrSalesFormId].get('storage_OrderDecreaseBy' + suffix).value;
          if (var12)
            var1.decreaseOrderStorage = +var12;
          let var13 = cmpt.fis.formGroupCollection[cmpt.attrSalesFormId].get('storage_ActualIncreaseBy' + suffix).value;
          if (var13)
            var1.increaseActualStorage = +var13;
          let var14 = cmpt.fis.formGroupCollection[cmpt.attrSalesFormId].get('storage_ActualDecreaseBy' + suffix).value;
          if (var14)
            var1.decreaseActualStorage = +var14;
        } else if (cmpt.aggregate && cmpt.udpateSkusOriginalCopy.find(e => JSON.stringify(e.attributesSales.sort()) !== JSON.stringify(var1.attributesSales.sort()))) {
          //new sku during update product
          var1.storageOrder = +cmpt.fis.formGroupCollection[cmpt.attrSalesFormId].get('storageOrder' + suffix).value;
          var1.storageActual = +cmpt.fis.formGroupCollection[cmpt.attrSalesFormId].get('storageActual' + suffix).value;
          var1.sales = +cmpt.fis.formGroupCollection[cmpt.attrSalesFormId].get('sales' + suffix).value;
        } else {

        }
        skusCalc.push(var1)
      });
    } else {
      let var1 = <ISku>{};
      var1.attributesSales = [];
      if (!cmpt.aggregate) {
        //create
        var1.storageOrder = +formGroup.get('storageOrder').value
        var1.storageActual = +formGroup.get('storageActual').value
        var1.price = +formGroup.get('price').value;
        var1.sales = +formGroup.get('sales').value;
        skusCalc.push(var1)
      } else {
        //update
        var1.increaseOrderStorage = +formGroup.get('storage_OrderIncreaseBy').value
        var1.decreaseOrderStorage = +formGroup.get('storage_OrderDecreaseBy').value
        var1.increaseActualStorage = +formGroup.get('storage_ActualIncreaseBy').value
        var1.decreaseActualStorage = +formGroup.get('storage_ActualDecreaseBy').value
        var1.price = +formGroup.get('price').value;
        var1.sales = +formGroup.get('sales').value;
        skusCalc.push(var1)
      }
    }
    let attrSaleImages = [];
    if (cmpt.hasSku && cmpt.hasAttrsForCtrl()) {
      Object.keys(cmpt.fis.formGroupCollection[cmpt.imageAttrSaleFormId].controls).filter(e => e.indexOf('attributeId') > -1).forEach((ctrlName) => {
        let var1 = <IAttrImage>{};
        var1.attributeSales = cmpt.getAddedAttrsForCtrl(ctrlName)
        let append = ctrlName.replace('attributeId', '');
        let childFormValue = cmpt.fis.formGroupCollection[cmpt.imageAttrSaleChildFormId + append].value;
        var1.imageUrls = Object.keys(childFormValue).map(e => childFormValue[e] as string).filter(e => e !== '');;
        attrSaleImages.push(var1)
      });
    }
    return {
      id: formGroup.get('id').value,
      attributesKey: formGroup.get('attributesKey').value,
      attributesProd: cmpt.hasAttr(cmpt.attrProdFormId) ? cmpt.getAddedAttrs(cmpt.attrProdFormId) : null,
      attributesGen: cmpt.hasAttr(cmpt.attrGeneralFormId) ? cmpt.getAddedAttrs(cmpt.attrGeneralFormId) : null,
      name: formGroup.get('name').value,
      imageUrlSmall: formGroup.get('imageUrlSmall').value,
      description: formGroup.get('description').value,
      imageUrlLarge: imagesUrl,
      selectedOptions: selectedOptions.filter(e => e.title !== ''),
      skus: skusCalc,
      endAt: formGroup.get('endAtDate').value ? cmpt._parseDate(formGroup.get('endAtDate').value, formGroup.get('endAtTime').value) : undefined,
      startAt: formGroup.get('startAtDate').value ? cmpt._parseDate(formGroup.get('startAtDate').value, formGroup.get('startAtTime').value) : undefined,
      attributeSaleImages: attrSaleImages,
      version: cmpt.aggregate && cmpt.aggregate.version
    }
  }
}
