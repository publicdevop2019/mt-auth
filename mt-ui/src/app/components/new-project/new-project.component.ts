import { HttpErrorResponse } from '@angular/common/http';
import { Component } from '@angular/core';
import { Logger } from 'src/app/misc/logger';
import { Utility } from 'src/app/misc/utility';
import { Validator } from 'src/app/misc/validator';
import { ICommonServerError, IProjectSimple } from 'src/app/misc/interface';
import { ProjectService } from 'src/app/services/project.service';
import { HttpProxyService } from 'src/app/services/http-proxy.service';
import { MatDialogRef } from '@angular/material/dialog';
import { FormControl, FormGroup } from '@angular/forms';
import { DeviceService } from 'src/app/services/device.service';

@Component({
  selector: 'app-new-project',
  templateUrl: './new-project.component.html',
  styleUrls: ['./new-project.component.css']
})
export class NewProjectComponent {
  allowError: boolean = false;
  changeId: string = Utility.getChangeId();
  showNotes: boolean = false
  createLoading: boolean = false
  nameErrorMsg: string = undefined
  public systemError: boolean = false;
  private count: number = 1;
  fg = new FormGroup({
    projectName: new FormControl('MyProject_' + Utility.getRandomString().substring(0, 3), []),
  });
  constructor(
    public dialogRef: MatDialogRef<NewProjectComponent>,
    private projectSvc: ProjectService,
    public deviceSvc: DeviceService,
    private httpProxy: HttpProxyService
  ) {
    this.fg.valueChanges.subscribe(() => {
      if (this.allowError) {
        this.validateForm()
      }
    })
  }
  create(): void {
    this.allowError = true;
    if (this.validateForm()) {
      Logger.trace('project name validation passed')
      const payload: IProjectSimple = {
        name: this.fg.get('projectName').value,
        id: '',
        version: 0
      }
      this.count = 0;
      this.showNotes = false;
      this.systemError = false
      this.disableUserInteraction()
      this.deviceSvc.operationCancelled.subscribe((_ => {
        this.enableUserInteraction()
      }))
      this.projectSvc.create(payload, this.changeId).subscribe(next => {
        let pull = setInterval(() => {
          this.projectSvc.ready(next).subscribe(next => {
            this.count++;
            if (next && next.status) {
              clearInterval(pull)
              this.showNotes = true;
              this.enableUserInteraction()
            }
            if (this.count === 6) {
              clearInterval(pull);
              this.systemError = true
              this.enableUserInteraction()
            }
          }, () => {
            this.count++;
            if (this.count === 6) {
              clearInterval(pull);
              this.systemError = true
              this.enableUserInteraction()
            }
          })

        }, 5000)
      }, error => {
        const errorMsg = ((error as HttpErrorResponse).error as ICommonServerError).errors[0]
        this.nameErrorMsg = errorMsg
        this.enableUserInteraction()
      }, () => {
        Logger.debug("complete")
      })
    }

  }
  private validateForm() {
    const var0 = Validator.exist(this.fg.get('projectName').value)
    this.nameErrorMsg = var0.errorMsg
    return !var0.errorMsg
  }
  doLogout() {
    Utility.logout(undefined, this.httpProxy)
  }
  dismiss(event: MouseEvent) {
    this.dialogRef.close();
    event.preventDefault();
  }
  disableUserInteraction() {
    this.fg.get('projectName').disable()
    this.createLoading = true;
  }
  enableUserInteraction() {
    this.fg.get('projectName').enable()
    this.createLoading = false;
  }
}
