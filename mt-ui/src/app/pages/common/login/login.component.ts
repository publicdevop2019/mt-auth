import { Component } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { IOption } from 'mt-form-builder/lib/classes/template.interface';
import { Utility } from 'src/app/misc/utility';
import { Validator } from 'src/app/misc/validator';
import { MsgBoxComponent } from 'src/app/components/msg-box/msg-box.component';
import { AuthService } from 'src/app/services/auth.service';
import { HttpProxyService } from 'src/app/services/http-proxy.service';
import { LanguageService } from 'src/app/services/language.service';
import { IForgetPasswordRequest, IMfaResponse, IPendingUser, ITokenResponse } from 'src/app/misc/interface';
import { Logger } from 'src/app/misc/logger';
import { MatSnackBar } from '@angular/material/snack-bar';
import { RouterWrapperService } from 'src/app/services/router-wrapper';
@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  context: 'REGISTER' | 'LOGIN' | 'FORGET' = this.hasLoginSuccessfully ? 'LOGIN' : 'REGISTER'
  public get hasLoginSuccessfully() {
    return localStorage.getItem('success_login') === 'true'
  }
  public set hasLoginSuccessfully(next: boolean) {
    localStorage.setItem('success_login', next + '')
  }
  nextUrl: string = '/home';

  loginEmailErrorMsg: string = undefined;
  loginPwdErrorMsg: string = undefined;

  forgetEmailErrorMsg: string = undefined;
  forgetTokenErrorMsg: string = undefined;
  forgetPwdErrorMsg: string = undefined;
  forgetConfirmPwdErrorMsg: string = undefined;

  registerEmailErrorMsg: string = undefined;
  registerCodeErrorMsg: string = undefined;
  registerMobileCountryCodeErrorMsg: string = undefined;
  registerMobilePhoneNumErrorMsg: string = undefined;
  registerPwdErrorMsg: string = undefined;
  registerConfirmPwdErrorMsg: string = undefined;

  enableResigerEmailError: boolean = false;
  enableResigerOtherError: boolean = false;
  enableForgetEmailError: boolean = false;
  enableForgetOtherError: boolean = false;
  enableLoginError: boolean = false;

  activationCodeChangeId = Utility.getChangeId();
  registerChangeId = Utility.getChangeId();
  tokenChangeId = Utility.getChangeId();
  resetChangeId = Utility.getChangeId();
  loginForm = new FormGroup({
    email: new FormControl('', []),
    pwd: new FormControl('', []),
  });
  registerForm = new FormGroup({
    email: new FormControl('', []),
    activationCode: new FormControl('', []),
    countryCode: new FormControl('', []),
    mobileNumber: new FormControl('', []),
    pwd: new FormControl('', []),
    confirmPwd: new FormControl('', []),
  });
  forgetForm = new FormGroup({
    email: new FormControl('', []),
    token: new FormControl('', []),
    pwd: new FormControl('', []),
    confirmPwd: new FormControl('', []),
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
        this.nextUrl = '/authorize';
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

    this.loginForm.valueChanges.subscribe(() => {
      if (this.enableLoginError) {
        Logger.debug('checking login')
        this.validateLogin();
      }
    })
    this.registerForm.valueChanges.subscribe(() => {
      if (this.enableResigerEmailError) {
        Logger.debug('checking register email')
        this.validateRegsiterEmail();
      }
      if (this.enableResigerOtherError) {
        Logger.debug('checking register others')
        this.validateRegsiterOthers();
      }
    })
    this.forgetForm.valueChanges.subscribe(() => {
      if (this.enableForgetEmailError) {
        Logger.debug('checking forget email')
        this.validateForgetEmail();
      }
      if (this.enableForgetOtherError) {
        Logger.debug('checking forget email')
        this.validateForgetOthers();
      }
    })
  }
  mobileNums: IOption[] = [
    {
      label: '+1', value: '1'
    },
    {
      label: '+86', value: '86'
    },
  ]
  login() {
    this.enableLoginError = true;
    if (this.validateLogin()) {
      this.httpProxy.login(this.loginForm.get('email').value, this.loginForm.get('pwd').value).subscribe(next => {
        this.hasLoginSuccessfully = true;
        if ((next as IMfaResponse).mfaId) {
          this.authSvc.loginFormValue = this.loginForm;
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

  register() {
    this.enableResigerEmailError = true;
    this.enableResigerOtherError = true;
    const var0 = this.validateRegsiterEmail()
    const var1 = this.validateRegsiterOthers()
    if (var0 && var1) {
      const payload: IPendingUser = {
        email: this.registerForm.get('email').value,
        password: this.registerForm.get('pwd').value,
        activationCode: this.registerForm.get('activationCode').value,
        mobileNumber: this.registerForm.get('mobileNumber').value,
        countryCode: this.registerForm.get('countryCode').value,
      };
      this.httpProxy.register(payload, this.registerChangeId).subscribe(next => {
        this.context = 'LOGIN'
        this.loginForm.get('email').setValue(this.registerForm.get('email').value)
        this.loginForm.get('pwd').setValue(this.registerForm.get('pwd').value)
        this.openDialog('REGISTER_SUCCESS_MSG');
      })
    }
  }



  getCode() {
    this.enableResigerEmailError = true;
    if (this.validateRegsiterEmail()) {
      this.httpProxy.currentUserAuthInfo = undefined;
      const payload: IPendingUser = { email: this.registerForm.get('email').value }
      this.httpProxy.activate(payload, this.activationCodeChangeId).subscribe(next => {
        this.openDialog('CODE_SEND_MSG');
      })
    }
  }
  getToken() {
    this.enableForgetEmailError = true;
    if (this.validateForgetEmail()) {
      this.httpProxy.currentUserAuthInfo = undefined;
      const payload: IPendingUser = { email: this.forgetForm.get('email').value }
      this.httpProxy.forgetPwd(payload, this.tokenChangeId).subscribe(next => {
        this.openDialog('TOKEN_SEND_MSG');
      })
    }
  }
  changePassword() {
    this.enableForgetEmailError = true;
    this.enableForgetOtherError = true;
    const var0 = this.validateForgetEmail()
    const var1 = this.validateForgetOthers()
    if (var0 && var1) {
      const payload: IForgetPasswordRequest = {
        email: this.forgetForm.get('email').value,
        token: this.forgetForm.get('token').value,
        newPassword: this.forgetForm.get('pwd').value,
      };
      this.httpProxy.resetPwd(payload, this.resetChangeId).subscribe(next => {
        this.context = 'LOGIN'
        this.loginForm.get('email').setValue(this.forgetForm.get('email').value)
        this.loginForm.get('pwd').setValue(this.forgetForm.get('pwd').value);
        this.openDialog('PASSWORD_UPDATE_SUCCESS_MSG');
      })
    }
  }
  openDialog(msg: string): void {
    this.dialog.open(MsgBoxComponent, {
      width: '250px',
      data: msg
    });
  }
  openDoc() {
    window.open('./docs', '_blank').focus();
  }
  openSource() {
    if (this.langSvc.currentLanguage() === 'zhHans') {
      window.open('https://gitee.com/mirrors/MT-AUTH', '_blank').focus();
    } else {
      window.open('https://github.com/publicdevop2019/mt-auth', '_blank').focus();
    }
  }
  showPasswordHint() {
    if (this.context === 'REGISTER') {
      return !!this.registerForm.get('pwd').value
    }
    if (this.context === 'FORGET') {
      return !!this.forgetForm.get('pwd').value
    }
    return false
  }
  private validateRegsiterOthers(): boolean {
    const var0 = Validator.same(this.registerForm.get('confirmPwd').value, this.registerForm.get('pwd').value)
    this.registerConfirmPwdErrorMsg = var0.errorMsg;
    this.registerForm.get('confirmPwd').setErrors(var0.errorMsg ? { wrongValue: true } : null);

    const var1 = Validator.exist(this.registerForm.get('countryCode').value)
    this.registerMobileCountryCodeErrorMsg = var1.errorMsg;
    this.registerForm.get('countryCode').setErrors(var1.errorMsg ? { wrongValue: true } : null);

    const var2 = Validator.exist(this.registerForm.get('mobileNumber').value)
    this.registerMobilePhoneNumErrorMsg = var2.errorMsg;
    this.registerForm.get('mobileNumber').setErrors(var2.errorMsg ? { wrongValue: true } : null);

    const var3 = Validator.exist(this.registerForm.get('activationCode').value)
    this.registerCodeErrorMsg = var3.errorMsg;
    this.registerForm.get('activationCode').setErrors(var3.errorMsg ? { wrongValue: true } : null);

    const var4 = Validator.exist(this.registerForm.get('pwd').value)
    this.registerPwdErrorMsg = var4.errorMsg;
    this.registerForm.get('pwd').setErrors(var4.errorMsg ? { wrongValue: true } : null);
    return !this.registerPwdErrorMsg && !this.registerCodeErrorMsg
      && !this.registerMobilePhoneNumErrorMsg && !this.registerMobileCountryCodeErrorMsg
      && !this.registerConfirmPwdErrorMsg
  }
  private validateForgetOthers(): boolean {
    const var0 = Validator.same(this.forgetForm.get('confirmPwd').value, this.forgetForm.get('pwd').value)
    this.forgetConfirmPwdErrorMsg = var0.errorMsg;
    this.forgetForm.get('confirmPwd').setErrors(var0.errorMsg ? { wrongValue: true } : null);

    const var3 = Validator.exist(this.forgetForm.get('token').value)
    this.forgetTokenErrorMsg = var3.errorMsg;
    this.forgetForm.get('token').setErrors(var3.errorMsg ? { wrongValue: true } : null);

    const var4 = Validator.exist(this.forgetForm.get('pwd').value)
    this.forgetPwdErrorMsg = var4.errorMsg;
    this.forgetForm.get('pwd').setErrors(var4.errorMsg ? { wrongValue: true } : null);
    return !this.forgetPwdErrorMsg && !this.forgetTokenErrorMsg && !this.forgetConfirmPwdErrorMsg
  }
  private validateLogin(): boolean {
    const var3 = Validator.exist(this.loginForm.get('email').value)
    this.loginEmailErrorMsg = var3.errorMsg;
    this.loginForm.get('email').setErrors(var3.errorMsg ? { wrongValue: true } : null);

    const var4 = Validator.exist(this.loginForm.get('pwd').value)
    this.loginPwdErrorMsg = var4.errorMsg;
    this.loginForm.get('pwd').setErrors(var4.errorMsg ? { wrongValue: true } : null);
    return !this.loginPwdErrorMsg && !this.loginEmailErrorMsg
  }
  private validateRegsiterEmail(): boolean {
    const result = Validator.exist(this.registerForm.get('email').value)
    this.registerEmailErrorMsg = result.errorMsg;
    result.errorMsg ? this.registerForm.get('email').setErrors({ wrongValue: true })
      : this.registerForm.get('email').setErrors(null)
    Logger.trace('errors {}', this.registerForm.get('email').errors)
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
}
