import {Component, OnInit} from '@angular/core';
import {AuthService, UserInfo} from "../../services/auth.service";
import {ActivatedRoute, Router} from "@angular/router";

@Component({
    selector: 'app-account',
    templateUrl: './account.component.html',
    styleUrls: ['./account.component.css']
})
export class AccountComponent implements OnInit {

    userInfo?: UserInfo;

    constructor(private authService: AuthService,
                private router: Router,
                private route: ActivatedRoute) {
    }

    ngOnInit(): void {
        this.router.navigate(['orders'], {relativeTo: this.route});
        this.authService.userInfo.subscribe(info => {
            this.userInfo = info;
        });
    }
}

