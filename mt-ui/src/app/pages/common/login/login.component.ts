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
import { IForgetPasswordRequest, IMfaResponse, IOption, IVerificationCodeRequest, ITokenResponse } from 'src/app/misc/interface';
import { Logger } from 'src/app/misc/logger';
import { MatSnackBar } from '@angular/material/snack-bar';
import { RouterWrapperService } from 'src/app/services/router-wrapper';
import { MatTabChangeEvent } from '@angular/material/tabs';
import { Observable } from 'rxjs';
import { DeviceService } from 'src/app/services/device.service';
import { environment } from 'src/environments/environment';
@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  hide = true;
  context: 'LOGIN' | 'FORGET' = 'LOGIN'
  loginContext: 'EMAIL_CODE' | 'PWD' | 'MOBILE_CODE' = 'EMAIL_CODE';
  selectedLoginIndex = 1;
  forgetContext: 'EMAIL' | 'MOBILE' = 'MOBILE';
  nextUrl: string = '/' + RouterWrapperService.HOME_URL;
  DEFAULT_WAIT: number = environment.codeCooldown;
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
  mobileCodeCooldown: boolean = false;
  mobileCodeCountDown: number = this.DEFAULT_WAIT;
  emailCodeCooldown: boolean = false;
  emailCodeCountDown: number = this.DEFAULT_WAIT;
  resetCodeCooldown: boolean = false;
  resetCodeCountDown: number = this.DEFAULT_WAIT;

  codeChangeId = Utility.getChangeId();
  registerChangeId = Utility.getChangeId();
  tokenChangeId = Utility.getChangeId();
  resetChangeId = Utility.getChangeId();
  loginId = Utility.getChangeId();
  DEFAULT_COUNTRY_CODE: string = '1'
  form = new FormGroup({
    countryCode: new FormControl(this.DEFAULT_COUNTRY_CODE, []),
    mobileNumber: new FormControl('', []),
    mobileCode: new FormControl('', []),
    email: new FormControl('', []),
    emailCode: new FormControl('', []),
    pwdEmailOrUsername: new FormControl('', []),
    pwdMobileNumber: new FormControl('', []),
    pwdCountryCode: new FormControl(this.DEFAULT_COUNTRY_CODE, []),
    pwd: new FormControl('', []),
  });
  forgetForm = new FormGroup({
    countryCode: new FormControl(this.DEFAULT_COUNTRY_CODE, []),
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
    public deviceSvc: DeviceService,
    public authSvc: AuthService,
    private snackBar: MatSnackBar
  ) {
    this.httpProxy.refreshInprogress = false;
    this.deviceSvc.updateDocTitle('LOGIN_DOC_TITLE')
    this.router.queryParamMap.subscribe(queryMaps => {
      if (queryMaps.get('redirect_uri') !== null) {
        /** get  authorize party info */
        this.nextUrl = '/' + RouterWrapperService.AUTHORIZE_URL;
      }
    });
    if (deviceSvc.isDemo()) {
      this.translate.get("DEMO_NOTIFICAIONT").subscribe(next => {
        this.snackBar.open(next, 'OK');
      })
    }
    this.translate.get("HOME_NOTIFICAIONT").subscribe(next => {
      this.snackBar.open(next, 'OK');
    })
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
    // {
    //   label: 'COUNTRY_CHINA', value: '86'
    // },
  ]
  loginOrRegister() {
    this.enableError = true;
    if (this.validateForm()) {
      let response: Observable<ITokenResponse | IMfaResponse>;
      if (this.loginContext === 'EMAIL_CODE') {
        response = this.httpProxy.loginEmail(this.form.get('email').value, this.form.get('emailCode').value, this.loginId)
      } else if (this.loginContext === 'MOBILE_CODE') {
        response = this.httpProxy.loginMobile(this.form.get('mobileNumber').value, this.form.get('countryCode').value, this.form.get('mobileCode').value, this.loginId)
      } else if (this.loginContext === 'PWD') {
        if (this.form.get('pwdMobileNumber').value) {
          response = this.httpProxy.loginMobilePwd(this.form.get('pwdMobileNumber').value, this.form.get('pwdCountryCode').value, this.form.get('pwd').value, this.loginId)
        } else {
          if ((this.form.get('pwdEmailOrUsername').value as string).includes("@")) {
            response = this.httpProxy.loginEmailPwd(this.form.get('pwdEmailOrUsername').value, this.form.get('pwd').value, this.loginId)
          } else {
            response = this.httpProxy.loginUsernamePwd(this.form.get('pwdEmailOrUsername').value, this.form.get('pwd').value, this.loginId)
          }
        }
      }
      response.subscribe(next => {
        if ((next as IMfaResponse).message || (next as IMfaResponse).deliveryMethod) {
          this.authSvc.loginFormValue = this.form;
          this.authSvc.loginNextUrl = this.nextUrl;
          this.authSvc.mfaResponse = (next as IMfaResponse);
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
    if (this.loginContext === 'EMAIL_CODE') {
      hasEmailOrMobile = this.validateEmail()
    } else if (this.loginContext === 'MOBILE_CODE') {
      hasEmailOrMobile = this.validateMobile()

    }
    if (hasEmailOrMobile) {
      this.httpProxy.currentUserAuthInfo = undefined;
      let payload: IVerificationCodeRequest
      if (this.loginContext === 'EMAIL_CODE') {
        payload = { email: this.form.get('email').value }
        this.emailCodeCooldown = true;
        this.emailCodeCountDown = this.DEFAULT_WAIT;
        const interval = setInterval(() => {
          this.emailCodeCountDown--;
          if (this.emailCodeCountDown === 0) {
            this.emailCodeCooldown = false;
            clearInterval(interval);
          }
        }, 1000);
      } else {
        payload = { mobileNumber: this.form.get('mobileNumber').value, countryCode: this.form.get('countryCode').value }
        this.mobileCodeCooldown = true;
        this.mobileCodeCountDown = this.DEFAULT_WAIT;
        const interval = setInterval(() => {
          this.mobileCodeCountDown--;
          if (this.mobileCodeCountDown === 0) {
            this.mobileCodeCooldown = false;
            clearInterval(interval);
          }
        }, 1000);
      }
      this.httpProxy.getCode(payload, this.codeChangeId).subscribe(next => {
        this.openDialog('CODE_SEND_MSG');
      }, error => { }, () => {
        if (this.loginContext === 'EMAIL_CODE') {
          this.emailCodeCooldown = true;
        } else {

        }
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
      if (this.forgetContext === 'EMAIL') {
        payload = { email: this.forgetForm.get('email').value }
      } else {
        payload = { mobileNumber: this.forgetForm.get('mobileNumber').value, countryCode: this.forgetForm.get('countryCode').value }
      }
      this.httpProxy.currentUserAuthInfo = undefined;
      this.resetCodeCooldown = true;
      this.resetCodeCountDown = this.DEFAULT_WAIT;
      const interval = setInterval(() => {
        this.resetCodeCountDown--;
        if (this.resetCodeCountDown === 0) {
          this.resetCodeCooldown = false;
          clearInterval(interval);
        }
      }, 1000);
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
      this.loginContext = 'MOBILE_CODE'
    } else if (tabChangeEvent.index === 1) {
      this.loginContext = 'EMAIL_CODE'
    } else if (tabChangeEvent.index === 2) {
      this.loginContext = 'PWD'
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
    if (this.loginContext === 'EMAIL_CODE') {
      const var3 = Validator.exist(this.form.get('email').value)
      this.emailErrorMsg = var3.errorMsg;
      this.form.get('email').setErrors(var3.errorMsg ? { wrongValue: true } : null);
      const var4 = Validator.exist(this.form.get('emailCode').value)
      this.emailCodeErrorMsg = var4.errorMsg;
      this.form.get('emailCode').setErrors(var4.errorMsg ? { wrongValue: true } : null);
      return !this.emailErrorMsg && !this.emailCodeErrorMsg

    } else if (this.loginContext === 'MOBILE_CODE') {
      const var3 = Validator.exist(this.form.get('mobileNumber').value)
      this.mobileErrorMsg = var3.errorMsg;
      this.form.get('mobileNumber').setErrors(var3.errorMsg ? { wrongValue: true } : null);
      const var4 = Validator.exist(this.form.get('mobileCode').value)
      this.mobileCodeErrorMsg = var4.errorMsg;
      this.form.get('mobileCode').setErrors(var4.errorMsg ? { wrongValue: true } : null);
      return !this.mobileErrorMsg && !this.mobileCodeErrorMsg
    } else if (this.loginContext === 'PWD') {
      const var3 = Validator.exist(this.form.get('pwdEmailOrUsername').value)
      const var2 = Validator.exist(this.form.get('pwdMobileNumber').value)
      if (var3.errorMsg && var2.errorMsg) {
        this.usernameErrorMsg = 'LOGIN_USERNAME_REQUIRED';
      } else {
        this.usernameErrorMsg = undefined;
      }
      this.form.get('pwdEmailOrUsername').setErrors(this.usernameErrorMsg ? { wrongValue: true } : null);
      this.form.get('pwdMobileNumber').setErrors(this.usernameErrorMsg ? { wrongValue: true } : null);
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
