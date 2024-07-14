import { Component } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { Utility } from 'src/app/misc/utility';
import { Validator } from 'src/app/misc/validator';
import { MsgBoxComponent } from 'src/app/components/msg-box/msg-box.component';
import { AuthService } from 'src/app/services/auth.service';
import { HttpProxyService } from 'src/app/services/http-proxy.service';
import { LanguageService } from 'src/app/services/language.service';
import { IForgetPasswordRequest, IMfaResponse, IOption, IPendingUser, ITokenResponse } from 'src/app/misc/interface';
import { Logger } from 'src/app/misc/logger';
import { MatSnackBar } from '@angular/material/snack-bar';
import { RouterWrapperService } from 'src/app/services/router-wrapper';
import { MatTabChangeEvent } from '@angular/material/tabs';
import { Observable } from 'rxjs';
@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  hide = true;
  context: 'REGISTER' | 'LOGIN' | 'FORGET' = 'LOGIN'
  loginContext: 'EMAIL' | 'USERNAME' | 'MOBILE' = 'MOBILE';
  selectedLoginIndex = 0;
  forgetContext: 'EMAIL' | 'MOBILE' = 'MOBILE';
  nextUrl: string = '/' + RouterWrapperService.HOME_URL;
  mobileErrorMsg: string = undefined;
  mobileCodeErrorMsg: string = undefined;
  emailCodeErrorMsg: string = undefined;
  emailErrorMsg: string = undefined;
  usernameErrorMsg: string = undefined;
  pwdErrorMsg: string = undefined;

  forgetEmailErrorMsg: string = undefined;
  forgetMobileErrorMsg: string = undefined;
  forgetCodeErrorMsg: string = undefined;
  forgetPwdErrorMsg: string = undefined;

  enableError: boolean = false;
  enableForgetError: boolean = false;

  codeChangeId = Utility.getChangeId();
  registerChangeId = Utility.getChangeId();
  tokenChangeId = Utility.getChangeId();
  resetChangeId = Utility.getChangeId();
  form = new FormGroup({
    countryCode: new FormControl('86', []),
    mobileNumber: new FormControl('', []),
    mobileCode: new FormControl('', []),
    email: new FormControl('', []),
    emailCode: new FormControl('', []),
    username: new FormControl('', []),
    pwd: new FormControl('', []),
  });
  forgetForm = new FormGroup({
    countryCode: new FormControl('86', []),
    mobileNumber: new FormControl('', []),
    email: new FormControl('', []),
    token: new FormControl('', []),
    pwd: new FormControl('', []),
  });
  constructor(
    public langSvc: LanguageService,
    public httpProxy: HttpProxyService,
    private route: RouterWrapperService,
    public dialog: MatDialog,
    private router: ActivatedRoute,
    public translate: TranslateService,
    public authSvc: AuthService,
    private snackBar: MatSnackBar
  ) {
    this.httpProxy.refreshInprogress = false;
    this.router.queryParamMap.subscribe(queryMaps => {
      if (queryMaps.get('redirect_uri') !== null) {
        /** get  authorize party info */
        this.nextUrl = '/' + RouterWrapperService.AUTHORIZE_URL;
      }
      if (queryMaps.get('demo') !== null) {
        Logger.debug('demo mode')
        let demoAccount = queryMaps.get('demo')
        this.loginContext = 'USERNAME';
        this.selectedLoginIndex = 2;
        if(demoAccount==='admin'){
          this.form.get('username').setValue('admin@sample.com')
          this.form.get('pwd').setValue('Password1!')
        }else{
          this.form.get('username').setValue('tenant@sample.com')
          this.form.get('pwd').setValue('Password1!')
        }
        this.loginOrRegister()
      }
    });
    if (localStorage.getItem('home_notification') !== 'true') {

      this.translate.get("HOME_NOTIFICAIONT").subscribe(next => {
        this.snackBar.open(next, 'OK');
        this.snackBar._openedSnackBarRef.afterDismissed().subscribe(() => {
          localStorage.setItem('home_notification', 'true')
        })
      })
    }

    this.form.valueChanges.subscribe(() => {
      if (this.enableError) {
        Logger.debug('checking login')
        this.validateForm();
      }
    })
    this.forgetForm.valueChanges.subscribe(() => {
      if (this.enableForgetError) {
        Logger.debug('checking forget')
        this.validateForgetForm();
      }
    })
  }
  mobileNums: IOption[] = [
    {
      label: 'COUNTRY_CANADA', value: '1'
    },
    {
      label: 'COUNTRY_CHINA', value: '86'
    },
  ]
  loginOrRegister() {
    this.enableError = true;
    if (this.validateForm()) {
      let response: Observable<ITokenResponse | IMfaResponse>;
      if (this.loginContext === 'EMAIL') {
        response = this.httpProxy.loginEmail(this.form.get('email').value, this.form.get('code').value)
      } else if (this.loginContext === 'MOBILE') {
        response = this.httpProxy.loginMobile(this.form.get('mobileNum').value, this.form.get('countryCode').value, this.form.get('code').value)
      } else if (this.loginContext === 'USERNAME') {
        response = this.httpProxy.loginUsername(this.form.get('username').value, this.form.get('pwd').value)
      }
      response.subscribe(next => {
        if ((next as IMfaResponse).mfaId) {
          this.authSvc.loginFormValue = this.form;
          this.authSvc.loginNextUrl = this.nextUrl;
          this.authSvc.mfaId = (next as IMfaResponse).mfaId;
          this.route.navMfa({ queryParams: this.router.snapshot.queryParams });
        } else {
          this.httpProxy.currentUserAuthInfo = next as ITokenResponse;
          this.route.navTo(this.nextUrl, { queryParams: this.router.snapshot.queryParams });
        }
      })
    }
  }

  getCode() {
    let hasEmailOrMobile = false;
    if (this.loginContext === 'EMAIL') {
      hasEmailOrMobile = this.validateEmail()
    } else if (this.loginContext === 'MOBILE') {
      hasEmailOrMobile = this.validateMobile()
    }
    if (hasEmailOrMobile) {
      this.httpProxy.currentUserAuthInfo = undefined;
      let payload: IPendingUser
      if (this.loginContext === 'EMAIL') {
        payload = { email: this.form.get('email').value }
      } else {
        payload = { mobileNumber: this.form.get('mobileNumber').value, countryCode: this.form.get('countryCode').value }
      }
      this.httpProxy.activate(payload, this.codeChangeId).subscribe(next => {
        this.openDialog('CODE_SEND_MSG');
      })
    }
  }
  getToken() {
    let hasEmailOrMobile = false;
    if (this.forgetContext === 'EMAIL') {
      hasEmailOrMobile = this.validateForgetEmail()
    } else if (this.forgetContext === 'MOBILE') {
      hasEmailOrMobile = this.validateForgetMobile()
    }
    if (hasEmailOrMobile) {
      this.httpProxy.currentUserAuthInfo = undefined;
      let payload: IForgetPasswordRequest
      if (this.loginContext === 'EMAIL') {
        payload = { email: this.forgetForm.get('email').value }
      } else {
        payload = { mobileNumber: this.forgetForm.get('mobileNumber').value, countryCode: this.forgetForm.get('countryCode').value }
      }
      this.httpProxy.currentUserAuthInfo = undefined;
      this.httpProxy.forgetPwd(payload, this.tokenChangeId).subscribe(next => {
        this.openDialog('TOKEN_SEND_MSG');
      })
    }
  }
  changePassword() {
    this.enableForgetError = true;
    if (this.validateForget()) {
      let payload: IForgetPasswordRequest;
      if (this.forgetContext === 'EMAIL') {
        payload = {
          email: this.forgetForm.get('email').value,
          token: this.forgetForm.get('token').value,
          newPassword: this.forgetForm.get('pwd').value,
        };
      } else {
        payload = {
          mobileNumber: this.forgetForm.get('mobileNumber').value,
          countryCode: this.forgetForm.get('countryCode').value,
          token: this.forgetForm.get('token').value,
          newPassword: this.forgetForm.get('pwd').value,
        };
      }
      this.httpProxy.resetPwd(payload, this.resetChangeId).subscribe(next => {
        this.context = 'LOGIN'
        this.openDialog('PASSWORD_UPDATE_SUCCESS_MSG');
      })
    }
  }
  private openDialog(msg: string): void {
    this.dialog.open(MsgBoxComponent, {
      width: '250px',
      data: msg
    });
  }
  handleTabChange(tabChangeEvent: MatTabChangeEvent) {
    if (tabChangeEvent.index === 0) {
      this.loginContext = 'MOBILE'
    } else if (tabChangeEvent.index === 1) {
      this.loginContext = 'EMAIL'
    } else if (tabChangeEvent.index === 2) {
      this.loginContext = 'USERNAME'
    }
  }
  handleForgetTabChange(tabChangeEvent: MatTabChangeEvent) {
    if (tabChangeEvent.index === 0) {
      this.forgetContext = 'MOBILE'
    } else if (tabChangeEvent.index === 1) {
      this.forgetContext = 'EMAIL'
    }
  }
  togglePwd() {
    this.hide = !this.hide;
  }
  private validateForget(): boolean {
    let passEmailOrMobile = false;
    if (this.forgetContext === 'EMAIL') {
      const var3 = Validator.exist(this.forgetForm.get('email').value)
      this.forgetEmailErrorMsg = var3.errorMsg;
      this.forgetForm.get('email').setErrors(var3.errorMsg ? { wrongValue: true } : null);
      passEmailOrMobile = !this.forgetEmailErrorMsg
    } else if (this.forgetContext === 'MOBILE') {
      const var3 = Validator.exist(this.forgetForm.get('mobileNumber').value)
      this.forgetMobileErrorMsg = var3.errorMsg;
      this.forgetForm.get('mobileNumber').setErrors(var3.errorMsg ? { wrongValue: true } : null);
      passEmailOrMobile = !this.forgetMobileErrorMsg
    }
    const var3 = Validator.exist(this.forgetForm.get('token').value)
    this.forgetCodeErrorMsg = var3.errorMsg;
    this.forgetForm.get('token').setErrors(var3.errorMsg ? { wrongValue: true } : null);

    const var4 = Validator.exist(this.forgetForm.get('pwd').value)
    this.forgetPwdErrorMsg = var4.errorMsg;
    this.forgetForm.get('pwd').setErrors(var4.errorMsg ? { wrongValue: true } : null);
    return !this.forgetPwdErrorMsg && !this.forgetCodeErrorMsg && passEmailOrMobile
  }

  private validateForm(): boolean {
    if (this.loginContext === 'EMAIL') {
      const var3 = Validator.exist(this.form.get('email').value)
      this.emailErrorMsg = var3.errorMsg;
      this.form.get('email').setErrors(var3.errorMsg ? { wrongValue: true } : null);
      const var4 = Validator.exist(this.form.get('emailCode').value)
      this.emailCodeErrorMsg = var4.errorMsg;
      this.form.get('emailCode').setErrors(var4.errorMsg ? { wrongValue: true } : null);
      return !this.emailErrorMsg && !this.emailCodeErrorMsg

    } else if (this.loginContext === 'MOBILE') {
      const var3 = Validator.exist(this.form.get('mobileNumber').value)
      this.mobileErrorMsg = var3.errorMsg;
      this.form.get('mobileNumber').setErrors(var3.errorMsg ? { wrongValue: true } : null);
      const var4 = Validator.exist(this.form.get('mobileCode').value)
      this.mobileCodeErrorMsg = var4.errorMsg;
      this.form.get('mobileCode').setErrors(var4.errorMsg ? { wrongValue: true } : null);
      return !this.mobileErrorMsg && !this.mobileCodeErrorMsg
    } else if (this.loginContext === 'USERNAME') {
      const var3 = Validator.exist(this.form.get('username').value)
      this.usernameErrorMsg = var3.errorMsg;
      this.form.get('username').setErrors(var3.errorMsg ? { wrongValue: true } : null);
      const var4 = Validator.exist(this.form.get('pwd').value)
      this.pwdErrorMsg = var4.errorMsg;
      this.form.get('pwd').setErrors(var4.errorMsg ? { wrongValue: true } : null);
      return !this.usernameErrorMsg && !this.pwdErrorMsg
    }
    return false
  }
  private validateEmail(): boolean {
    const result = Validator.exist(this.form.get('email').value)
    this.emailErrorMsg = result.errorMsg;
    result.errorMsg ? this.form.get('email').setErrors({ wrongValue: true })
      : this.form.get('email').setErrors(null)
    Logger.trace('errors {}', this.form.get('email').errors)
    return !result.errorMsg
  }
  private validateForgetEmail(): boolean {
    const result = Validator.exist(this.forgetForm.get('email').value)
    this.forgetEmailErrorMsg = result.errorMsg;
    result.errorMsg ? this.forgetForm.get('email').setErrors({ wrongValue: true })
      : this.forgetForm.get('email').setErrors(null)
    Logger.trace('errors {}', this.forgetForm.get('email').errors)
    return !result.errorMsg
  }
  private validateForgetMobile(): boolean {
    const result = Validator.exist(this.forgetForm.get('mobileNumber').value)
    this.forgetMobileErrorMsg = result.errorMsg;
    result.errorMsg ? this.forgetForm.get('mobileNumber').setErrors({ wrongValue: true })
      : this.forgetForm.get('mobileNumber').setErrors(null)
    Logger.trace('errors {}', this.forgetForm.get('mobileNumber').errors)
    return !result.errorMsg
  }

  private validateMobile(): boolean {
    const result = Validator.exist(this.form.get('mobileNumber').value)
    this.mobileErrorMsg = result.errorMsg;
    result.errorMsg ? this.form.get('mobileNumber').setErrors({ wrongValue: true })
      : this.form.get('mobileNumber').setErrors(null)
    Logger.trace('errors {}', this.form.get('mobileNumber').errors)
    return !result.errorMsg
  }

  private validateForgetForm(): boolean {
    const result = Validator.exist(this.forgetForm.get('email').value)
    this.forgetEmailErrorMsg = result.errorMsg;
    result.errorMsg ? this.forgetForm.get('email').setErrors({ wrongValue: true })
      : this.forgetForm.get('email').setErrors(null)
    Logger.trace('errors {}', this.forgetForm.get('email').errors)
    return !result.errorMsg
  }

}
