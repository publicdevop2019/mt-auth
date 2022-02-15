import { AfterViewInit, ChangeDetectorRef, Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { MatBottomSheetRef, MAT_BOTTOM_SHEET_DATA } from '@angular/material/bottom-sheet';
import { FormInfoService } from 'mt-form-builder';
import { Observable } from 'rxjs';
import { Aggregate } from 'src/app/clazz/abstract-aggregate';
import { IAuthUser } from 'src/app/clazz/validation/aggregate/user/interfaze-user';
import { UserValidator } from 'src/app/clazz/validation/aggregate/user/validator-user';
import { ErrorMessage } from 'src/app/clazz/validation/validator-common';
import { FORM_CONFIG } from 'src/app/form-configs/resource-owner.config';
import { MyRoleService } from 'src/app/services/my-role.service';
import { UserService } from 'src/app/services/user.service';
@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.css']
})
export class ResourceOwnerComponent extends Aggregate<ResourceOwnerComponent, IAuthUser> implements OnInit, AfterViewInit, OnDestroy {
  create(): void {
    throw new Error('Method not implemented.');
  }
  constructor(
    public resourceOwnerService: UserService,
    fis: FormInfoService,
    public roleSvc: MyRoleService,
    @Inject(MAT_BOTTOM_SHEET_DATA) public data: any,
    bottomSheetRef: MatBottomSheetRef<ResourceOwnerComponent>,
    cdr: ChangeDetectorRef
  ) {
    super('authUser', JSON.parse(JSON.stringify(FORM_CONFIG)), new UserValidator(), bottomSheetRef, data, fis, cdr);
  }
  ngAfterViewInit(): void {
    if (this.aggregate) {
      this.fis.formGroupCollection[this.formId].get('id').setValue(this.aggregate.id)
      this.fis.formGroupCollection[this.formId].get('email').setValue(this.aggregate.email)
      this.fis.formGroupCollection[this.formId].get('locked').setValue(this.aggregate.locked)
      this.fis.formGroupCollection[this.formId].get('createdAt').setValue(new Date(this.aggregate.createdAt))
      this.cdr.markForCheck()
    }
  }
  ngOnDestroy(): void {
    this.cleanUp()
  }
  ngOnInit() {
  }
  convertToPayload(cmpt: ResourceOwnerComponent): IAuthUser {
    let formGroup = cmpt.fis.formGroupCollection[cmpt.formId];
    let authority: string[] = [];
    if (Array.isArray(formGroup.get('authority').value)) {
      authority = (formGroup.get('authority').value as Array<string>)
    }
    return {
      id: formGroup.get('id').value,//value is ignored
      locked: formGroup.get('locked').value,
      version: cmpt.aggregate && cmpt.aggregate.version
    }
  }
  update() {
    if (this.validateHelper.validate(this.validator, this.convertToPayload, 'adminUpdateUserCommandValidator', this.fis, this, this.errorMapper))
      this.resourceOwnerService.update(this.aggregate.id, this.convertToPayload(this), this.changeId)
  }
  errorMapper(original: ErrorMessage[], cmpt: ResourceOwnerComponent) {
    return original.map(e => {
      return {
        ...e,
        formId: cmpt.formId
      }
    })
  }
}
