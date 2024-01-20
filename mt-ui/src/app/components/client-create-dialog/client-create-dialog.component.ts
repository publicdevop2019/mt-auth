import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Validator } from 'src/app/misc/validator';
import { HttpProxyService } from 'src/app/services/http-proxy.service';
import { MyClientService } from 'src/app/services/my-client.service';
import { DialogData } from '../batch-update-cors/batch-update-cors.component';
import { ProjectService } from 'src/app/services/project.service';
import { FormControl, FormGroup } from '@angular/forms';
import { Utility } from 'src/app/misc/utility';

@Component({
  selector: 'app-client-create-dialog',
  templateUrl: './client-create-dialog.component.html',
  styleUrls: ['./client-create-dialog.component.css']
})
export class ClientCreateDialogComponent {
  allowError: boolean = false;
  nameErrorMsg: string = undefined;
  typeErrorMsg: string = undefined;
  createClientFormGroup = new FormGroup({
    name: new FormControl('我的应用' + Utility.getRandomString().substring(0,3), []),
    type: new FormControl('BACKEND_APP', []),
  });
  constructor(
    public dialogRef: MatDialogRef<ClientCreateDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: DialogData,
    public clientSvc: MyClientService,
    public httpProxySvc: HttpProxyService,
    private psv: ProjectService,
  ) {
    this.createClientFormGroup.valueChanges.subscribe(() => {
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
    const fg = this.createClientFormGroup;

    const var0 = Validator.exist(fg.get('name').value)
    this.nameErrorMsg = var0.errorMsg;

    const var1 = Validator.exist(fg.get('type').value)
    this.typeErrorMsg = var1.errorMsg;


    return !var0.errorMsg && !var1.errorMsg
  }
  create() {
    this.allowError = true;
    const result = this.validateCreateDialogForm()
    if (result) {
      this.dialogRef.close({
        projectId: this.psv.viewProject.id,
        name: this.createClientFormGroup.get('name').value,
        type: this.createClientFormGroup.get('type').value,
      })
    }
  }
}
