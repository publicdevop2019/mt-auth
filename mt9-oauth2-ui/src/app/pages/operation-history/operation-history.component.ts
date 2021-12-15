import { Overlay, OverlayConfig } from '@angular/cdk/overlay';
import { ComponentPortal } from '@angular/cdk/portal';
import { Component, OnDestroy } from '@angular/core';
import { MatBottomSheet } from '@angular/material/bottom-sheet';
import { MatIcon } from '@angular/material/icon';
import { PageEvent } from '@angular/material/paginator';
import { ActivatedRoute } from '@angular/router';
import { SummaryEntityComponent } from 'src/app/clazz/summary.component';
import { ObjectDetailComponent } from 'src/app/components/object-detail/object-detail.component';
import { ISearchConfig, ISearchEvent } from 'src/app/components/search/search.component';
import { DeviceService } from 'src/app/services/device.service';
import { IChangeRecord, OperationHistoryService } from 'src/app/services/operation-history.service';
import { OverlayService } from 'src/app/services/overlay.service';
import { environment } from 'src/environments/environment';

@Component({
  selector: 'app-operation-history',
  templateUrl: './operation-history.component.html',
  styleUrls: ['./operation-history.component.css']
})
export class OperationHistoryComponent extends SummaryEntityComponent<IChangeRecord, IChangeRecord> implements OnDestroy {
  displayedColumns: string[] = ['changeId', 'entityType'];
  // sheetComponent = ClientComponent;
  label: string;
  queryPrefix: string;
  searchConfigs: ISearchConfig[] = [
    {
      searchLabel: 'CHANGE_ID',
      searchValue: 'changeId',
      type: 'text',
      multiple: {
        delimiter:'.'
      }
    },
  ]
  constructor(
    public entitySvc: OperationHistoryService,
    public deviceSvc: DeviceService,
    public bottomSheet: MatBottomSheet,
    private route: ActivatedRoute,
    private overlay: Overlay,
    private overlaySvc: OverlayService,
  ) {
    super(entitySvc, deviceSvc, bottomSheet, 2, true);
    let ob = this.route.paramMap.subscribe(queryMaps => {
      this.entitySvc.pageNumber = 0;//reset var
      this.queryString = undefined;
      if (queryMaps.get('type') === 'auth') {
        this.entitySvc.PRODUCT_SVC_NAME = '/auth-svc';
        if (queryMaps.get('entity') === 'client') {
          this.queryPrefix = 'entityType:Client'
          this.label = 'OPERATION_DASHBOARD_CLIENT'
        }
        if (queryMaps.get('entity') === 'user') {
          this.queryPrefix = 'entityType:User'
          this.label = 'OPERATION_DASHBOARD_USER'
        }
        if (queryMaps.get('entity') === 'endpoint') {
          this.label = 'OPERATION_DASHBOARD_EP'
          this.queryPrefix = 'entityType:BizEndpoint'
        }
      } else if (queryMaps.get('type') === 'proxy') {
        this.entitySvc.PRODUCT_SVC_NAME = '/proxy';
        if (queryMaps.get('entity') === 'token') {
          this.label = 'OPERATION_DASHBOARD_TOKEN'
          this.queryPrefix = 'entityType:RevokeToken'
        }
      } else if (queryMaps.get('type') === 'mall') {
        this.entitySvc.PRODUCT_SVC_NAME = '/product-svc';
        if (queryMaps.get('entity') === 'sku') {
          this.label = 'OPERATION_DASHBOARD_SKU'
          this.queryPrefix = 'entityType:Sku'
        }
        if (queryMaps.get('entity') === 'product') {
          this.label = 'OPERATION_DASHBOARD_PRODUCT'
          this.queryPrefix = 'entityType:Product'
        }
        if (queryMaps.get('entity') === 'catalog') {
          this.label = 'OPERATION_DASHBOARD_CATALOG'
          this.queryPrefix = 'entityType:BizCatalog'
        }
        if (queryMaps.get('entity') === 'attribute') {
          this.label = 'OPERATION_DASHBOARD_ATTR'
          this.queryPrefix = 'entityType:BizAttribute'
        }
        if (queryMaps.get('entity') === 'filter') {
          this.label = 'OPERATION_DASHBOARD_FILTER'
          this.queryPrefix = 'entityType:BizFilter'
        }
      } else if (queryMaps.get('type') === 'profile') {
        this.entitySvc.PRODUCT_SVC_NAME = '/profile-svc';
        if (queryMaps.get('entity') === 'order') {
          this.label = 'OPERATION_DASHBOARD_ORDER'
          this.queryPrefix = 'entityType:BizOrder'
        }
      } else {

      }
      this.entitySvc.entityRepo = environment.serverUri + this.entitySvc.PRODUCT_SVC_NAME + this.entitySvc.ENTITY_NAME;
      this.deviceSvc.refreshSummary.next();
    });
  }
  doSearch(config: ISearchEvent) {
    if(this.queryPrefix){
      this.queryString = this.queryPrefix + (config.value ? (","+config.value ): '');
      super.doSearch({value:this.queryString,resetPage:config.resetPage});
    }
  }
  pageHandler(e: PageEvent) {
    if (this.queryString && this.queryString.includes('entityType')) {
    } else {
      this.queryString = this.queryPrefix + (this.queryString ? this.queryString : '');
    }
    super.pageHandler(e);
  }
  launchOverlay(el: MatIcon, data: any) {
    this.overlaySvc.data = data;
    let config = new OverlayConfig();
    config.hasBackdrop = true;
    config.positionStrategy = this.overlay.position().global().centerVertically().centerHorizontally();
    config.scrollStrategy = this.overlay.scrollStrategies.reposition();
    const overlayRef = this.overlay.create(config);
    const filePreviewPortal = new ComponentPortal(ObjectDetailComponent);
    overlayRef.attach(filePreviewPortal);
    overlayRef.backdropClick().subscribe(() => {
      overlayRef.dispose();
    })

  }
}