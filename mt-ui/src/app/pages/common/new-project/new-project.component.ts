import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormInfoService } from 'mt-form-builder';
import { Aggregate } from 'src/app/clazz/abstract-aggregate';
import { ICommonServerError } from 'src/app/clazz/common.interface';
import { IProjectSimple } from 'src/app/clazz/validation/aggregate/project/interface-project';
import { ProjectValidator } from 'src/app/clazz/validation/aggregate/project/validator-project';
import { ErrorMessage } from 'src/app/clazz/validation/validator-common';
import { FORM_CONFIG } from 'src/app/form-configs/new-project.config';
import { HttpProxyService } from 'src/app/services/http-proxy.service';
import { ProjectService } from 'src/app/services/project.service';

@Component({
  selector: 'app-new-project',
  templateUrl: './new-project.component.html',
  styleUrls: ['./new-project.component.css']
})
export class NewProjectComponent extends Aggregate<NewProjectComponent, IProjectSimple> implements OnInit {
  showNotes: boolean = false
  createLoading: boolean = false
  public systemError: boolean = false;
  constructor(
    private projectSvc: ProjectService,
    public httpProxySvc: HttpProxyService,
    fis: FormInfoService,
    cdr: ChangeDetectorRef
  ) {
    super('newProjectForm', JSON.parse(JSON.stringify(FORM_CONFIG)), new ProjectValidator('CLIENT'), undefined, { from: undefined, context: 'new', params: {} }, fis, cdr);
  }
  ngOnInit(): void {
  }
  convertToPayload(cmpt: NewProjectComponent): IProjectSimple {
    let formGroup = cmpt.fis.formGroups[cmpt.formId];
    return {
      name: formGroup.get('projectName').value,
      id: '',
      version: 0
    }
  }
  private count: number = 1;
  create(): void {
    if (!this.fis.formGroups[this.formId].get('projectName')) {
      return;
    }
    this.projectSvc.create(this.convertToPayload(this), this.changeId).subscribe(next => {
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
      //TODO below will not work until dirty check issue is fixed
      // console.dir("error")
      const errorMsg = ((error as HttpErrorResponse).error as ICommonServerError).errors[0]
      // console.dir(errorMsg)
      this.fis.formGroups_formInfo[this.formId].inputs.filter(e => e.key === 'projectName')[0].errorMsg = errorMsg;
      // console.dir(this.fis.formGroups_formInfo[this.formId])
      this.fis.update(this.formId)
      this.fis.$refresh.next()
      this.cdr.markForCheck()
      this.createLoading = false;
    })
  }
  update(): void {
    throw new Error('Method not implemented.');
  }
  errorMapper(original: ErrorMessage[], cmpt: NewProjectComponent): ErrorMessage[] {
    throw new Error('Method not implemented.');
  }
  dismiss() {
    this.showNotes = false;
  }
}
