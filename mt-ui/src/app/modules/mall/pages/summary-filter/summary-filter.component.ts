import { Component, OnDestroy } from '@angular/core';
import { MatBottomSheet } from '@angular/material/bottom-sheet';
import { FormInfoService } from 'mt-form-builder';
import { IOption } from 'mt-form-builder/lib/classes/template.interface';
import { ISumRep, SummaryEntityComponent } from 'src/app/clazz/summary.component';
import { IBizFilter } from 'src/app/clazz/validation/aggregate/filter/interfaze-filter';
import { ISearchConfig } from 'src/app/components/search/search.component';
import { CatalogService } from 'src/app/services/catalog.service';
import { DeviceService } from 'src/app/services/device.service';
import { FilterService } from 'src/app/services/filter.service';
import { FilterComponent } from '../filter/filter.component';

@Component({
  selector: 'app-summary-filter',
  templateUrl: './summary-filter.component.html',
})
export class SummaryFilterComponent extends SummaryEntityComponent<IBizFilter, IBizFilter> implements OnDestroy {
  public formId = "mallFilterTableColumnConfig";
  columnList = {
    id: 'ID',
    description: 'DESCRIPTION',
    catalogs: 'CATALOGS',
    edit: 'EDIT',
    delete: 'DELETE',
    review: 'REVIEW_REQUIRED',
  }
  sheetComponent = FilterComponent;
  public catalogList: IOption[] = []
  searchConfigs: ISearchConfig[] = [
  ]
  initSearchConfigs: ISearchConfig[] = [
    {
      searchLabel: 'ID',
      searchValue: 'id',
      type: 'text',
      multiple: {
        delimiter: '.'
      }
    }
  ]
  constructor(
    public entitySvc: FilterService,
    public deviceSvc: DeviceService,
    protected bottomSheet: MatBottomSheet,
    public catalogSvc: CatalogService,
    public fis: FormInfoService,
  ) {
    super(entitySvc, deviceSvc, bottomSheet,fis, 0);
    this.catalogSvc.readEntityByQuery(0, 1000, 'type:FRONTEND').subscribe(next => {
      this.searchConfigs = [...this.initSearchConfigs,
      {
        searchLabel: 'CATALOGS',
        searchValue: 'catalog',
        type: 'dropdown',
        source: next.data.map(e => { return { label: e.name, value: e.id } })
      }]
    })
  }

  updateSummaryData(next: ISumRep<IBizFilter>) {
    super.updateSummaryData(next)
    let var0 = new Set(next.data.flatMap(e => e.catalogs));
    let var1 = new Array(...var0);
    if (var1.length > 0) {
      this.catalogSvc.readEntityByQuery(0, var1.length, 'type:FRONTEND,id:' + var1.join('.')).subscribe(next => {
        this.catalogList = next.data.map(e => <IOption>{ label: e.name, value: e.id })
      })
    }
  }
  getCatalogList(inputs: string[]): IOption[] {
    return this.catalogList.filter(e => inputs.includes(e.value + ""))
  }
}
