import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { ProjectService } from 'src/app/services/project.service';
import { NewProjectComponent } from '../new-project/new-project.component';

@Component({
  selector: 'app-welcome',
  templateUrl: './welcome.component.html',
  styleUrls: ['./welcome.component.css']
})
export class WelcomeComponent implements OnInit {

  constructor(public projectSvc: ProjectService, private router: Router, public dialog: MatDialog) { }

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
  goToClient() {
    this.router.navigate(['home', this.projectSvc.viewProject.id, 'my-client']);
  }
  goToEndpoint() {
    this.router.navigate(['home', this.projectSvc.viewProject.id, 'my-api']);
  }
  openNewProject() {
    this.dialog.open(NewProjectComponent, { data: {} });
  }
}
