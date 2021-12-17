import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
export interface IEditBooleanEvent {
  original: boolean | undefined,
  next: boolean | undefined
}
@Component({
  selector: 'app-editable-boolean',
  templateUrl: './editable-boolean.component.html',
  styleUrls: ['./editable-boolean.component.css']
})
export class EditableBooleanComponent implements OnInit {
  @Input() inputValue: boolean = undefined;
  @Input() allowNull: boolean = false;
  @Output() newValue: EventEmitter<IEditBooleanEvent> = new EventEmitter();
  displayEdit = 'hidden';
  lockEditIcon = false;
  constructor() {
  }

  ngOnInit() {
  }
  showEditIcon() {
    this.displayEdit = 'visible'
  }
  hideEditIcon() {
    this.displayEdit = 'hidden'
  }
  doCancel() {
    this.displayEdit = 'hidden';
  }
  doUpdate(newValue: boolean | undefined) {
    this.newValue.emit({ original: this.inputValue, next: newValue });
    this.displayEdit = 'hidden';
  }
}
