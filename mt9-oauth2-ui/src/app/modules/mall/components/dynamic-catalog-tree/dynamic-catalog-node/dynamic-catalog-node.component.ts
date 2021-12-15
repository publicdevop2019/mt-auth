import { ComponentFactoryResolver } from '@angular/core';
import { Component, Input, OnInit, ViewChild } from '@angular/core';
import { ICatalog } from 'src/app/clazz/validation/aggregate/catalog/interfaze-catalog';
import { CatalogService } from 'src/app/services/catalog.service';
import { TreeNodeDirective } from '../../../directive/tree-node.directive';

@Component({
  selector: 'app-dynamic-catalog-node',
  templateUrl: './dynamic-catalog-node.component.html',
  styleUrls: ['./dynamic-catalog-node.component.css']
})
export class DynamicCatalogNodeComponent implements OnInit {
  @Input() node: ICatalog;
  @ViewChild(TreeNodeDirective, {static: true}) treeNodeHost: TreeNodeDirective;
  constructor(private catalogSvc: CatalogService,private componentFactoryResolver: ComponentFactoryResolver) { }

  ngOnInit(): void {
  }

  loadChildren(id: string,event:MouseEvent) {
    console.dir('clicking '+id)
    event.preventDefault();
    this.catalogSvc.readByQuery(0, 10, ",parentId:"+id).subscribe(next => {
      next.data.forEach(e=>{
        const componentFactory = this.componentFactoryResolver.resolveComponentFactory(DynamicCatalogNodeComponent);
        const viewContainerRef = this.treeNodeHost.viewContainerRef;
        const componentRef = viewContainerRef.createComponent<DynamicCatalogNodeComponent>(componentFactory);
        componentRef.instance.node = e;
      })
    })
  }
}
