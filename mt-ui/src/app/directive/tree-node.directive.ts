import { ViewContainerRef } from '@angular/core';
import { Directive } from '@angular/core';

@Directive({
  selector: '[appTreeNode]'
})
export class TreeNodeDirective {

  constructor(public viewContainerRef: ViewContainerRef) { }

}
