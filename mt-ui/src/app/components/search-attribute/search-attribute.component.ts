import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormInfoService } from 'mt-form-builder';
import { IForm, IOption } from 'mt-form-builder/lib/classes/template.interface';
import { Subscription } from 'rxjs';
import { filter, take } from 'rxjs/operators';
import { IBizAttribute } from 'src/app/clazz/validation/aggregate/attribute/interfaze-attribute';
import { FORM_SEARCH_CATALOG_CONFIG } from 'src/app/form-configs/search.config';
import { AttributeService } from 'src/app/services/attribute.service';
import { SearchComponent } from '../search/search.component';
@Component({
  templateUrl: './search-attribute.component.html',
})
export class SearchAttributeComponent implements OnDestroy, OnInit {
  formId: string = 'searchFormNew'
  formInfo: IForm = JSON.parse(JSON.stringify(FORM_SEARCH_CATALOG_CONFIG));
  private searchRef: SearchComponent;
  private subs: Subscription = new Subscription();
  constructor(private fis: FormInfoService, private attrSvc: AttributeService) {
    this.fis.queryProvider[this.formId + '_' + 'searchByAttr'] = this.attrSvc;
    this.fis.formCreated(this.formId).subscribe(_ => {
      this.fis.formGroupCollection[this.formId].get('')
      let sub2 = this.fis.formGroupCollection[this.formId].valueChanges.subscribe(next => {
        let selected = (this.formInfo.inputs.find(e => e.key === 'searchByAttr').optionOriginal)?.find(e => e.id === next['searchByAttr']) as IBizAttribute
        if (selected) {
          this.formInfo.inputs.find(ee => ee.key === 'searchByAttrSelect').display = selected.method === 'SELECT';
          this.formInfo.inputs.find(ee => ee.key === 'searchByAttrManual').display = selected.method !== 'SELECT';
          if (selected.method === 'SELECT') {
            this.formInfo.inputs.find(ee => ee.key === 'searchByAttrSelect').options = selected.selectValues.map(e => <IOption>{ label: e, value: e })
          }
        }
      });
      this.subs.add(sub2);
    });
  }
  ngOnInit(): void {
  }
  ngOnChanges(): void {
  }
  ngOnDestroy(): void {
    this.fis.reset(this.formId)
    this.subs.unsubscribe()
  }
  appendAttr() {
    let var1 = (this.formInfo.inputs[0].optionOriginal.find(e => e.id === this.fis.formGroupCollection[this.formId].get('searchByAttr').value) as IBizAttribute);
    let var2 = var1.name + ":" + (var1.method === 'SELECT' ? this.fis.formGroupCollection[this.formId].get('searchByAttrSelect').value : this.fis.formGroupCollection[this.formId].get('searchByAttrManual').value);
    let var3 = var1.id + ":" + (var1.method === 'SELECT' ? this.fis.formGroupCollection[this.formId].get('searchByAttrSelect').value : this.fis.formGroupCollection[this.formId].get('searchByAttrManual').value);
    this.searchRef.searchItems.push({ label: var2, value: var3 });
    this.searchRef.searchQuery.setValue(this.searchRef.searchItems.map(e=>e.value))
  }
}
