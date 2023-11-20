import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { ProjectService } from 'src/app/services/project.service';
import { NewProjectComponent } from '../new-project/new-project.component';
import { RouterWrapperService } from 'src/app/services/router-wrapper';

@Component({
  selector: 'app-welcome',
  templateUrl: './welcome.component.html',
  styleUrls: ['./welcome.component.css']
})
export class WelcomeComponent implements OnInit {

  constructor(public projectSvc: ProjectService, 
    public router: RouterWrapperService, public dialog: MatDialog) { }

  ngOnInit(): void {
  }
  openGit() {
    window.open('https://github.com/publicdevop2019/mt-auth', '_blank').focus();
  }
  openGitee() {
    window.open('https://gitee.com/mirrors/MT-AUTH', '_blank').focus();
  }
  hasProject() {
    return this.projectSvc.totalProjects.length > 0
  }
  openNewProject() {
    this.dialog.open(NewProjectComponent, { data: {} });
  }
}
