import { Component, OnDestroy } from '@angular/core';
import { MatBottomSheet } from '@angular/material/bottom-sheet';
import { ActivatedRoute } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { FormInfoService } from 'mt-form-builder';
import { map } from 'rxjs/operators';
import { SummaryEntityComponent } from 'src/app/clazz/summary.component';
import { DeviceService } from 'src/app/services/device.service';
import { IMySubscription, MySubscriptionsService } from 'src/app/services/my-subscriptions.service';
import { RouterWrapperService } from 'src/app/services/router-wrapper';

@Component({
  selector: 'app-my-subscriptions',
  templateUrl: './my-subscriptions.component.html',
  styleUrls: ['./my-subscriptions.component.css']
})
export class MySubscriptionsComponent extends SummaryEntityComponent<IMySubscription, IMySubscription> implements OnDestroy {
  public formId = "mySubscriptionTableColumnConfig";
  columnList = {
    id: 'ID',
    projectName: 'SUB_PROJECT_NAME',
    endpointName: 'API_NAME',
    replenishRate: 'REPLENISH_RATE',
    burstCapacity: 'BURST_CAPACITY',
    endpointStatus: 'ENDPOINT_STATUS',
  }
  constructor(
    public entitySvc: MySubscriptionsService,
    public activated: ActivatedRoute,
    public router: RouterWrapperService,
    public device: DeviceService,
    public bottomSheet: MatBottomSheet,
    public fis: FormInfoService,
    public ts: TranslateService,
  ) {
    super(entitySvc, activated,router, bottomSheet, fis, 0);
    //manually handle search since no search component is here
    this.doSearch({ value: '', key: '', resetPage: false });
    const sub = this.device.refreshSummary.subscribe(() => {
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