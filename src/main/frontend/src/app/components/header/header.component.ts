import {Component, OnInit} from '@angular/core';
import {AuthService} from "../../services/auth.service";
import {Router} from "@angular/router";
import {UserRoles} from "../../config";

@Component({
    selector: 'app-header',
    templateUrl: './header.component.html',
    styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit {

    constructor(public authService: AuthService, private router: Router) {
    }

    userRole: string = '';
    userRoles = UserRoles;

    ngOnInit(): void {
        this.authService.userRole.subscribe(role => {
            this.userRole = role;
        });
        this.authService.refreshLoggedUserDetails()
    }

    logout() {
        this.authService.logout();
        this.router.navigateByUrl('/products');
    }
}
