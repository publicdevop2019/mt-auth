import { Component, OnDestroy } from '@angular/core';
import { MatBottomSheet } from '@angular/material/bottom-sheet';
import { FormInfoService } from 'mt-form-builder';
import { IIdBasedEntity, SummaryEntityComponent } from 'src/app/clazz/summary.component';
import { DeviceService } from 'src/app/services/device.service';
import { MySubRequestService } from 'src/app/services/my-sub-request.service';
import { ISubRequest, SubscribeRequestComponent } from '../subscribe-request/subscribe-request.component';
import { ActivatedRoute } from '@angular/router';
import { RouterWrapperService } from 'src/app/services/router-wrapper';
export interface IMySubReq extends ISubRequest {
  endpointName: string,
  projectName: string,
  rejectionReason: string,
  status: string,
  approvedBy: string,
  createdBy: string,
  updateAt: string,
  createAt: string,
  endpointProjectId: string,
}
@Component({
  selector: 'app-my-requests',
  templateUrl: './my-requests.component.html',
  styleUrls: ['./my-requests.component.css']
})
export class MyRequestsComponent extends SummaryEntityComponent<IMySubReq, IMySubReq> implements OnDestroy {
  public formId = "mySubReqTableColumnConfig";
  columnList = {
    id: 'ID',
    projectName: 'SUB_PROJECT_NAME',
    endpointName: 'API_NAME',
    status: 'STATUS',
    rejectionReason: 'REJECTION_REASON',
    update: 'UPDATE',
    cancel: 'CANCEL',
  }
  sheetComponent = SubscribeRequestComponent;
  constructor(
    public entitySvc: MySubRequestService,
    public activated: ActivatedRoute,
    public router: RouterWrapperService,
    public device: DeviceService,
    public bottomSheet: MatBottomSheet,
    public fis: FormInfoService,
  ) {
    super(entitySvc, activated,router, bottomSheet, fis, 0);
    //manually handle search since no search component is here
    this.doSearch({ value: 'type:my_request', key: 'type', resetPage: false });
    const sub = this.device.refreshSummary.subscribe(() => {
      this.doSearch({ value: 'type:my_request', key: 'type', resetPage: false })
    })
    this.subs.add(sub)
  }
  cancel(id: string) {
    this.entitySvc.cancelSubRequest(id).subscribe(() => {
      this.entitySvc.notify(true)
      this.doSearch({ value: 'type:my_request', key: 'type', resetPage: false })
    }, () => {
      this.entitySvc.notify(false)
    })
  }
}