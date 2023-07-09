import { Component, OnDestroy } from '@angular/core';
import { MatBottomSheet } from '@angular/material/bottom-sheet';
import { FormInfoService } from 'mt-form-builder';
import { IIdBasedEntity, SummaryEntityComponent } from 'src/app/clazz/summary.component';
import { DeviceService } from 'src/app/services/device.service';
import { MySubRequestService } from 'src/app/services/my-sub-request.service';
import { ISubRequest, SubRequestComponent } from '../sub-request/sub-request.component';
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
  selector: 'app-my-sub-req',
  templateUrl: './my-sub-req.component.html',
  styleUrls: ['./my-sub-req.component.css']
})
export class MySubReqComponent extends SummaryEntityComponent<IMySubReq, IMySubReq> implements OnDestroy {
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
  sheetComponent = SubRequestComponent;
  constructor(
    public entitySvc: MySubRequestService,
    public deviceSvc: DeviceService,
    public bottomSheet: MatBottomSheet,
    public fis: FormInfoService,
  ) {
    super(entitySvc, deviceSvc, bottomSheet, fis, 0);
    //manually handle search since no search component is here
    this.doSearch({ value: 'type:my_request', key: 'type', resetPage: false });
    const sub = this.deviceSvc.refreshSummary.subscribe(() => {
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