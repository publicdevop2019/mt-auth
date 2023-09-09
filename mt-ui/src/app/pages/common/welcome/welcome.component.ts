import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-welcome',
  templateUrl: './welcome.component.html',
  styleUrls: ['./welcome.component.css']
})
export class WelcomeComponent implements OnInit {

  constructor() { }

  ngOnInit(): void {
  }
  openGit(){
    window.open('https://github.com/publicdevop2019/mt-auth', '_blank').focus();
  }
  openGitee(){
    window.open('https://gitee.com/mirrors/MT-AUTH', '_blank').focus();
  }
}
