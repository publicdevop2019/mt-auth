import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ITokenResponse } from 'src/app/clazz/validation/common.interface';
import { AuthService } from 'src/app/services/auth.service';
import { HttpProxyService } from 'src/app/services/http-proxy.service';

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
  constructor(public httpProxy: HttpProxyService, public authSvc: AuthService, private router: ActivatedRoute, private route: Router,) { }

  ngOnInit(): void {
  }
  confirm() {
    if (!this.mfaForm.get('mfaCode').value) {
      this.mfaCodeErrorMsg = 'REQUIRED';
      this.mfaForm.get('mfaCode').setErrors({ wrongValue: true });
    } else {
      if(this.authSvc.loginFormValue){
        const var0 = this.authSvc.loginFormValue;
        const var1 = this.authSvc.mfaId;
        const var2 = this.authSvc.loginNextUrl;
        this.authSvc.loginFormValue = undefined;
        this.authSvc.mfaId = undefined;
        this.httpProxy.mfaLogin(var0, this.mfaForm.get('mfaCode').value, var1).subscribe(next => {
          this.httpProxy.currentUserAuthInfo = next as ITokenResponse;
          this.route.navigate([var2], { queryParams: this.router.snapshot.queryParams });
        })
      }else{
        this.route.navigate(['/login'], { queryParams: this.router.snapshot.queryParams });
      }
    }
  }
  resend() {

  }
}
