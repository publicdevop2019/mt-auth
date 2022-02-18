import { ChangeDetectorRef, Component, ComponentFactoryResolver, Input, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { Observable } from 'rxjs';
import { ISumRep } from 'src/app/clazz/summary.component';
import { TreeNodeDirective } from '../../../directive/tree-node.directive';
import { INode } from '../../tree/tree.component';

@Component({
  selector: 'app-dynamic-node',
  templateUrl: './dynamic-node.component.html',
  styleUrls: ['./dynamic-node.component.css']
})
export class DynamicNodeComponent implements OnInit {
  @Input() node: INode;
  @Input() flatNodes: INode[];
  @Input() loadChildren: (id: string) => Observable<ISumRep<INode>>;
  @Input() fg?: FormGroup;
  @ViewChild(TreeNodeDirective, { static: false }) treeNodeHost: TreeNodeDirective;
  @Input() edit: boolean = false
  expanded: boolean = false
  loaded: boolean = false
  level: number = 0
  constructor(private componentFactoryResolver: ComponentFactoryResolver, private cdr: ChangeDetectorRef) { }

  ngOnInit(): void {
    if (this.fg) {
      const ctrl = new FormControl('unchecked')
      this.fg.addControl(this.node.id, ctrl)
      ctrl.valueChanges.subscribe(next => {
        if (next === 'checked') {
          //auto select child nodes
          const children = this.flatNodes.filter(e => e.parentId === this.node.id)
          children.forEach(e => {
            if (this.fg.get(e.id).value !== 'checked') {
              this.fg.get(e.id).setValue('checked')
            }
          })
          //find parent and check if all children are selected
          if (this.node.parentId) {
            const sibling = this.flatNodes.filter(e => e.parentId === this.node.parentId);
            const allSiblingSelected = !sibling.some(e => this.fg.get(e.id).value !== 'checked')
            if (allSiblingSelected) {
              if (this.fg.get(this.node.parentId).value !== 'checked') {
                this.fg.get(this.node.parentId).setValue('checked')
              }
            } else {
              if (this.fg.get(this.node.parentId).value !== 'indeterminate') {
                this.fg.get(this.node.parentId).setValue('indeterminate')
              }
            }
          }
        } else if (next === 'unchecked') {
          const children = this.flatNodes.filter(e => e.parentId === this.node.id)
          children.forEach(e => {
            if (this.fg.get(e.id).value !== 'unchecked') {
              this.fg.get(e.id).setValue('unchecked')
            }
          })
          if (this.node.parentId) {
            //find parent and check if all children are selected
            const sibling = this.flatNodes.filter(e => e.parentId === this.node.parentId);
            const allSiblingUnselected = sibling.every(e => this.fg.get(e.id).value === 'unchecked')
            this.cdr.detectChanges()
            if (allSiblingUnselected) {
              if (this.fg.get(this.node.parentId).value !== 'unchecked') {
                this.fg.get(this.node.parentId).setValue('unchecked')
                // this.fg.get(this.node.parentId).setValue('unchecked', { emitEvent: false })
              }
            } else {
              if (this.fg.get(this.node.parentId).value !== 'indeterminate') {
                this.fg.get(this.node.parentId).setValue('indeterminate')
              }

            }
          }
        } else {
          //do nothing
        }
        this.cdr.markForCheck()
      })
    }
  }

  onClick(id: string, event: MouseEvent) {
    if (!this.expanded && !this.loaded) {
      event.preventDefault();
      this.loadChildren(id).subscribe(next => {
        this.loaded = true
        next.data.forEach(e => {
          const componentFactory = this.componentFactoryResolver.resolveComponentFactory(DynamicNodeComponent);
          const viewContainerRef = this.treeNodeHost.viewContainerRef;
          const componentRef = viewContainerRef.createComponent<DynamicNodeComponent>(componentFactory);
          componentRef.instance.node = e;
          componentRef.instance.loadChildren = this.loadChildren;
          componentRef.instance.level = this.level + 1;
          componentRef.instance.fg = this.fg;
          componentRef.instance.flatNodes = this.flatNodes;
          componentRef.instance.edit = this.edit;
        })
        next.data.forEach(e => {
          this.flatNodes.push(e)
        })
        this.cdr.markForCheck();
      })
    }
    this.expanded = (!this.expanded);
  }
  toggle() {
    const value = this.fg.get(this.node.id).value;
    const next = value === 'unchecked' ? 'checked' : (value === 'indeterminate' ? 'checked' : 'unchecked')
    this.fg.get(this.node.id).setValue(next)
  }
  checked() {
    return this.fg.get(this.node.id).value === 'checked'
  }
}
