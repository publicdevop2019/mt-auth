import { Component } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { Validator } from 'src/app/misc/validator';
import { HttpProxyService } from 'src/app/services/http-proxy.service';
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
    name: new FormControl('MyApp_' + Utility.getRandomString().substring(0,3), []),
    type: new FormControl('BACKEND_APP', []),
  });
  constructor(
    public dialogRef: MatDialogRef<ClientCreateDialogComponent>,
    public httpProxySvc: HttpProxyService,
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
        name: this.createClientFormGroup.get('name').value,
        type: this.createClientFormGroup.get('type').value,
      })
    }
  }
}
