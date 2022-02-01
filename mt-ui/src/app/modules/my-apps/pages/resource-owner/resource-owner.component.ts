import { AfterViewInit, ChangeDetectorRef, Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { MatBottomSheetRef, MAT_BOTTOM_SHEET_DATA } from '@angular/material/bottom-sheet';
import { FormInfoService } from 'mt-form-builder';
import { IOption } from 'mt-form-builder/lib/classes/template.interface';
import { combineLatest, Observable } from 'rxjs';
import { filter, take } from 'rxjs/operators';
import { Aggregate } from 'src/app/clazz/abstract-aggregate';
import { IResourceOwner } from 'src/app/clazz/validation/aggregate/user/interfaze-user';
import { UserValidator } from 'src/app/clazz/validation/aggregate/user/validator-user';
import { ErrorMessage } from 'src/app/clazz/validation/validator-common';
import { FORM_CONFIG } from 'src/app/form-configs/resource-owner.config';
import { ResourceOwnerService } from 'src/app/services/resource-owner.service';
import { RoleService } from 'src/app/services/role.service';
import * as UUID from 'uuid/v1';
@Component({
  selector: 'app-resource-owner',
  templateUrl: './resource-owner.component.html',
  styleUrls: ['./resource-owner.component.css']
})
export class ResourceOwnerComponent extends Aggregate<ResourceOwnerComponent, IResourceOwner> implements OnInit, AfterViewInit, OnDestroy {
  create(): void {
    throw new Error('Method not implemented.');
  }
  private formCreatedOb: Observable<string>;
  constructor(
    public resourceOwnerService: ResourceOwnerService,
    fis: FormInfoService,
    public roleSvc: RoleService,
    @Inject(MAT_BOTTOM_SHEET_DATA) public data: any,
    bottomSheetRef: MatBottomSheetRef<ResourceOwnerComponent>,
    cdr:ChangeDetectorRef
  ) {
    super('resourceOwner', JSON.parse(JSON.stringify(FORM_CONFIG)), new UserValidator(), bottomSheetRef,data,fis,cdr);
    this.formCreatedOb = this.fis.formCreated(this.formId);
    combineLatest([this.formCreatedOb,this.roleSvc.readEntityByQuery(0, 1000,'type:USER')]).pipe(take(1)).subscribe(next => {//@todo add checkbox paginated
      this.formInfo.inputs.find(e => e.key === 'authority').options = next[1].data.map(e => <IOption>{ label: e.name, value: String(e.id) });
      this.formInfo.inputs.find(e => e.key === 'authority').id = UUID()//ngFor chagne detect fix
    })
  }
  ngAfterViewInit(): void {
    if (this.aggregate) {
      this.fis.formGroupCollection[this.formId].get('id').setValue(this.aggregate.id)
      this.fis.formGroupCollection[this.formId].get('email').setValue(this.aggregate.email)
      this.fis.formGroupCollection[this.formId].get('authority').setValue(this.aggregate.grantedAuthorities)
      this.fis.formGroupCollection[this.formId].get('locked').setValue(this.aggregate.locked)
      this.fis.formGroupCollection[this.formId].get('subNewOrder').setValue(this.aggregate.subscription)
      this.cdr.markForCheck()
    }
  }
  ngOnDestroy(): void {
    this.cleanUp()
  }
  ngOnInit() {
  }
  convertToPayload(cmpt: ResourceOwnerComponent): IResourceOwner {
    let formGroup = cmpt.fis.formGroupCollection[cmpt.formId];
    let authority: string[] = [];
    if (Array.isArray(formGroup.get('authority').value)) {
      authority = (formGroup.get('authority').value as Array<string>)
    }
    return {
      id: formGroup.get('id').value,//value is ignored
      locked: formGroup.get('locked').value,
      subscription: formGroup.get('subNewOrder').value,
      grantedAuthorities: authority,
      version:cmpt.aggregate&&cmpt.aggregate.version
    }
  }
  update() {
    if (this.validateHelper.validate(this.validator, this.convertToPayload, 'adminUpdateUserCommandValidator', this.fis, this, this.errorMapper))
      this.resourceOwnerService.update(this.aggregate.id, this.convertToPayload(this), this.changeId)
  }
  errorMapper(original: ErrorMessage[], cmpt: ResourceOwnerComponent) {
    return original.map(e => {
      if (e.key === 'grantedAuthorities') {
        return {
          ...e,
          key: 'authority',
          formId: cmpt.formId
        }
      } else if (e.key === 'subscription') {
        return {
          ...e,
          key: 'subNewOrder',
          formId: cmpt.formId
        }
      } else {
        return {
          ...e,
          formId: cmpt.formId
        }
      }
    })
  }
}
