import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnDestroy } from '@angular/core';
import { FormInfoService } from 'mt-form-builder';
import { ICommonServerError } from 'src/app/clazz/common.interface';
import { Logger, Utility } from 'src/app/clazz/utility';
import { IProjectSimple } from 'src/app/clazz/project.interface';
import { Validator } from 'src/app/clazz/validator-next-common';
import { FORM_CONFIG } from 'src/app/form-configs/new-project.config';
import { ProjectService } from 'src/app/services/project.service';

@Component({
  selector: 'app-new-project',
  templateUrl: './new-project.component.html',
  styleUrls: ['./new-project.component.css']
})
export class NewProjectComponent implements OnDestroy {
  formId: string;
  allowError: boolean = false;
  changeId: string = Utility.getChangeId();
  showNotes: boolean = false
  createLoading: boolean = false
  public systemError: boolean = false;
  private count: number = 1;
  constructor(
    private projectSvc: ProjectService,
    public fis: FormInfoService,
  ) {
    this.fis.init(FORM_CONFIG, this.formId);
    this.fis.formGroups[this.formId].valueChanges.subscribe(() => {
      if (this.allowError) {
        this.validateForm()
      }
    })
  }
  ngOnDestroy(): void {
    this.fis.reset(this.formId)
  }
  create(): void {
    this.allowError = true;
    if (this.validateForm()) {
      Logger.trace('project name validation passed')
      const formGroup = this.fis.formGroups[this.formId];
      const payload: IProjectSimple = {
        name: formGroup.get('projectName').value,
        id: '',
        version: 0
      }
      this.projectSvc.create(payload, this.changeId).subscribe(next => {
        this.createLoading = true;
        let pull = setInterval(() => {
          this.projectSvc.ready(next).subscribe(next => {
            this.count++;
            if (next && next.status) {
              clearInterval(pull)
              this.showNotes = true;
              this.createLoading = false;
            }
            if (this.count === 6) {
              clearInterval(pull);
              this.systemError = true
              this.createLoading = false;
            }
          }, () => {
            this.count++;
            if (this.count === 6) {
              clearInterval(pull);
              this.systemError = true
              this.createLoading = false;
            }
          })

        }, 5000)
      }, error => {
        const errorMsg = ((error as HttpErrorResponse).error as ICommonServerError).errors[0]
        this.fis.updateError(this.formId, FORM_CONFIG.inputs[0].key, errorMsg);
        this.createLoading = false;
      })
    }

  }
  private validateForm() {
    const var0 = Validator.exist(this.fis.formGroups[this.formId].get('projectName').value)
    this.fis.updateError(this.formId, 'projectName', var0.errorMsg)
    return !var0.errorMsg
  }
  dismiss() {
    this.showNotes = false;
  }
}
