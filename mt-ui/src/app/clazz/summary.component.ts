import { SelectionModel } from '@angular/cdk/collections';
import { ComponentType } from '@angular/cdk/portal';
import { Directive, OnDestroy, ViewChild } from '@angular/core';
import { MatBottomSheet, MatBottomSheetConfig } from '@angular/material/bottom-sheet';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { Sort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { FormInfoService } from 'mt-form-builder';
import { ICheckboxControl } from 'mt-form-builder/lib/classes/template.interface';
import { Observable, Subscription } from 'rxjs';
import { IEditEvent } from 'src/app/components/editable-field/editable-field.component';
import { DeviceService } from 'src/app/services/device.service';
import { IEditBooleanEvent } from '../components/editable-boolean/editable-boolean.component';
import { IEditInputListEvent } from '../components/editable-input-multi/editable-input-multi.component';
import { IEditListEvent } from '../components/editable-select-multi/editable-select-multi.component';
import { ISearchEvent, SearchComponent } from '../components/search/search.component';
import { FORM_TABLE_COLUMN_CONFIG } from '../form-configs/table-column.config';
import { Utility } from '../misc/utility';
import { TABLE_SETTING_KEY } from '../misc/constant';
export interface IIdBasedEntity {
  id: string;
  version: number
}
export interface IEntityService<C extends IIdBasedEntity, D> {
  readById: (id: string) => Observable<D>;
  readEntityByQuery: (num: number, size: number, query?: string, by?: string, order?: string) => Observable<ISumRep<C>>;
  deleteByQuery: (query: string, changeId: string) => void;
  deleteById: (id: string, changeId: string) => void;
  create: (s: D, changeId: string) => void;
  update: (id: string, s: D, changeId: string, version: number) => void;
  patch: (id: string, event: IEditEvent, changeId: string, fieldName: string) => void;
  patchAtomicNum: (id: string, event: IEditEvent, changeId: string, fieldName: string) => void;
  patchList: (id: string, event: IEditListEvent, changeId: string, fieldName: string) => void;
  patchMultiInput: (id: string, event: IEditInputListEvent, changeId: string, fieldName: string) => void;
  patchBoolean: (id: string, event: IEditBooleanEvent, changeId: string, fieldName: string) => void;
  pageNumber: number;
}
export interface ISumRep<T> {
  data: T[],
  totalItemCount: number
}
export interface IBottomSheet<S> {
  context: 'clone' | 'new' | 'edit';
  from: S;
  params: {}
}
@Directive()
export class SummaryEntityComponent<T extends IIdBasedEntity, S extends T> implements OnDestroy {
  sheetComponent: ComponentType<any>;
  columnWidth: number;
  columnList: any;
  bottomSheetParams = {};
  queryString: string = undefined;
  formId: string = undefined;
  queryKey: string = undefined;
  dataSource: MatTableDataSource<T>;
  totoalItemCount = 0;
  pageSizeOffset = 0;
  pageSize = 0;
  sortBy: string = undefined;
  sortOrder: string = undefined;
  protected subs: Subscription = new Subscription()
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(SearchComponent, { static: true }) searcher: SearchComponent;
  selection = new SelectionModel<T>(true, []);
  constructor(
    protected entitySvc: IEntityService<T, S>,
    protected deviceSvc: DeviceService,
    protected bottomSheet: MatBottomSheet,
    protected fis: FormInfoService,
    protected _pageSizeOffset: number,
    protected skipInitialLoad?: boolean
  ) {
    this.pageSizeOffset = _pageSizeOffset;
    this.initUrlRelatedValues();
  }
  
  initUrlRelatedValues() {
    this.entitySvc.pageNumber = this.getPageNum(this.deviceSvc.getParams().page);
    this.pageSize = this.getPageSize(this.deviceSvc.getParams().page);
    if (this.pageSize === -1) {
      this.pageSize = this.getDefaultPageSize();
      this.deviceSvc.updateURLQueryParamPageAndSort(this.entitySvc.pageNumber, this.pageSize, this.sortBy, this.sortOrder)
    }
    if (this.entitySvc.pageNumber === -1) {
      this.entitySvc.pageNumber = 0;
      this.deviceSvc.updateURLQueryParamPageAndSort(this.entitySvc.pageNumber, this.pageSize, this.sortBy, this.sortOrder)
    }
  }
  private getPageNum(input: string): number {
    if (input) {
      if (input.split(',').filter(e => e.includes('num')).length === 1) {
        const numStr = input.split(',').filter(e => e.includes('num:'))[0];
        if (numStr.split(":")[1]) {
          const next = +numStr.split(":")[1]
          if (!Number.isNaN(next))
            return next
        }
      }
    }
    return -1;
  }
  private getPageSize(input: string): number {
    if (input) {
      if (input.split(',').filter(e => e.includes('size')).length === 1) {
        const numStr = input.split(',').filter(e => e.includes('size:'))[0];
        if (numStr.split(":")[1]) {
          const next = +numStr.split(":")[1]
          if (!Number.isNaN(next))
            return next
        }
      }
    }
    return -1;
  }
  ngOnDestroy(): void {
    this.subs.unsubscribe();
  }
  displayedColumns() {
    if (this.fis.formGroups[this.formId]) {
      const orderKeys = ['select', ...Object.keys(this.columnList)];
      const value = this.fis.formGroups[this.formId].get(TABLE_SETTING_KEY).value as string[]
      return orderKeys.filter(e => value.includes(e))
    } else {
      return Object.keys(this.columnList)
    }
  };
  openBottomSheet(id?: string, clone?: boolean, bypassQuery?: boolean): void {
    const config = new MatBottomSheetConfig();
    config.autoFocus = true;
    config.panelClass = 'fix-height'
    const doNext = (data: S | T) => {
      if (clone) {
        config.data = <IBottomSheet<S>>{ context: 'clone', from: data, params: this.bottomSheetParams };
        this.bottomSheet.open(this.sheetComponent, config);
      } else {
        config.data = <IBottomSheet<S>>{ context: 'edit', from: data, params: this.bottomSheetParams };
        this.bottomSheet.open(this.sheetComponent, config);
      }
    }
    if (Utility.hasValue(id)) {
      if (bypassQuery) {
        const data = this.dataSource.data.find(e => e.id === id)!
        doNext(data)
      } else {
        this.entitySvc.readById(id).subscribe(next => {
          doNext(next)
        })
      }
    } else {
      config.data = <IBottomSheet<S>>{ context: 'new', from: undefined, params: this.bottomSheetParams };
      this.bottomSheet.open(this.sheetComponent, config);
    }
  }
  pageHandler(e: PageEvent) {
    this.entitySvc.pageNumber = e.pageIndex;
    this.deviceSvc.updateURLQueryParamBeforeSearch(this.entitySvc.pageNumber, this.pageSize, this.queryString, this.sortBy, this.sortOrder, this.queryKey);
    this.entitySvc.readEntityByQuery(this.entitySvc.pageNumber, this.pageSize, this.queryString, this.sortBy, this.sortOrder).subscribe(next => {
      this.updateSummaryData(next);
    });
  }
  protected getDefaultPageSize() {
    return this.pageSize = (this.deviceSvc.pageSize - this.pageSizeOffset) > 0 ? (this.deviceSvc.pageSize - this.pageSizeOffset) : 1;
  }
  protected setPageSize(size: number) {
    this.pageSize = size;
  }
  protected updateSummaryData(next: ISumRep<T>) {
    if (next.data) {
      this.dataSource = new MatTableDataSource(next.data);
      this.totoalItemCount = next.totalItemCount;
    } else {
      this.dataSource = new MatTableDataSource([]);
      this.totoalItemCount = 0;
    }
    this.selection.clear();
  }
  updateTable(sort: Sort) {
    this.sortBy = sort.active;
    this.sortOrder = sort.direction;
    this.entitySvc.readEntityByQuery(this.entitySvc.pageNumber, this.pageSize, this.queryString, this.sortBy, this.sortOrder).subscribe(next => {
      this.updateSummaryData(next)
    });
  }
  showOptions() {
    if (!this.displayedColumns().includes('select')) {
      this.fis.formGroups[this.formId].get(TABLE_SETTING_KEY).setValue(['select', ...this.displayedColumns()])
    } else {
      this.fis.formGroups[this.formId].get(TABLE_SETTING_KEY).setValue(this.displayedColumns().filter(e => e !== 'select'))
    }
  }
  getColumnLabelValue() {
    return Object.keys(this.columnList).map(e => ({ label: this.columnList[e], value: e }))
  }
  /** Whether the number of selected elements matches the total number of rows. */
  isAllSelected() {
    const numSelected = this.selection.selected.length;
    const numRows = this.dataSource ? this.dataSource.data.length : 0;
    return numSelected === numRows;
  }

  /** Selects all rows if they are not all selected; otherwise clear selection. */
  masterToggle() {
    this.isAllSelected() ?
      this.selection.clear() :
      this.dataSource.data.forEach(row => this.selection.select(row));
  }

  /** The label for the checkbox on the passed row */
  checkboxLabel(row?: T): string {
    if (!row) {
      return `${this.isAllSelected() ? 'select' : 'deselect'} all`;
    }
    return `${this.selection.isSelected(row) ? 'deselect' : 'select'} row ${row.id + 1}`;
  }
  doBatchDelete() {
    let ids = this.selection.selected.map(e => e.id)
    this.entitySvc.deleteByQuery(this.getIdQuery(ids), Utility.getChangeId())
  }
  doPatch(id: string, event: IEditEvent, fieldName: string) {
    this.entitySvc.patch(id, event, Utility.getChangeId(), fieldName)
  }
  doMultiInputPatch(id: string, event: IEditInputListEvent, fieldName: string) {
    this.entitySvc.patchMultiInput(id, event, Utility.getChangeId(), fieldName)
  }
  doPatchBoolean(id: string, event: IEditBooleanEvent, fieldName: string) {
    this.entitySvc.patchBoolean(id, event, Utility.getChangeId(), fieldName)
  }
  doPatchAtomicNum(id: string, event: IEditEvent, fieldName: string) {
    this.entitySvc.patchAtomicNum(id, event, Utility.getChangeId(), fieldName)
  }
  doPatchList(id: string, event: IEditListEvent, fieldName: string) {
    this.entitySvc.patchList(id, event, Utility.getChangeId(), fieldName)
  }
  doClone(id: string) {
    this.openBottomSheet(id, true)
  }
  doDeleteById(id: string) {
    this.entitySvc.deleteById(id, Utility.getChangeId())
  }
  doDeleteByQuery(query: string) {
    this.entitySvc.deleteByQuery(query, Utility.getChangeId())
  }
  doSearch(config: ISearchEvent) {
    this.queryString = config.value;
    this.queryKey = config.key;

    if (config.resetPage) {
      this.entitySvc.pageNumber = 0;
      if (!config.value) {//reset sort as well
        this.sortBy = undefined;
        this.sortOrder = undefined;
      }
    }
    this.deviceSvc.updateURLQueryParamBeforeSearch(this.entitySvc.pageNumber, this.pageSize, this.queryString, this.sortBy, this.sortOrder, this.queryKey);
    this.entitySvc.readEntityByQuery(this.entitySvc.pageNumber, this.pageSize, this.queryString, this.sortBy, this.sortOrder).subscribe(next => {

      this.updateSummaryData(next);
    })
  }
  private getIdQuery(ids: string[]): string {
    return 'id:' + ids.join(".")
  }
  protected initTableSetting(){
    const deepCopy = Utility.copyOf(FORM_TABLE_COLUMN_CONFIG)
    const settingKey = deepCopy.inputs[0].key;
    const options = this.getColumnLabelValue();
    (deepCopy.inputs[0] as ICheckboxControl).options = options;
    this.fis.init(deepCopy, this.formId)
    this.fis.formGroups[this.formId].get(settingKey).setValue(options.map(e => e.value))
  }
}
