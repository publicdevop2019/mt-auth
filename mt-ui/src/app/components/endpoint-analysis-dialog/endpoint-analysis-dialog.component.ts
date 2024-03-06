import { Component, Inject, OnInit } from '@angular/core';
import { FormControl } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { HttpProxyService } from 'src/app/services/http-proxy.service';
import { RouterWrapperService } from 'src/app/services/router-wrapper';
interface DialogData {
  endpointId: string;
  projectId: string;
}
export interface IAnalysisResult {
  authenticationRequiredRequestCount: string;
  averageResponseSize: string;
  averageRoundTimeInMili: string;
  badRequestCount: string;
  endpointId: string;
  failureResponseRate: string;
  internalServerErrorCount: string;
  serviceUnavailableErrorCount: string;
  totalInvokeCount: string;
  unauthorizedRequestCount: string;
  notModifiedRequestCount: string;
}

@Component({
  selector: 'app-endpoint-analysis-dialog',
  templateUrl: './endpoint-analysis-dialog.component.html',
  styleUrls: ['./endpoint-analysis-dialog.component.css']
})
export class EndpointAnalysisComponent implements OnInit {
  public ctrl = new FormControl();
  displayedColumns: string[] = ['name', 'value'];
  dataSource = [
    { key: 'TOTAL_INVOKE_COUNT' },
    { key: 'AVG_RESP_SIZE' },
    { key: 'AVG_RT' },
    { key: 'FAILURE_RESP_COUNT' },
    { key: 'INTER_SVR_COUNT' },
    { key: 'SVC_UNAVAILABLE_COUNT' },
    { key: 'BAD_REQ_COUNT' },
    { key: 'AUTH_REQUIRED_REQ_COUNT' },
    { key: 'UNAUTHORIZED_COUNT' },
    { key: 'NOT_MODIFIED_COUNT' },
  ];
  constructor(
    public dialogRef: MatDialogRef<EndpointAnalysisComponent>, 
    @Inject(MAT_DIALOG_DATA) public data: DialogData, 
    public httpProxySvc: HttpProxyService,
    public route: RouterWrapperService,
    ) {
  }
  result: IAnalysisResult;
  public projectId = this.route.getProjectIdFromUrl()
  ngOnInit(): void {
    this.ctrl.valueChanges.subscribe(next => {
      this.result = undefined;
      this.httpProxySvc.viewEndpointReport(this.projectId,this.data.endpointId,next).subscribe(next => { this.result = next; })
    })
  }
  viewReport(id: string,type:string) {
    return this.httpProxySvc.viewEndpointReport(this.projectId,id,type)
  }
  onDismiss(): void {
    this.dialogRef.close();
  }

  getValue(key: string) {
    if (this.result) {

      if (key === 'AUTH_REQUIRED_REQ_COUNT')
        return this.result.authenticationRequiredRequestCount
      if (key === 'AVG_RESP_SIZE')
        return (this.result.averageResponseSize || 0)
      if (key === 'AVG_RT')
        return (this.result.averageRoundTimeInMili || 0) + 'ms'
      if (key === 'BAD_REQ_COUNT')
        return this.result.badRequestCount
      if (key === 'FAILURE_RESP_COUNT')
        return this.result.failureResponseRate + '%'
      if (key === 'INTER_SVR_COUNT')
        return this.result.internalServerErrorCount
      if (key === 'SVC_UNAVAILABLE_COUNT')
        return this.result.serviceUnavailableErrorCount
      if (key === 'TOTAL_INVOKE_COUNT')
        return this.result.totalInvokeCount
      if (key === 'UNAUTHORIZED_COUNT')
        return this.result.unauthorizedRequestCount
      if (key === 'NOT_MODIFIED_COUNT')
        return this.result.notModifiedRequestCount
    } else {
      return 'N/A'
    }
  }

}
