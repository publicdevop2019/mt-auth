import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Logger } from 'src/app/misc/logger';

@Component({
  selector: 'app-image-upload',
  templateUrl: './image-upload.component.html',
  styleUrls: ['./image-upload.component.css']
})
export class ImageUploadComponent {
  @Input() value: string
  @Output() upload = new EventEmitter<FileList>();
  constructor() {
  }
  updateCtrl(files: FileList | undefined) {
    Logger.debugObj('update ctrl', files)
    this.upload.emit(files)
  }
  isString(str: string | FileList) {
    if (str === '')
      return false
    if (typeof str === 'string')
      return true
    return false
  }
}
