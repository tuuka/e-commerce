import {Component, OnInit} from '@angular/core';
import {AccountService} from "../../services/account.service";

@Component({
    selector: 'app-account',
    templateUrl: './account.component.html',
    styleUrls: ['./account.component.css']
})
export class AccountComponent implements OnInit {

    accountDetail?: AccountDetail

    constructor(private accountService: AccountService) {
    }

    ngOnInit(): void {
        this.getAccountDetail();
    }

    private getAccountDetail() {
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

