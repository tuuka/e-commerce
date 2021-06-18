import {Injectable} from '@angular/core';
import {environment} from "../../environments/environment";
import {HttpClient} from "@angular/common/http";
import {AccountDetail} from "../components/account/account.component";

@Injectable({
    providedIn: 'root'
})
export class AccountService {

    private accountUrl = environment.apiUrl + '/api/account';

    constructor(private httpClient: HttpClient) {
    }

    getAccountDetail() {
        return this.httpClient.get<AccountDetail>(this.accountUrl);
    }

}
