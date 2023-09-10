import { ChangeDetectorRef, Component, OnDestroy, OnInit } from '@angular/core';
import { FormInfoService } from 'mt-form-builder';
import { Subscription } from 'rxjs';
import { Utility, createImageFromBlob } from 'src/app/misc/utility';
import { FORM_CONFIG } from 'src/app/form-configs/my-profile.config';
import { AuthService } from 'src/app/services/auth.service';
import { HttpProxyService, IUpdateUser, IUser } from 'src/app/services/http-proxy.service';
import { CustomHttpInterceptor } from 'src/app/services/interceptors/http.interceptor';
import { IUpdatePwdCommand } from 'src/app/misc/interface';
import { Logger } from 'src/app/misc/logger';
import { Validator } from 'src/app/misc/validator';
import { UserService } from 'src/app/services/user.service';
import { FORM_CONFIG as PWD_FORM_CONFIG } from 'src/app/form-configs/update-pwd.config';
@Component({
  selector: 'app-my-profile',
  templateUrl: './my-profile.component.html',
  styleUrls: ['./my-profile.component.css']
})
export class MyProfileComponent implements OnInit, OnDestroy {
  formId: string = "myProfile";
  pwdFormId = 'updatePwd';
  currentUser: IUser;
  subs: Subscription;
  changeId = Utility.getChangeId()
  private hasSubmitted: boolean = false;
  constructor(
    public authSvc: AuthService,
    private fis: FormInfoService,
    private httpSvc: HttpProxyService,
    private interceptor: CustomHttpInterceptor,
    private cdr: ChangeDetectorRef,
    public resourceOwnerService: UserService,
  ) {
    this.fis.init(FORM_CONFIG, this.formId);
    this.fis.init(PWD_FORM_CONFIG, this.pwdFormId)
    this.fis.formGroups[this.pwdFormId].valueChanges.subscribe(e => {
      if (this.hasSubmitted) {
        Logger.trace('checking update pwd form')
        this.validateForm()
      }
    })
  }
  ngOnDestroy(): void {
    this.subs.unsubscribe();
    this.fis.reset(this.formId)
    this.fis.reset(this.pwdFormId)
  }
  ngOnInit(): void {
    this.subs = this.fis.$uploadFile.subscribe(next => {
      this._uploadFile(next.files);
    })
    this.authSvc.currentUser.subscribe(next => {
      this.currentUser = next;
      if (this.currentUser.username) {
        this.fis.disableIfMatch(this.formId, ['username'])
      }
      this.httpSvc.getAvatar().subscribe(blob => {
        createImageFromBlob(blob, (reader) => {
          this.fis.restore(this.formId, {
            avatar: reader.result,
          })
        })
      })
      this.fis.restore(this.formId, {
        language: next.language,
        mobileNumber: next.mobileNumber,
        mobileCountryCode: next.countryCode,
        username: next.username || '',
      })
    });
  }

  private _uploadFile(files: FileList) {
    this.httpSvc.uploadFile(files.item(0)).subscribe(() => {
      this.authSvc.avatarUpdated$.next()
      this.httpSvc.getAvatar().subscribe(blob => {
        createImageFromBlob(blob, (reader) => {
          this.fis.restore(this.formId, {
            avatar: reader.result,
          })
        })
      })
      this.cdr.detectChanges();
    })
  }
  update() {
    const form = this.fis.formGroups[this.formId];
    const next: IUpdateUser = {
      countryCode: form.get('mobileCountryCode').value ? form.get('mobileCountryCode').value : null,
      mobileNumber: form.get('mobileNumber').value ? form.get('mobileNumber').value : null,
      username: form.get('username').value ? form.get('username').value : null,
      language: form.get('language').value ? form.get('language').value : null,
    }
    this.httpSvc.updateMyProfile(next).subscribe(_ => {
      this.interceptor.openSnackbar('OPERATION_SUCCESS')
    }, error => {
      this.interceptor.openSnackbar('OPERATION_FAILED')
    })
  }

  private validateForm() {
    const fg = this.fis.formGroups[this.pwdFormId]
    const newPwd = fg.get('pwd').value
    const currentPwd = fg.get('currentPwd').value
    const confirmPwd = fg.get('confirmPwd').value
    const var0 = Validator.exist(newPwd)
    const var1 = Validator.exist(currentPwd)
    const var2 = Validator.same(newPwd, confirmPwd)
    this.fis.updateError(this.pwdFormId, 'pwd', var0.errorMsg)
    this.fis.updateError(this.pwdFormId, 'currentPwd', var1.errorMsg)
    this.fis.updateError(this.pwdFormId, 'confirmPwd', var2.errorMsg)
    return !var0.errorMsg && !var1.errorMsg && !var2.errorMsg
  }
  updatePwd() {
    this.hasSubmitted = true;
    if (this.validateForm()) {
      const fg = this.fis.formGroups[this.pwdFormId]
      const payload: IUpdatePwdCommand = {
        password: fg.get('pwd').value,
        currentPwd: fg.get('currentPwd').value
      }
      this.resourceOwnerService.updateMyPwd(payload, this.changeId)
    }
  }
}
