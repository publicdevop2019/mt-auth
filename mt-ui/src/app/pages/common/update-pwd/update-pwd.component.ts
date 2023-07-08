import { AfterViewInit, Component, OnDestroy, OnInit } from '@angular/core';
import { FormInfoService } from 'mt-form-builder';
import { IForm } from 'mt-form-builder/lib/classes/template.interface';
import { Utility } from 'src/app/clazz/utility';
import { ValidatorHelper } from 'src/app/clazz/validateHelper';
import { IResourceOwnerUpdatePwd } from 'src/app/clazz/validation/aggregate/user/interfaze-user';
import { UserValidator } from 'src/app/clazz/validation/aggregate/user/validator-user';
import { ErrorMessage } from 'src/app/clazz/validation/validator-common';
import { FORM_CONFIG } from 'src/app/form-configs/update-pwd.config';
import { UserService } from 'src/app/services/user.service';
import * as UUID from 'uuid/v1';
@Component({
  selector: 'app-update-pwd',
  templateUrl: './update-pwd.component.html',
  styleUrls: ['./update-pwd.component.css']
})
export class UpdatePwdComponent implements OnInit, AfterViewInit, OnDestroy {
  formId = 'updatePwd';
  changeId = UUID();
  formInfo: IForm = JSON.parse(JSON.stringify(FORM_CONFIG));
  private validator = new UserValidator()
  private validateHelper = new ValidatorHelper()
  constructor(
    public resourceOwnerService: UserService,
    private fis: FormInfoService,
  ) {
    this.fis.init(this.formInfo, this.formId)
  }
  ngAfterViewInit(): void {
  }
  ngOnDestroy(): void {
  }
  ngOnInit() {
  }
  convertToPayload(cmpt: UpdatePwdComponent): IResourceOwnerUpdatePwd {
    let formGroup = cmpt.fis.formGroups[cmpt.formId];
    return {
      password: formGroup.get('pwd').value,
      currentPwd: formGroup.get('currentPwd').value
    }
  }
  updatePwd() {
    if (this.checkConfirmPwd()) {
    } else {
      this.fis.forms[this.formId].inputs.find(e => e.key === 'confirmPwd').errorMsg = "PWD_NOT_SAME"
      this.fis.formGroups[this.formId].valueChanges.subscribe(e => {
        if (this.checkConfirmPwd()) {
          this.fis.forms[this.formId].inputs.find(e => e.key === 'confirmPwd').errorMsg = undefined
        } else {
          this.fis.forms[this.formId].inputs.find(e => e.key === 'confirmPwd').errorMsg = "PWD_NOT_SAME"
        }
      })
    }
    if (this.checkConfirmPwd() && this.validateHelper.validate(this.validator, this.convertToPayload, 'userUpdatePwdCommandValidator', this.fis, this, this.errorMapper)) {
      this.resourceOwnerService.updateMyPwd(this.convertToPayload(this), this.changeId)
    }
  }
  checkConfirmPwd(): boolean {
    return this.fis.formGroups[this.formId].get('confirmPwd').value === this.fis.formGroups[this.formId].get('pwd').value
  }

  errorMapper(original: ErrorMessage[], cmpt: UpdatePwdComponent) {
    return original.map(e => {
      if (e.key === 'password') {
        return {
          ...e,
          key: 'pwd',
          formId: cmpt.formId
        }
      }
      return {
        ...e,
        formId: cmpt.formId
      }
    })
  }
}
