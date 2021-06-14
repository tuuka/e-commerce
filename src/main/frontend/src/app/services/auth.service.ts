import {Injectable, OnInit} from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {environment} from "../../environments/environment";
import {Observable} from "rxjs";

const httpOptions = {
    headers: new HttpHeaders({'Content-Type': 'application/json'})
};

@Injectable({
    providedIn: 'root'
})
export class AuthService implements OnInit {

    private loginUrl = environment.apiUrl + '/api/auth/login';
    private signupUrl = environment.apiUrl + '/api/auth/signup';
    isLoggedIn?: boolean;

    constructor(private http: HttpClient) {
    }

    login(credentials: AuthLoginInfo): Observable<JwtResponse> {
        return this.http.post(this.loginUrl, credentials, httpOptions)
        // .pipe(shareReplay())
    }

    signUp(info: SignUpInfo): Observable<any> {
        return this.http.post(this.signupUrl, info, httpOptions);
    }

    setSession(authResult: JwtResponse) {
        localStorage.setItem('token', JSON.stringify(authResult));
        this.checkIfLoggedIn();
    }

    logout() {
        localStorage.removeItem("token");
        this.isLoggedIn = false;
    }

    public checkIfLoggedIn() {
        const token: JwtResponse = JSON.parse(<string>localStorage.getItem("token"));
        if (!token || !token.expiresAt) {
            this.isLoggedIn = false;
            return false;
        }

        this.isLoggedIn = new Date() < new Date(token.expiresAt);
        return this.isLoggedIn;
    }

    public getUsername(): string {
        if (this.checkIfLoggedIn()) {
            const token: JwtResponse = JSON.parse(<string>localStorage.getItem("token"));
            return <string>token.username;
        }
        return environment.anonymousName;
    }

    ngOnInit(): void {
        this.checkIfLoggedIn();
    }

}

export class SignUpInfo {
    firstName?: string;
    lastName?: string;
    email?: string;
    password?: string;

    constructor(firstName: string, lastName: string, email: string, password: string) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }
}

export class AuthLoginInfo {
    username: string;
    password: string;


    constructor(username: string, password: string) {
        this.username = username;
        this.password = password;
    }
}

export class JwtResponse {
    token?: string;
    type?: string;
    expiresAt?: number;
    username?: string;
}
