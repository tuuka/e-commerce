import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {environment} from "../../environments/environment";
import {Observable} from "rxjs";
import {shareReplay} from "rxjs/operators";
import * as moment from "moment";

const httpOptions = {
    headers: new HttpHeaders({'Content-Type': 'application/json'})
};

@Injectable({
    providedIn: 'root'
})
export class AuthService {

    private loginUrl = environment.apiUrl + '/api/auth/login';
    private signupUrl = environment.apiUrl + '/api/auth/signup';

    constructor(private http: HttpClient) {
    }

    login(credentials: AuthLoginInfo): Observable<JwtResponse> {
        return this.http.post(this.loginUrl, credentials, httpOptions)
            .pipe(shareReplay())
    }

    signUp(info: SignUpInfo): Observable<any> {
        return this.http.post(this.signupUrl, info, httpOptions);
    }

    setSession(authResult: JwtResponse) {
        const expiresAt = moment().add(authResult.expiresInMin, 'minutes');

        localStorage.setItem('id_token', <string>authResult.token);
        localStorage.setItem("expires_at", JSON.stringify(expiresAt.valueOf()));
    }

    logout() {
        localStorage.removeItem("id_token");
        localStorage.removeItem("expires_at");
    }

    public isLoggedIn() {
        const expiration = localStorage.getItem("expires_at");
        if (expiration == null) return false;
        const expiresAt = JSON.parse(expiration);

        return moment().isBefore(moment(expiresAt));
    }

    isLoggedOut() {
        return !this.isLoggedIn();
    }
}

class SignUpInfo {
    name?: string;
    username?: string;
    email?: string;
    password?: string;
}

export class AuthLoginInfo {
    username: string;
    password: string;


    constructor(username: string, password: string) {
        this.username = username;
        this.password = password;
    }
}

class JwtResponse {
    token?: string;
    type?: string;
    expiresInMin?: number;
}
