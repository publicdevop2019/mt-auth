import { Component } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { ILoginHistory, IAuthUser } from 'src/app/misc/interface';
import { RouterWrapperService } from 'src/app/services/router-wrapper';
import { FormControl, FormGroup } from '@angular/forms';
import { HttpProxyService } from 'src/app/services/http-proxy.service';
import { RESOURCE_NAME } from 'src/app/misc/constant';
import { Utility } from 'src/app/misc/utility';
@Component({
  selector: 'app-user',
  templateUrl: './mgmt-user.component.html',
  styleUrls: ['./mgmt-user.component.css']
})
export class MgmtUserComponent {
  private url = Utility.getMgmtResource(RESOURCE_NAME.MGMT_USERS)
  columnList = {
    loginAt: 'LOGIN_AT',
    ipAddress: 'IP_ADDRESS',
    agent: 'AGENT',
  }
  dataSource: MatTableDataSource<ILoginHistory>;
  data: IAuthUser;
  public formGroup: FormGroup = new FormGroup({
    id: new FormControl({ value: '', disabled: true }),
    email: new FormControl({ value: '', disabled: true }),
    createdAt: new FormControl({ value: '', disabled: true }),
    locked: new FormControl({ value: '', disabled: true }),
  })
  constructor(
    public router: RouterWrapperService,
    public httpProxySvc: HttpProxyService,
  ) {
    const endpointId = this.router.getMgmtEndpointIdFromUrl();
    this.httpProxySvc.readEntityById<IAuthUser>(this.url, endpointId).subscribe(next => {
      this.formGroup.get('id').setValue(next.id)
      this.formGroup.get('email').setValue(next.email)
      this.formGroup.get('locked').setValue(next.locked)
      this.formGroup.get('createdAt').setValue(new Date(next.createdAt))
      this.dataSource = new MatTableDataSource(next.loginHistory);
    })
  }
  displayedColumns(): string[] {
    return Object.keys(this.columnList)
  }
}
