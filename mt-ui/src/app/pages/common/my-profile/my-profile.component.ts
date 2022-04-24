import { Component, OnInit } from '@angular/core';
import { FormInfoService } from 'mt-form-builder';
import { IForm } from 'mt-form-builder/lib/classes/template.interface';
import { FORM_CONFIG } from 'src/app/form-configs/my-profile.config';
import { AuthService } from 'src/app/services/auth.service';
import { HttpProxyService, IUpdateUser, IUser } from 'src/app/services/http-proxy.service';
import { CustomHttpInterceptor } from 'src/app/services/interceptors/http.interceptor';

@Component({
  selector: 'app-my-profile',
  templateUrl: './my-profile.component.html',
  styleUrls: ['./my-profile.component.css']
})
export class MyProfileComponent implements OnInit {
  formId: string = "myProfile";
  formInfo: IForm = JSON.parse(JSON.stringify(FORM_CONFIG));
  constructor(public authSvc: AuthService, private fis: FormInfoService, private httpSvc: HttpProxyService,private interceptor:CustomHttpInterceptor) { }
  currentUser: IUser
  ngOnInit(): void {
    this.fis.formCreated(this.formId).subscribe(()=>{
      this.authSvc.currentUser.subscribe(next => {
        this.currentUser = next;
        if(this.currentUser.username){
          this.fis.disableIfMatch(this.formId,['username'])
        }
        this.fis.restore(this.formId, {
          language: next.language,
          mobileNumber: next.mobileNumber,
          mobileCountryCode: next.countryCode,
          username: next.username || '',
          avatar: next.avatarLink,
        })
      });
    })
  }
  update() {
    const form = this.fis.formGroupCollection[this.formId];
    const next: IUpdateUser = {
      countryCode: form.get('mobileCountryCode').value ? form.get('mobileCountryCode').value : null,
      mobileNumber: form.get('mobileNumber').value ? form.get('mobileNumber').value : null,
      username: form.get('username').value ? form.get('username').value : null,
      language: form.get('language').value ? form.get('language').value : null,
      avatarLink: form.get('avatar').value ? form.get('avatar').value : null,
    }
    this.httpSvc.updateMyProfile(next).subscribe(_=>{
      this.interceptor.openSnackbar('OPERATION_SUCCESS')
    },error=>{
      this.interceptor.openSnackbar('OPERATION_FAILED')
    })
  }
}
