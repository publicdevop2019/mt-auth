import { Overlay, OverlayConfig } from '@angular/cdk/overlay';
import { ComponentPortal } from '@angular/cdk/portal';
import { Component } from '@angular/core';
import { FormControl } from '@angular/forms';
import { MatIcon } from '@angular/material/icon';
import { ActivatedRoute } from '@angular/router';
import { TableHelper } from 'src/app/clazz/table-helper';
import { ObjectDetailComponent } from 'src/app/components/object-detail/object-detail.component';
import { ISearchConfig, ISearchEvent } from 'src/app/components/search/search.component';
import { RESOURCE_NAME } from 'src/app/misc/constant';
import { Logger } from 'src/app/misc/logger';
import { Utility } from 'src/app/misc/utility';
import { DeviceService } from 'src/app/services/device.service';
import { HttpProxyService } from 'src/app/services/http-proxy.service';
import { RouterWrapperService } from 'src/app/services/router-wrapper';
export interface IStoredEvent {
  id: string,
  eventBody: string,
  timestamp: number,
  name: string,
  domainId: string,
  internal: string,
  topic: string,
  version: number
}
@Component({
  selector: 'app-summary-stored-event-access',
  templateUrl: './summary-stored-event-access.component.html',
  styleUrls: []
})
export class SummaryStoredEventAccessComponent {
  columnList = {
    id: 'ID',
    eventBody: 'DETAILS',
    timestamp: 'CREATE_AT',
    domainId: 'REFERENCE_ID',
    name: 'NAME',
    internal: 'INTERNAL',
    retry: 'RETRY',
  }
  public filter = new FormControl('all')
  private url = Utility.getMgmtResource(RESOURCE_NAME.MGMT_EVENTS)
  private urlDefault = Utility.getMgmtResource(RESOURCE_NAME.MGMT_EVENTS)
  private urlAudit = Utility.getMgmtResource(RESOURCE_NAME.MGMT_EVENTS_AUDIT)
  public tableSource: TableHelper<IStoredEvent> = new TableHelper(this.columnList, 10, this.httpSvc, this.url);
  searchConfigs: ISearchConfig[] = [
    {
      searchLabel: 'ID',
      searchValue: 'id',
      type: 'text',
      multiple: {
        delimiter: '.'
      }
    },
    {
      searchLabel: 'REFERENCE_ID',
      searchValue: 'domainId',
      type: 'text',
      multiple: {
        delimiter: '.'
      }
    }
  ];
  constructor(
    public activated: ActivatedRoute,
    public router: RouterWrapperService,
    public device: DeviceService,
    private overlay: Overlay,
    private httpSvc: HttpProxyService,
  ) {
    Logger.trace(this.filter.value)
    this.filter.valueChanges.subscribe(next => {
      if (next === 'audit') {
        this.tableSource = new TableHelper(this.tableSource.columnConfig, this.tableSource.pageSize, this.httpSvc, this.urlAudit);
      } else if (next === 'rejected') {
        this.tableSource = new TableHelper(this.tableSource.columnConfig, this.tableSource.pageSize, this.httpSvc, this.urlDefault, 'rejected:1');
      } else if (next === 'unroutable') {
        this.tableSource = new TableHelper(this.tableSource.columnConfig, this.tableSource.pageSize, this.httpSvc, this.urlDefault, 'routable:0');
      } else {
        this.tableSource = new TableHelper(this.tableSource.columnConfig, this.tableSource.pageSize, this.httpSvc, this.urlDefault);
      }
      this.tableSource.loadPage(0)
    })
  }
  launchOverlay(el: MatIcon, data: IStoredEvent) {
    this.device.overlayData = data;
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
  doRetry(id: string) {
    this.httpSvc.retry(this.urlDefault, id)
  }
  doSearch(config: ISearchEvent) {
    this.tableSource = new TableHelper(this.tableSource.columnConfig, this.tableSource.pageSize, this.httpSvc, this.tableSource.url, config.value);
    this.tableSource.loadPage(0)
  }
}
