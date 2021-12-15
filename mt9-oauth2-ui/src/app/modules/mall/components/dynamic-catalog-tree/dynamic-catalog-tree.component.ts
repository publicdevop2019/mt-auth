import { Component, OnInit } from '@angular/core';
import { ICatalog } from 'src/app/clazz/validation/aggregate/catalog/interfaze-catalog';
import { CatalogService } from 'src/app/services/catalog.service';
import { HttpProxyService } from 'src/app/services/http-proxy.service';

@Component({
  selector: 'app-dynamic-catalog-tree',
  templateUrl: './dynamic-catalog-tree.component.html',
  styleUrls: ['./dynamic-catalog-tree.component.css']
})
export class DynamicCatalogTreeComponent implements OnInit {
  rootNodes: ICatalog[] = []
  constructor(private httpSvc: HttpProxyService, private catalogSvc: CatalogService) { }

  ngOnInit(): void {
    this.catalogSvc.readByQuery(0, 10, ",parentId:null").subscribe(next => {
      this.rootNodes = next.data;
    })
  }
 
}
