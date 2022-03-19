import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormInfoService } from 'mt-form-builder';
import { Aggregate } from 'src/app/clazz/abstract-aggregate';
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
  showNotes:boolean=false
  constructor(
    private projectSvc: ProjectService,
    public httpProxySvc: HttpProxyService,
    fis: FormInfoService,
    cdr: ChangeDetectorRef
  ) {
    super('newProjectForm', JSON.parse(JSON.stringify(FORM_CONFIG)), new ProjectValidator('CLIENT'), undefined, {from:undefined,context:'new',params:{}}, fis, cdr);
  }
  ngOnInit(): void {
  }
  convertToPayload(cmpt: NewProjectComponent): IProjectSimple {
    let formGroup = cmpt.fis.formGroupCollection[cmpt.formId];
    return {
      name: formGroup.get('projectName').value,
      id: '',
      version: 0
    }
  }
  create(): void {
    this.projectSvc.create(this.convertToPayload(this), this.changeId).subscribe(next=>{
      this.showNotes=true;
    })
  }
  update(): void {
    throw new Error('Method not implemented.');
  }
  errorMapper(original: ErrorMessage[], cmpt: NewProjectComponent): ErrorMessage[] {
    throw new Error('Method not implemented.');
  }
  dismiss(){
    this.showNotes=false;
  }
}
