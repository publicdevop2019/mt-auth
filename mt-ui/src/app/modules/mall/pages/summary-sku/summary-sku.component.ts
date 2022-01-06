import { Component, OnDestroy } from '@angular/core';
import { MatBottomSheet } from '@angular/material/bottom-sheet';
import { MatDialog } from '@angular/material/dialog';
import { PageEvent } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';
import { FormInfoService } from 'mt-form-builder';
import { switchMap } from 'rxjs/internal/operators/switchMap';
import { ISumRep, SummaryEntityComponent } from 'src/app/clazz/summary.component';
import { ISkuNew, IProductSimple } from 'src/app/clazz/validation/aggregate/product/interfaze-product';
import { ISearchConfig } from 'src/app/components/search/search.component';
import { AttributeService } from 'src/app/services/attribute.service';
import { DeviceService } from 'src/app/services/device.service';
import { ProductService } from 'src/app/services/product.service';
import { SkuService } from 'src/app/services/sku.service';
@Component({
  selector: 'app-summary-sku',
  templateUrl: './summary-sku.component.html',
  styleUrls: ['./summary-sku.component.css']
})
export class SummarySkuComponent extends SummaryEntityComponent<ISkuNew, ISkuNew> implements OnDestroy {
  public formId = "mallSkuTableColumnConfig";
  columnList = {
    id: 'ID',
    coverImage: 'COVER_IMAGE',
    referenceId: 'REFERENCE_ID',
    salesAttr: 'SALES_ATTR',
    description: 'DESCRIPTION',
    storageOrder: 'STORAGE_ORDER',
    storageActual: 'STORAGE_ACTUAL',
    price: 'PRICE',
    sales: 'SALES',
    delete: 'DELETE',
  }
  productRef: ISumRep<IProductSimple>
  searchConfigs: ISearchConfig[] = [
    {
      searchLabel: 'ID',
      searchValue: 'id',
      type: 'text',
      multiple: {
        delimiter:'.'
      }
    }
  ]
  constructor(
    public entitySvc: SkuService,
    private productSvc: ProductService,
    private attrSvc: AttributeService,
    public deviceSvc: DeviceService,
    public bottomSheet: MatBottomSheet,
    public dialog: MatDialog,
    public fis: FormInfoService,
  ) {
    super(entitySvc, deviceSvc, bottomSheet,fis, 7);
  }
  updateSummaryData(next: ISumRep<ISkuNew>) {
    super.updateSummaryData(next);
    this.loadProduct(next)
  }
  public parsedRef: { [key: number]: IProductSimple } = {};
  public parsedRefAttr: { [key: number]: string } = {};
  private loadProduct(input: ISumRep<ISkuNew>) {
    let parsedRef: { [key: number]: IProductSimple } = {};
    let parsedRefAttr: { [key: number]: string } = {};
    let ids = input.data.map(e => e.referenceId);
    if (ids.length > 0) {
      let var0 = new Set(ids);
      let var1 = new Array(...var0);
      this.productSvc.readEntityByQuery(0, var1.length, "id:" + var1.join('.')).subscribe(next => {
        this.productRef = next;
        this.dataSource.data.forEach(e => {
          parsedRef[e.id] = this.parseRef(e.referenceId)
          parsedRefAttr[e.id] = this.parseSalesAttr(e.referenceId, e.id)
        });
        let reqAttrIds: string[] = []
        Object.values(parsedRefAttr).forEach(e => {
          if(e){
            e.split(',').forEach(ee => {
              reqAttrIds.push(ee.split(':')[0]);
            })
          }
        })
        let var2 = new Set(reqAttrIds);
        let var3 = new Array(...var2);
        if (var3.filter(e => e).length > 0) {
          this.attrSvc.readEntityByQuery(0, var3.length, "id:" + var3.filter(e => e).join('.')).subscribe(next2 => {
            Object.keys(parsedRefAttr).forEach(e => {
              let attr = parsedRefAttr[+e];
              let parsed = attr.split(',').map(ee => {
                if (ee) {
                  let attrId = ee.split(':')[0];
                  return next2.data.find(eee => eee.id === attrId).name + ":" + ee.split(':')[1];
                }
              }).join(',')
              parsedRefAttr[+e] = parsed;
            })
          })
        }
        this.parsedRef = parsedRef;
        this.parsedRefAttr = parsedRefAttr;
      })
    }
  }
  private parseRef(id: string): IProductSimple {
    return this.productRef && (this.productRef.data.filter(e => e.id === id)[0] ? this.productRef.data.filter(e => e.id === id)[0] : undefined)
  }
  private parseSalesAttr(refId: string, id: string) {
    if (this.productRef && this.productRef.data.filter(e => e.id === refId)[0]) {
      let map = this.productRef.data.filter(e => e.id === refId)[0].attrSalesMap
      if(map){
        let output: string = '';
        Object.keys(map).forEach(key => {
          if (map[key] === id) {
            output = key;
            return key
          }
        })
        return output;
      }
    } else {
      return ''
    }
  }
}
