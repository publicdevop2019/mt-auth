import { Component, Inject } from '@angular/core';
import { FormGroup, FormControl } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { FormInfoService } from 'mt-form-builder';
import { Utility } from 'src/app/misc/utility';
import { Validator } from 'src/app/misc/validator';
import { HttpProxyService } from 'src/app/services/http-proxy.service';
import { MyClientService } from 'src/app/services/my-client.service';
import { ProjectService } from 'src/app/services/project.service';
import { DialogData } from '../batch-update-cors/batch-update-cors.component';

@Component({
  selector: 'app-role-create-dialog',
  templateUrl: './role-create-dialog.component.html',
  styleUrls: ['./role-create-dialog.component.css']
})
export class RoleCreateDialogComponent {
  allowError: boolean = false;
  nameErrorMsg: string = undefined;
  fg = new FormGroup({
    name: new FormControl('我的角色' + Utility.getRandomString().substring(0, 3), []),
    description: new FormControl('', []),
  });
  constructor(
    public dialogRef: MatDialogRef<RoleCreateDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: DialogData,
    private psv: ProjectService,
  ) {
    this.fg.valueChanges.subscribe(() => {
      if (this.allowError) {
        this.validateCreateDialogForm()
      }
    })
  }
  dismiss(event: MouseEvent) {
    this.dialogRef.close();
    event.preventDefault();
  }
  private validateCreateDialogForm() {
    const fg = this.fg;
    const var0 = Validator.exist(fg.get('name').value)
    this.nameErrorMsg = var0.errorMsg;
    return !var0.errorMsg
  }
  create() {
    this.allowError = true;
    const result = this.validateCreateDialogForm()
    if (result) {
      this.dialogRef.close({
        projectId: this.psv.viewProject.id,
        name: this.fg.get('name').value,
        description: this.fg.get('description').value,
      })
    }
  }
}
