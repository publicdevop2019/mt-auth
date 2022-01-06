import { Component, OnDestroy } from '@angular/core';
import { MatBottomSheet } from '@angular/material/bottom-sheet';
import { MatDialog } from '@angular/material/dialog';
import { FormInfoService } from 'mt-form-builder';
import { IOption } from 'mt-form-builder/lib/classes/template.interface';
import { combineLatest, of } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { CATALOG_TYPE } from 'src/app/clazz/constants';
import { SummaryEntityComponent } from 'src/app/clazz/summary.component';
import { ICatalog } from 'src/app/clazz/validation/aggregate/catalog/interfaze-catalog';
import { IProductDetail, IProductSimple } from 'src/app/clazz/validation/aggregate/product/interfaze-product';
import { OperationConfirmDialogComponent } from 'src/app/components/operation-confirm-dialog/operation-confirm-dialog.component';
import { SearchAttributeComponent } from 'src/app/components/search-attribute/search-attribute.component';
import { ISearchConfig, SearchComponent } from 'src/app/components/search/search.component';
import { AttributeService } from 'src/app/services/attribute.service';
import { CatalogService } from 'src/app/services/catalog.service';
import { DeviceService } from 'src/app/services/device.service';
import { ProductService } from 'src/app/services/product.service';
import { isNullOrUndefined } from 'util';
import * as UUID from 'uuid/v1';
import { CatalogTreeComponent } from '../../components/catalog-tree/catalog-tree.component';
import { ProductComponent } from '../product/product.component';
@Component({
  selector: 'app-summary-product',
  templateUrl: './summary-product.component.html',
})
export class SummaryProductComponent extends SummaryEntityComponent<IProductSimple, IProductDetail> implements OnDestroy {
  sheetComponent = ProductComponent;
  public formId = "mallProductTableColumnConfig";
  columnList = {
    id: 'ID',
    coverImage: 'COVER_IMAGE',
    name: 'NAME',
    sales: 'TOTAL_SALES',
    status: 'AVAILABLE',
    endAt: 'EXPIREAT',
    edit: 'EDIT',
    delete: 'DELETE',
    clone: 'CLONE',
    review: 'REVIEW_REQUIRED',
  }
  searchConfigs: ISearchConfig[] = []
  private initSearchConfig: ISearchConfig[] = [
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
    },
    {
      searchLabel: 'LOWEST_PRICE',
      searchValue: 'lowestPrice',
      type: 'range',
    }
  ]
  constructor(
    public entitySvc: ProductService,
    public deviceSvc: DeviceService,
    public bottomSheet: MatBottomSheet,
    public dialog: MatDialog,
    private catalogSvc: CatalogService,
    private attrSvc: AttributeService,
    public fis: FormInfoService
  ) {
    super(entitySvc, deviceSvc, bottomSheet,fis, 3);
    const queryCatalog = (event: any, searchCmpt: SearchComponent, catalogs: ICatalog[]) => {
      const attr = this.loadAttributes(event, catalogs)
      this.parseAttrId(attr).subscribe((next: string[]) => {
        const var1 = next.map((e, i) => {
          return { label: e, value: this.loadAttributes(event, catalogs)[i] } as IOption
        })
        searchCmpt.searchItems = var1;
        searchCmpt.searchQuery.setValue(var1.map(e => e.value))
      })
    }
    const resumeCatalog = (searchCmpt: SearchComponent, queryValue: string, config: ISearchConfig) => {
      const ids = queryValue.split('.').map(e => e.split('-')[0])
      this.attrSvc.readEntityByQuery(0, ids.length, 'id:' + ids.join('.'), undefined, undefined, { loading: true })
        .subscribe(next => {
          const mapped = queryValue.split('.').map(e => {
            const id = e.split('-')[0]
            const label = next.data.find(ee => ee.id === id).name + ':' + e.split('-')[1]
            return { value: e, label: label }
          })
          searchCmpt.searchItems = mapped;
          searchCmpt.searchQuery.setValue(searchCmpt.searchItems.map(e => e.value), { emitEvent: false })
          searchCmpt.search.emit({ value: searchCmpt.getFinalQuery(config, searchCmpt.searchQuery.value), resetPage: false, key: config.key });
        })
      searchCmpt.searchLevel1.setValue(config, { emitEvent: false });
    }
    //@todo use paginated select component
    combineLatest([this.catalogSvc.readEntityByQuery(this.catalogSvc.pageNumber, 1000, CATALOG_TYPE.FRONTEND), this.catalogSvc.readEntityByQuery(this.catalogSvc.pageNumber, 1000, CATALOG_TYPE.BACKEND)])
      .subscribe(response => {
        if (response[0].data) {
          const catalogFrontConfig: ISearchConfig = {
            searchLabel: 'SEARCH_BY_CATALOG_FRONT',
            searchValue: 'attributes',
            type: 'custom',
            key: '0',
            component: CatalogTreeComponent,
            componentInputMap: { 'catalogs': response[0].data },
            componentOutputMap: {
              'leafNodeClicked': (event: any, searchCmpt: SearchComponent) => {
                queryCatalog(event, searchCmpt, response[0].data)
              },
              'nonLeafNodeClicked': (event: any, searchCmpt: SearchComponent) => {
                queryCatalog(event, searchCmpt, response[0].data)
              }
            },
            resumeFromUrl: (searchCmpt: SearchComponent, queryValue: string) => { resumeCatalog(searchCmpt, queryValue, catalogFrontConfig) }
          }
          const catalogBackConfig: ISearchConfig = {
            searchLabel: 'SEARCH_BY_CATALOG_BACK',
            searchValue: 'attributes',
            type: 'custom',
            key: '1',
            component: CatalogTreeComponent,
            componentInputMap: { 'catalogs': response[1].data },
            componentOutputMap: {
              'leafNodeClicked': (event: any, searchCmpt: SearchComponent) => {
                queryCatalog(event, searchCmpt, response[1].data)
              },
              'nonLeafNodeClicked': (event: any, searchCmpt: SearchComponent) => {
                queryCatalog(event, searchCmpt, response[1].data)
              }
            },
            resumeFromUrl: (searchCmpt: SearchComponent, queryValue: string) => { resumeCatalog(searchCmpt, queryValue, catalogBackConfig) }
          }
          const attrConfig: ISearchConfig = {
            searchLabel: 'SEARCH_BY_ATTRIBUTES',
            searchValue: 'attributes',
            type: 'custom',
            key: '2',
            component: SearchAttributeComponent,
            resumeFromUrl: (searchCmpt: SearchComponent, queryValue: string) => { resumeCatalog(searchCmpt, queryValue, attrConfig) }
          }
          this.searchConfigs = [...this.initSearchConfig,
            catalogFrontConfig,
            catalogBackConfig,
            attrConfig
          ]
        }
      });
  }
  private loadAttributes(attr: ICatalog, catalogs: ICatalog[]) {
    let tags: string[] = [];
    tags.push(...attr.attributes);
    while (attr.parentId !== null && attr.parentId !== undefined) {
      let nextId = attr.parentId;
      attr = catalogs.find(e => e.id === nextId);
      tags.push(...attr.attributes);
    }
    return tags;
  }
  private parseAttrId(attributes: string[]) {
    if (attributes && attributes.length > 0) {
      let ids = attributes.map(e => e.split(":")[0]);
      return this.attrSvc.readEntityByQuery(0, ids.length, 'id:' + ids.join('.'), undefined, undefined, { loading: false }).pipe(map(next => attributes.map(e => next.data.find(ee => ee.id === e.split(":")[0]).name + ":" + e.split(":")[1])))
    } else {
      return of([])
    }
  }
  toggleProductStatus(row: IProductSimple) {
    const dialogRef = this.dialog.open(OperationConfirmDialogComponent);
    let next: 'AVAILABLE' | 'UNAVAILABLE';
    if (this.isAvaliable(row)) {
      next = 'UNAVAILABLE'
    } else {
      next = 'AVAILABLE'
    }
    dialogRef.afterClosed().pipe(filter(result => result)).subscribe(() => this.entitySvc.updateProdStatus(row.id, next, UUID()));
  }
  isAvaliable(row: IProductSimple) {
    if (isNullOrUndefined(row.startAt))
      return false;
    let current = new Date()
    if (current.valueOf() >= row.startAt) {
      if (isNullOrUndefined(row.endAt)) {
        return true;
      }
      if (current.valueOf() < row.endAt) {
        return true;
      } else {
        return false;
      }
    } else {
      return false
    }

  }
  doBatchOffline() {
    const dialogRef = this.dialog.open(OperationConfirmDialogComponent);
    let ids = this.selection.selected.map(e => e.id)
    dialogRef.afterClosed().pipe(filter(result => result)).subscribe(() => this.entitySvc.batchUpdateProdStatus(ids, 'UNAVAILABLE', UUID()));

  }
  doBatchOnline() {
    const dialogRef = this.dialog.open(OperationConfirmDialogComponent);
    let ids = this.selection.selected.map(e => e.id)
    dialogRef.afterClosed().pipe(filter(result => result)).subscribe(() => this.entitySvc.batchUpdateProdStatus(ids, 'AVAILABLE', UUID()));
  }
}
