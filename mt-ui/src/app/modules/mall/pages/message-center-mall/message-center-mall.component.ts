import { Overlay, OverlayConfig } from '@angular/cdk/overlay';
import { ComponentPortal } from '@angular/cdk/portal';
import { Component } from '@angular/core';
import { MatBottomSheet } from '@angular/material/bottom-sheet';
import { MatIcon } from '@angular/material/icon';
import { TranslateService } from '@ngx-translate/core';
import { FormInfoService } from 'mt-form-builder';
import { SummaryEntityComponent } from 'src/app/clazz/summary.component';
import { ObjectDetailComponent } from 'src/app/components/object-detail/object-detail.component';
import { DeviceService } from 'src/app/services/device.service';
import { IMallMonitorMsg, MessageMallService } from 'src/app/services/message-mall.service';
import { OverlayService } from 'src/app/services/overlay.service';
import { IBizTask } from 'src/app/services/task.service';

@Component({
  selector: 'app-message-center-mall',
  templateUrl: './message-center-mall.component.html',
  styleUrls: ['./message-center-mall.component.css']
})
export class MessageCenterMallComponent extends SummaryEntityComponent<IMallMonitorMsg, IMallMonitorMsg>{
  public formId = "mallMsgTableColumnConfig";
  columnList = {
    date: 'DATE',
    orderId: 'REFERENCE_ID',
    name: 'NAME',
    detail: 'DETAIL',
  }
  constructor(
    public entitySvc: MessageMallService,
    public deviceSvc: DeviceService,
    public bottomSheet: MatBottomSheet,
    private overlay: Overlay,
    private overlaySvc: OverlayService,
    public fis: FormInfoService,
  ) {
    super(entitySvc, deviceSvc, bottomSheet,fis, -2);
    super.doSearch({value:'',resetPage:false})
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
  refreash(){
    super.doSearch({value:'',resetPage:false})
  }
}
