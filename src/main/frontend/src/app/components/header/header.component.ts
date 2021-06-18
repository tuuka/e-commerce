import {Component, OnInit} from '@angular/core';
import {AuthService} from "../../services/auth.service";
import {Router} from "@angular/router";

@Component({
    selector: 'app-header',
    templateUrl: './header.component.html',
    styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit {

    constructor(public authService: AuthService, private router: Router) {
    }

    isLoggedIn: boolean = false;

    ngOnInit(): void {
        this.authService.userInfo.subscribe(info => {
            this.isLoggedIn = info.isLoggedIn;
        });
        this.authService.refreshLoggedUserDetails()
    }

    logout() {
        this.authService.logout();
        this.router.navigateByUrl('/products');
    }
}
