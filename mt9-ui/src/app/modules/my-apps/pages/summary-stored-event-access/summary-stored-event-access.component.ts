import { Overlay, OverlayConfig } from '@angular/cdk/overlay';
import { ComponentPortal } from '@angular/cdk/portal';
import { Component, OnDestroy } from '@angular/core';
import { MatBottomSheet } from '@angular/material/bottom-sheet';
import { MatDialog } from '@angular/material/dialog';
import { MatIcon } from '@angular/material/icon';
import { FormInfoService } from 'mt-form-builder';
import { IForm } from 'mt-form-builder/lib/classes/template.interface';
import { Observable } from 'rxjs';
import { filter } from 'rxjs/operators';
import { SummaryEntityComponent } from 'src/app/clazz/summary.component';
import { ObjectDetailComponent } from 'src/app/components/object-detail/object-detail.component';
import { ISearchConfig } from 'src/app/components/search/search.component';
import { FORM_CONFIG } from 'src/app/form-configs/stored-event.config';
import { DeviceService } from 'src/app/services/device.service';
import { OverlayService } from 'src/app/services/overlay.service';
import { IStoredEvent, StoredEventService } from 'src/app/services/stored-event.service';
import { StoredEventAccessService } from 'src/app/services/stored-event.service-access';
import { IBizTask } from 'src/app/services/task.service';
@Component({
  selector: 'app-summary-stored-event-access',
  templateUrl: './summary-stored-event-access.component.html',
  styleUrls: ['./summary-stored-event-access.component.css']
})
export class SummaryStoredEventAccessComponent extends SummaryEntityComponent<IStoredEvent, IStoredEvent> implements OnDestroy {
  displayedColumns: string[] = ['id', 'eventBody', 'timestamp', 'domainId', 'name', 'internal','retry'];
  searchConfigs: ISearchConfig[] = [
    {
      searchLabel: 'ID',
      searchValue: 'id',
      type: 'text',
      multiple: {
        delimiter:'.'
      }
    },
    {
      searchLabel: 'REFERENCE_ID',
      searchValue: 'domainId',
      type: 'text',
      multiple: {
        delimiter:'.'
      }
    }
  ]
  constructor(
    public entitySvc: StoredEventAccessService,
    public deviceSvc: DeviceService,
    public bottomSheet: MatBottomSheet,
    public dialog: MatDialog,
    private overlay: Overlay,
    private overlaySvc: OverlayService,
    private fis: FormInfoService,
    ) {
      super(entitySvc, deviceSvc, bottomSheet, 1);
  }
  launchOverlay(el: MatIcon, data: IBizTask) {
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
  doRetry(id:string){
    this.entitySvc.retry(id).subscribe()
  }
}
