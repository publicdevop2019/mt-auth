import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs';
import { IProjectDashboard, IProjectSimple } from 'src/app/clazz/project.interface';
import { HttpProxyService } from 'src/app/services/http-proxy.service';
import { ProjectService } from 'src/app/services/project.service';
export interface IMyDashboardInfo {
  totalClients: number;
  totalEndpoint: number;
  totalUser: number;
  totalPermissionCreated: number;
  totalRole: number;
}
@Component({
  selector: 'app-my-project',
  templateUrl: './my-project.component.html',
  styleUrls: ['./my-project.component.css']
})
export class MyProjectComponent implements OnDestroy {
  data: IProjectDashboard;
  public projectId: string;
  private subs: Subscription = new Subscription();
  constructor(
    private projectSvc: ProjectService,
    public httpProxySvc: HttpProxyService,
    private route: ActivatedRoute,
  ) {
    const sub = this.route.paramMap.subscribe(queryMaps => {
      this.projectId = queryMaps.get('id')
      this.projectSvc.getMyProject(this.projectId).subscribe(next => {
        this.data = next;
      })
    });
    this.subs.add(sub)
  }

  ngOnDestroy(): void {
    this.subs.unsubscribe()
  }
}
