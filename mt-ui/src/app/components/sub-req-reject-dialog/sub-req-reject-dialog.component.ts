import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
export interface DialogData {
  reason: string;
}
@Component({
  selector: 'app-sub-req-reject-dialog',
  templateUrl: './sub-req-reject-dialog.component.html',
  styleUrls: ['./sub-req-reject-dialog.component.css']
})
export class SubReqRejectDialogComponent implements OnInit {

  constructor(public dialogRef: MatDialogRef<SubReqRejectDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: DialogData,) { }

  ngOnInit(): void {
  }
  onNoClick(): void {
    this.dialogRef.close();
  }
}
