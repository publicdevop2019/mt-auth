import { ChangeDetectorRef, Component, OnDestroy, OnInit } from '@angular/core';
import { FormInfoService } from 'mt-form-builder';
import { Subscription } from 'rxjs';
import { createImageFromBlob } from 'src/app/misc/utility';
import { FORM_CONFIG } from 'src/app/form-configs/my-profile.config';
import { AuthService } from 'src/app/services/auth.service';
import { HttpProxyService, IUpdateUser, IUser } from 'src/app/services/http-proxy.service';
import { CustomHttpInterceptor } from 'src/app/services/interceptors/http.interceptor';

@Component({
  selector: 'app-my-profile',
  templateUrl: './my-profile.component.html',
  styleUrls: ['./my-profile.component.css']
})
export class MyProfileComponent implements OnInit, OnDestroy {
  formId: string = "myProfile";
  currentUser: IUser;
  subs: Subscription;
  constructor(
    public authSvc: AuthService,
    private fis: FormInfoService,
    private httpSvc: HttpProxyService,
    private interceptor: CustomHttpInterceptor,
    private cdr: ChangeDetectorRef,
  ) { 
    this.fis.init(FORM_CONFIG, this.formId)
  }
  ngOnDestroy(): void {
    this.subs.unsubscribe();
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
}
