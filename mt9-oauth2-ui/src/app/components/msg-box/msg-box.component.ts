import { Component, OnInit, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'app-msg-box',
  templateUrl: './msg-box.component.html',
  styleUrls: ['./msg-box.component.css']
})
export class MsgBoxComponent {

  constructor(
    public dialogRef: MatDialogRef<MsgBoxComponent>,
    @Inject(MAT_DIALOG_DATA) public data: string) { }

  onNoClick(): void {
    this.dialogRef.close();
  }

}
