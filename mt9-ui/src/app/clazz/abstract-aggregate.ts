import { ChangeDetectorRef } from '@angular/core';
import { MatBottomSheetRef } from '@angular/material/bottom-sheet';
import { FormInfoService } from 'mt-form-builder';
import { IForm } from 'mt-form-builder/lib/classes/template.interface';
import { Subject, Subscription } from 'rxjs';
import * as UUID from 'uuid/v1';
import { EntityCommonService } from './entity.common-service';
import { IBottomSheet, IIdBasedEntity } from './summary.component';
import { ValidatorHelper } from './validateHelper';
import { ErrorMessage, IAggregateValidator } from './validation/validator-common';
export abstract class Aggregate<C, T extends IIdBasedEntity>{
    formId: string;
    formInfo: IForm;
    validator: IAggregateValidator;
    changeId: string = UUID();
    bottomSheetRef: MatBottomSheetRef<C>
    aggregateSvc: EntityCommonService<T, T>
    subs: { [key: string]: Subscription } = {};
    aggregate: T;
    fis: FormInfoService;
    cdr: ChangeDetectorRef;
    delayResume: boolean = false;
    resumeComplete: Subject<boolean> = new Subject<boolean>();
    constructor(
        formId: string,
        formInfo: IForm,
        validator: IAggregateValidator,
        bottomSheetRef: MatBottomSheetRef<C>,
        bottomSheetData: IBottomSheet<T>,
        fis: FormInfoService,
        cdr: ChangeDetectorRef,
    ) {
        this.formId = formId;
        this.formInfo = formInfo;
        this.validator = validator;
        this.bottomSheetRef = bottomSheetRef;
        this.aggregate = bottomSheetData.from;
        this.fis = fis;
        this.cdr = cdr;
    }
    validateHelper = new ValidatorHelper()
    abstract convertToPayload(cmpt: C): T;
    abstract create(): void;
    abstract update(): void;
    abstract errorMapper(original: ErrorMessage[], cmpt: C): ErrorMessage[];
    dismiss(event: MouseEvent) {
        this.bottomSheetRef.dismiss();
        event.preventDefault();
    }
    cleanUp() {
        Object.keys(this.subs).forEach(k => { this.subs[k].unsubscribe() })
        this.fis.resetAll();
    }
}