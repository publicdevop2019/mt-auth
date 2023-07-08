import { Component, Input } from '@angular/core';
@Component({
  selector: 'app-table-column-config',
  templateUrl: './table-column-config.component.html',
  styleUrls: ['./table-column-config.component.css']
})
export class TableColumnConfigComponent {
  @Input() formId: string = '';
  constructor() { }

  handleClick($event: Event) {
    $event.stopPropagation()//prevent menu from close
  }

}
