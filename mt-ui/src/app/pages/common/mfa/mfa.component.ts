import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { IMfaResponse, ITokenResponse } from 'src/app/misc/interface';
import { Utility } from 'src/app/misc/utility';
import { AuthService } from 'src/app/services/auth.service';
import { HttpProxyService } from 'src/app/services/http-proxy.service';
import { RouterWrapperService } from 'src/app/services/router-wrapper';

@Component({
  selector: 'app-mfa',
  templateUrl: './mfa.component.html',
  styleUrls: ['./mfa.component.css']
})
export class MfaComponent implements OnInit {
  mfaForm = new FormGroup({
    mfaCode: new FormControl('', []),
  });
  mfaCodeErrorMsg: string
  changeId: string = Utility.getChangeId();
  constructor(
    public httpProxy: HttpProxyService,
    public authSvc: AuthService,
    private router: ActivatedRoute,
    private route: RouterWrapperService
  ) {
    if (!authSvc.mfaResponse) {
      this.route.navLogin({ queryParams: this.router.snapshot.queryParams });
    }
  }

  ngOnInit(): void {

  }
  confirm() {
    if (!this.mfaForm.get('mfaCode').value) {
      this.mfaCodeErrorMsg = 'REQUIRED';
      this.mfaForm.get('mfaCode').setErrors({ wrongValue: true });
    } else {

      if (this.authSvc.loginFormValue) {
        const code = this.mfaForm.get('mfaCode').value
        const mfaId = this.authSvc.mfaResponse.mfaId;
        const nextUrl = this.authSvc.loginNextUrl;
        let response: Observable<ITokenResponse | IMfaResponse>
        if (this.authSvc.loginFormValue.get('pwdMobileNumber').value) {
          response = this.httpProxy.mfaLoginMobilePwd(this.authSvc.loginFormValue, code, mfaId, this.changeId)
        } else {
          if ((this.authSvc.loginFormValue.get('pwdEmailOrUsername').value as string).includes("@")) {
            response = this.httpProxy.mfaLoginEmailPwd(this.authSvc.loginFormValue, code, mfaId, this.changeId)
          } else {
            response = this.httpProxy.mfaLoginUsernamePwd(this.authSvc.loginFormValue, code, mfaId, this.changeId)
          }
        }
        response.subscribe(next => {
          this.httpProxy.currentUserAuthInfo = next as ITokenResponse;
          this.authSvc.loginFormValue = undefined;
          this.authSvc.mfaResponse = undefined;
          this.route.navTo(nextUrl, { queryParams: this.router.snapshot.queryParams });
        })
      } else {
        this.route.navLogin({ queryParams: this.router.snapshot.queryParams });
      }
    }
  }
  sendByEmail() {
    this.sendByCommon('email')
  }
  sendByMobile() {
    this.sendByCommon('mobile')
  }
  private sendByCommon(value: string) {
    let response: Observable<IMfaResponse>
    if (this.authSvc.loginFormValue.get('pwdMobileNumber').value) {
      response = this.httpProxy.mfaLoginMobilePwdMfaSelect(this.authSvc.loginFormValue, value, Utility.getChangeId())
    } else {
      if ((this.authSvc.loginFormValue.get('pwdEmailOrUsername').value as string).includes("@")) {
        response = this.httpProxy.mfaLoginEmailPwdMfaSelect(this.authSvc.loginFormValue, value, Utility.getChangeId())
      } else {
        response = this.httpProxy.mfaLoginUsernamePwdMfaSelect(this.authSvc.loginFormValue, value, Utility.getChangeId())
      }
    }
    response.subscribe(next => {
      this.authSvc.mfaResponse = next;
    })
  }
}
