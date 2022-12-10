import { Component, OnDestroy } from '@angular/core';
import { MatBottomSheet } from '@angular/material/bottom-sheet';
import { TranslateService } from '@ngx-translate/core';
import { FormInfoService } from 'mt-form-builder';
import { map, switchMap } from 'rxjs/operators';
import { SummaryEntityComponent } from 'src/app/clazz/summary.component';
import { DeviceService } from 'src/app/services/device.service';
import { IMySubscription, MySubscriptionsService } from 'src/app/services/my-subscriptions.service';
import { SubRequestComponent } from '../sub-request/sub-request.component';

@Component({
  selector: 'app-my-subs',
  templateUrl: './my-subs.component.html',
  styleUrls: ['./my-subs.component.css']
})
export class MySubsComponent extends SummaryEntityComponent<IMySubscription, IMySubscription> implements OnDestroy {
  public formId = "mySubscriptionTableColumnConfig";
  columnList = {
    id: 'ID',
    projectName: 'SUB_PROJECT_NAME',
    endpointName: 'API_NAME',
    replenishRate: 'REPLENISH_RATE',
    burstCapacity: 'BURST_CAPACITY',
    endpointStatus: 'ENDPOINT_STATUS',
  }
  sheetComponent = SubRequestComponent;
  constructor(
    public entitySvc: MySubscriptionsService,
    public deviceSvc: DeviceService,
    public bottomSheet: MatBottomSheet,
    public fis: FormInfoService,
    public ts: TranslateService,
  ) {
    super(entitySvc, deviceSvc, bottomSheet, fis, 0);
    //manually handle search since no search component is here
    this.doSearch({ value: '', key: '', resetPage: false });
    const sub = this.deviceSvc.refreshSummary.subscribe(() => {
      this.doSearch({ value: '', key: '', resetPage: false });
    })
    this.subs.add(sub)
  }
  getReason(reason:string){
    return this.ts.get('STATUS_EXPIRED').pipe(map(e=>{
      return e+reason
    }))
  }
}