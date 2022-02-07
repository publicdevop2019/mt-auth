import { Component, OnDestroy } from '@angular/core';
import { MatBottomSheet } from '@angular/material/bottom-sheet';
import { ActivatedRoute } from '@angular/router';
import { FormInfoService } from 'mt-form-builder';
import { IForm, IOption } from 'mt-form-builder/lib/classes/template.interface';
import { combineLatest, Observable } from 'rxjs';
import { CATALOG_TYPE } from 'src/app/clazz/constants';
import { ISumRep, SummaryEntityComponent } from 'src/app/clazz/summary.component';
import { ICatalog } from 'src/app/clazz/validation/aggregate/catalog/interfaze-catalog';
import { ISearchConfig } from 'src/app/components/search/search.component';
import { FORM_CONFIG } from 'src/app/form-configs/view.config';
import { CatalogService } from 'src/app/services/catalog.service';
import { DeviceService } from 'src/app/services/device.service';
import { copyOf } from 'src/app/services/utility';
import { CatalogComponent } from '../catalog/catalog.component';

@Component({
  selector: 'app-summary-category',
  templateUrl: './summary-catalog.component.html',
})
export class SummaryCatalogComponent extends SummaryEntityComponent<ICatalog, ICatalog> implements OnDestroy {
  formId2 = 'summaryCatalogCustomerView';
  formInfo: IForm = JSON.parse(JSON.stringify(FORM_CONFIG));
  viewType: "TREE_VIEW" | "LIST_VIEW" | "DYNAMIC_TREE_VIEW" = "LIST_VIEW";
  public formId = "mallCatalogTableColumnConfig";
  columnList = {
    id: 'ID',
    name: 'NAME',
    parentId: 'PARENT_ID',
    edit: 'EDIT',
    delete: 'DELETE',
    review: 'REVIEW_REQUIRED',
  }
  sheetComponent = CatalogComponent;
  private formCreatedOb2: Observable<string>;
  initSearchConfigs: ISearchConfig[] = [
    {
      searchLabel: 'ID',
      searchValue: 'id',
      type: 'text',
      multiple: {
        delimiter: '.'
      }
    },
    {
      searchLabel: 'NAME',
      searchValue: 'name',
      type: 'text',
      multiple: {
        delimiter: '.'
      }
    }
  ]
  searchConfigs: ISearchConfig[] = copyOf(this.initSearchConfigs);
  public loadRoot = this.entitySvc.readByQuery(0, 10, ",parentId:null")
  public loadChildren = (id: string) => this.entitySvc.readByQuery(0, 10, ",parentId:" + id)
  constructor(
    public entitySvc: CatalogService,
    public deviceSvc: DeviceService,
    public bottomSheet: MatBottomSheet,
    private route: ActivatedRoute,
    fis: FormInfoService,
  ) {

    super(entitySvc, deviceSvc, bottomSheet, fis, 5);
    this.formCreatedOb2 = this.fis.formCreated(this.formId2);

    const sub0 = combineLatest([this.formCreatedOb2]).subscribe(() => {
      const sub = this.fis.formGroupCollection[this.formId2].valueChanges.subscribe(e => {
        this.viewType = e.view;
        if (this.viewType === 'TREE_VIEW') {
          this.entitySvc.readEntityByQuery(0, 1000).subscribe(next => {
            super.updateSummaryData(next)
          });
        }
      });
      this.subs.add(sub)
    })
    this.subs.add(sub0)
    const sub = combineLatest([this.formCreatedOb2, this.route.paramMap]).subscribe(combine => {
      if (combine[1].get('type') === 'frontend') {
        if (this.entitySvc.queryPrefix !== CATALOG_TYPE.FRONTEND) {
          this.entitySvc.queryPrefix = CATALOG_TYPE.FRONTEND;
          this.prepareDashboard(combine[1].get('type'))
        }
      } else {
        if (this.entitySvc.queryPrefix !== CATALOG_TYPE.BACKEND) {
          this.entitySvc.queryPrefix = CATALOG_TYPE.BACKEND;
          this.prepareDashboard(combine[1].get('type'))
        }
      }
      if (!this.fis.formGroupCollection[this.formId2].get('view').value) {
        this.fis.formGroupCollection[this.formId2].get('view').setValue(this.viewType);
      }

    })
    this.subs.add(sub)
  }
  prepareDashboard(catalogType: string) {
    this.deviceSvc.refreshSummary.next();
    //update search config
    this.entitySvc.readEntityByQuery(0, 1000)//@todo use paginated select component
      .subscribe(catalogs => {
        if (catalogs.data) {
          this.searchConfigs = [...copyOf(this.initSearchConfigs), {
            searchLabel: catalogType === 'frontend' ? 'PARENT_ID_FRONT' : 'PARENT_ID_BACK',
            searchValue: 'parentId',
            type: 'dropdown',
            source: catalogs.data.map(e => {
              return {
                label: e.name,
                value: e.id
              }
            })
          }];
        }
      });

  }
  catalogList: IOption[] = [];
  updateSummaryData(inputs: ISumRep<ICatalog>) {
    super.updateSummaryData(inputs);
    let parentId: string[] = inputs.data.map(e => e.parentId).filter(e => e);
    if (parentId.length > 0)
      this.entitySvc.readEntityByQuery(0, parentId.length, 'id:' + parentId.join('.')).subscribe(next => {
        this.catalogList = next.data.map(e => <IOption>{ label: e.name, value: e.id });
      });
  }
  ngOnDestroy(): void {
    this.fis.reset(this.formId2);
    this.entitySvc.queryPrefix = undefined;
    super.ngOnDestroy();
  }
  getOption(value: string, options: IOption[]) {
    return options.find(e => e.value == value)
  }
}
