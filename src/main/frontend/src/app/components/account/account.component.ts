import {Component, OnInit} from '@angular/core';
import {AccountService} from "../../services/account.service";
import {AuthService} from "../../services/auth.service";
import {Router} from "@angular/router";

@Component({
    selector: 'app-account',
    templateUrl: './account.component.html',
    styleUrls: ['./account.component.css']
})
export class AccountComponent implements OnInit {

    accountDetail?: AccountDetail

    constructor(private accountService: AccountService,
                private authService: AuthService,
                private router: Router) {
    }

    ngOnInit(): void {
        this.getAccountDetail();
    }

    private getAccountDetail() {
        if (!this.authService.checkIfLoggedIn())
            this.router.navigateByUrl('/auth')
        this.accountService.getAccountDetail().subscribe(
            data => {
                this.accountDetail = data;
            }
        )
    }
}

export interface AccountDetail {
    firstName: string;
    lastName: string;
    email: string;
    userAuthorities: string[]

}

