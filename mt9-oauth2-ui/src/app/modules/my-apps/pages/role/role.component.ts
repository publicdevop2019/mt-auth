import { AfterViewInit, ChangeDetectorRef, Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { MatBottomSheetRef, MAT_BOTTOM_SHEET_DATA } from '@angular/material/bottom-sheet';
import { FormInfoService } from 'mt-form-builder';
import { Aggregate } from 'src/app/clazz/abstract-aggregate';
import { IBottomSheet } from 'src/app/clazz/summary.component';
import { IRole } from 'src/app/clazz/validation/aggregate/role/interface-role';
import { RoleValidator } from 'src/app/clazz/validation/aggregate/role/validator-role';
import { UserValidator } from 'src/app/clazz/validation/aggregate/user/validator-user';
import { ErrorMessage } from 'src/app/clazz/validation/validator-common';
import { FORM_CONFIG } from 'src/app/form-configs/role.config';
import { RoleService } from 'src/app/services/role.service';
@Component({
  selector: 'app-role',
  templateUrl: './role.component.html',
  styleUrls: ['./role.component.css']
})
export class RoleComponent extends Aggregate<RoleComponent, IRole> implements OnInit, AfterViewInit, OnDestroy {
  bottomSheet: IBottomSheet<IRole>;

  constructor(
    public entityService: RoleService,
    fis: FormInfoService,
    @Inject(MAT_BOTTOM_SHEET_DATA) public data: any,
    bottomSheetRef: MatBottomSheetRef<RoleComponent>,
    cdr: ChangeDetectorRef
  ) {
    super('role-form', JSON.parse(JSON.stringify(FORM_CONFIG)), new RoleValidator(), bottomSheetRef, data, fis, cdr)
    this.bottomSheet = data;
  }
  ngAfterViewInit(): void {
    if (this.aggregate) {
      this.fis.formGroupCollection[this.formId].get('id').setValue(this.aggregate.id)
      this.fis.formGroupCollection[this.formId].get('name').setValue(this.aggregate.name)
      this.fis.formGroupCollection[this.formId].get('description').setValue(this.aggregate.description)
      this.fis.formGroupCollection[this.formId].get('type').setValue(this.aggregate.type)
      if(this.bottomSheet.context!=='clone'){
        this.fis.formGroupCollection_formInfo[this.formId].inputs.find(e => e.key === 'type').disabled = true;
      }
      this.cdr.markForCheck()
    }
  }
  ngOnDestroy(): void {
    this.cleanUp()
  }
  ngOnInit() {
  }
  convertToPayload(cmpt: RoleComponent): IRole {
    let formGroup = cmpt.fis.formGroupCollection[cmpt.formId];
    return {
      id: formGroup.get('id').value,//value is ignored
      name: formGroup.get('name').value,
      description: formGroup.get('description').value?formGroup.get('description').value:null,
      type: formGroup.get('type').value,
      version: cmpt.aggregate && cmpt.aggregate.version
    }
  }
  update() {
    if (this.validateHelper.validate(this.validator, this.convertToPayload, 'update', this.fis, this, this.errorMapper))
      this.entityService.update(this.aggregate.id, this.convertToPayload(this), this.changeId)
  }
  create() {
    if (this.validateHelper.validate(this.validator, this.convertToPayload, 'create', this.fis, this, this.errorMapper)){
      this.entityService.create(this.convertToPayload(this), this.changeId)
    }
  }
  errorMapper(original: ErrorMessage[], cmpt: RoleComponent) {
    return original.map(e => {
      return {
        ...e,
        formId: cmpt.formId
      }
    })
  }
}
