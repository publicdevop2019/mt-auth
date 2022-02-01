import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { take } from 'rxjs/operators';
import { IProject } from 'src/app/clazz/validation/aggregate/project/interface-project';
import { HttpProxyService } from 'src/app/services/http-proxy.service';
import { ProjectService } from 'src/app/services/project.service';

@Component({
  selector: 'app-my-project',
  templateUrl: './my-project.component.html',
  styleUrls: ['./my-project.component.css']
})
export class MyProjectComponent implements OnInit {
  data: IProject;
  public projectId: string;
  constructor(
    private projectSvc: ProjectService,
    public httpProxySvc: HttpProxyService,
    private route: ActivatedRoute,
  ) {
    this.route.paramMap.pipe(take(1)).subscribe(queryMaps => {
      this.projectId = queryMaps.get('id')
      this.projectSvc.queryPrefix = 'id:'+this.projectId;
    });
  }
  ngOnInit(): void {
    //find my project
    this.projectSvc.readEntityByQuery(0,1).subscribe(next => {
      this.data = next.data[0];
    })
  }
}
