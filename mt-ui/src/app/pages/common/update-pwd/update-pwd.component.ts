import { Component, OnDestroy } from '@angular/core';
import { FormInfoService } from 'mt-form-builder';
import { Logger, Utility } from 'src/app/clazz/utility';
import { IUpdatePwdCommand } from 'src/app/clazz/validation/interfaze-user';
import { Validator } from 'src/app/clazz/validation/validator-next-common';
import { FORM_CONFIG } from 'src/app/form-configs/update-pwd.config';
import { UserService } from 'src/app/services/user.service';
@Component({
  selector: 'app-update-pwd',
  templateUrl: './update-pwd.component.html',
  styleUrls: ['./update-pwd.component.css']
})
export class UpdatePwdComponent implements OnDestroy {
  formId = 'updatePwd';
  changeId = Utility.getChangeId()
  private hasSubmitted: boolean = false;
  constructor(
    public resourceOwnerService: UserService,
    private fis: FormInfoService,
  ) {
    this.fis.init(FORM_CONFIG, this.formId)
    this.fis.formGroups[this.formId].valueChanges.subscribe(e => {
      if (this.hasSubmitted) {
        Logger.trace('checking update pwd form')
        this.validateForm()
      }
    })
  }
  private validateForm() {
    const fg = this.fis.formGroups[this.formId]
    const newPwd = fg.get('pwd').value
    const currentPwd = fg.get('currentPwd').value
    const confirmPwd = fg.get('confirmPwd').value
    const var0 = Validator.exist(newPwd)
    const var1 = Validator.exist(currentPwd)
    const var2 = Validator.same(newPwd, confirmPwd)
    this.fis.updateError(this.formId, 'pwd', var0.errorMsg)
    this.fis.updateError(this.formId, 'currentPwd', var1.errorMsg)
    this.fis.updateError(this.formId, 'confirmPwd', var2.errorMsg)
    return !var0.errorMsg && !var1.errorMsg && !var2.errorMsg
  }
  ngOnDestroy(): void {
    this.fis.reset(this.formId)
  }
  updatePwd() {
    this.hasSubmitted = true;
    if (this.validateForm()) {
      const fg = this.fis.formGroups[this.formId]
      const payload: IUpdatePwdCommand = {
        password: fg.get('pwd').value,
        currentPwd: fg.get('currentPwd').value
      }
      this.resourceOwnerService.updateMyPwd(payload, this.changeId)
    }
  }
}
