import { ChangeDetectorRef, Component, OnDestroy, OnInit } from '@angular/core';
import { Subscription } from 'rxjs';
import { Utility } from 'src/app/misc/utility';
import { AuthService } from 'src/app/services/auth.service';
import { HttpProxyService, IUser } from 'src/app/services/http-proxy.service';
import { IUpdatePwdCommand } from 'src/app/misc/interface';
import { Logger } from 'src/app/misc/logger';
import { Validator } from 'src/app/misc/validator';
import { FormGroup, FormControl } from '@angular/forms';
import { DeviceService } from 'src/app/services/device.service';
@Component({
  selector: 'app-my-profile',
  templateUrl: './my-profile.component.html',
  styleUrls: ['./my-profile.component.css']
})
export class MyProfileComponent implements OnInit, OnDestroy {
  context: 'PROFILE_EDIT' | 'PWD_UPDATE' = 'PROFILE_EDIT'
  updatePwdFg = new FormGroup({
    currentPwd: new FormControl(''),
    pwd: new FormControl(''),
    confirmPwd: new FormControl(''),
  });
  userInfo: IUser;
  profileFg = new FormGroup({
    avatar: new FormControl(''),
    username: new FormControl(''),
    mobileCountryCode: new FormControl(''),
    mobileNumber: new FormControl(''),
    language: new FormControl(''),
    email: new FormControl(''),
  });
  currentUser: IUser;
  currentPwdErrorMsg: string;
  newPwdErrorMsg: string;
  confirmPwdErrorMsg: string;
  subs: Subscription = new Subscription();
  changeId = Utility.getChangeId()
  private hasSubmitted: boolean = false;
  constructor(
    public authSvc: AuthService,
    private httpSvc: HttpProxyService,
    private deviceSvc: DeviceService,
    private cdr: ChangeDetectorRef,
  ) {
    this.updatePwdFg.valueChanges.subscribe(e => {
      if (this.hasSubmitted) {
        Logger.trace('checking update pwd form')
        this.validateForm()
      }
    })
  }
  ngOnDestroy(): void {
    this.subs.unsubscribe();
  }
  handleFileUpload(fileList: FileList) {
    Logger.debugObj('fileList', fileList)
    if (fileList === undefined) {
      //TODO delete file
      this.profileFg.get('avatar').setValue(undefined)
    } else {
      this.httpSvc.uploadAvatar(fileList.item(0)).subscribe(() => {
        this.authSvc.avatarUpdated$.next()
        this.httpSvc.getAvatar().subscribe(blob => {
          Utility.createImageFromBlob(blob, (reader) => {
            this.profileFg.get('avatar').setValue(reader.result)
          })
        })
        this.cdr.detectChanges();
      })
    }
  }
  ngOnInit(): void {
    this.authSvc.currentUser.subscribe(next => {
      this.currentUser = next;
      this.httpSvc.getAvatar().subscribe(blob => {
        Utility.createImageFromBlob(blob, (reader) => {
          this.profileFg.get('avatar').setValue(reader.result)
        })
      })
      this.userInfo = next;
      this.profileFg.patchValue({
        language: next.language,
        mobileNumber: next.mobileNumber,
        mobileCountryCode: next.countryCode,
        username: next.username || '',
        email: next.email,
      })
      this.profileFg.get('language').valueChanges.subscribe(next => {
        this.httpSvc.updateProfileLanguage(next).subscribe(_ => {
          this.deviceSvc.notify(true)
        }, () => {
          this.deviceSvc.notify(false)
        })
      });
    });

  }
  private validateForm() {
    const newPwd = this.updatePwdFg.get('pwd').value
    const currentPwd = this.updatePwdFg.get('currentPwd').value
    const confirmPwd = this.updatePwdFg.get('confirmPwd').value
    this.newPwdErrorMsg = Validator.exist(newPwd).errorMsg
    this.currentPwdErrorMsg = Validator.exist(currentPwd).errorMsg
    this.confirmPwdErrorMsg = Validator.same(newPwd, confirmPwd).errorMsg
    return !this.newPwdErrorMsg && !this.currentPwdErrorMsg && !this.confirmPwdErrorMsg
  }
  updatePwd() {
    this.hasSubmitted = true;
    if (this.validateForm()) {
      const payload: IUpdatePwdCommand = {
        password: this.updatePwdFg.get('pwd').value,
        currentPwd: this.updatePwdFg.get('currentPwd').value
      }
      this.httpSvc.updateUserPwd(payload, this.changeId).subscribe(result => {
        this.deviceSvc.notify(result)
        Utility.logout(undefined, this.httpSvc)
      });
    }
  }
  addEmail() {
    this.httpSvc.addProfileEmail(this.profileFg.get('email').value, Utility.getChangeId()).subscribe(_ => {
      this.deviceSvc.notify(true)
    }, () => {
      this.deviceSvc.notify(false)
    })
  }
  removeEmail() {
    this.httpSvc.removeProfileEmail(Utility.getChangeId()).subscribe(_ => {
      this.deviceSvc.notify(true)
    }, () => {
      this.deviceSvc.notify(false)
    })
  }
  addPhone() {
    this.httpSvc.addProfileMobile(this.profileFg.get('mobileCountryCode').value, this.profileFg.get('mobileNumber').value, Utility.getChangeId()).subscribe(_ => {
      this.deviceSvc.notify(true)
    }, () => {
      this.deviceSvc.notify(false)
    })
  }
  removePhone() {
    this.httpSvc.removeProfileMobile(Utility.getChangeId()).subscribe(_ => {
      this.deviceSvc.notify(true)
    }, () => {
      this.deviceSvc.notify(false)
    })
  }
  addUsername() {
    this.httpSvc.addProfileUsername(this.profileFg.get('username').value, Utility.getChangeId()).subscribe(_ => {
      this.deviceSvc.notify(true)
    }, () => {
      this.deviceSvc.notify(false)
    })
  }
  removeUsername() {
    this.httpSvc.removeProfileUsername(Utility.getChangeId()).subscribe(_ => {
      this.deviceSvc.notify(true)
    }, () => {
      this.deviceSvc.notify(false)
    })
  }
}
