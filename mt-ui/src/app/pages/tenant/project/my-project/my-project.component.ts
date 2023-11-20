import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs';
import { IProjectDashboard } from 'src/app/misc/interface';
import { HttpProxyService } from 'src/app/services/http-proxy.service';
import { ProjectService } from 'src/app/services/project.service';
import { RouterWrapperService } from 'src/app/services/router-wrapper';
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
  private subs: Subscription = new Subscription();
  constructor(
    private projectSvc: ProjectService,
    public httpProxySvc: HttpProxyService,
    public router: RouterWrapperService,
  ) {
    this.projectSvc.getMyProject(this.router.getProjectId()).subscribe(next => {
      this.data = next;
    })
  }

  ngOnDestroy(): void {
    this.subs.unsubscribe()
  }
}
