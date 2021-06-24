import {Injectable, OnDestroy, OnInit} from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {environment} from "../../environments/environment";
import {BehaviorSubject, Subject} from "rxjs";
import {catchError} from "rxjs/operators";
import {HttpErrorHandler} from "./http-error-handler";

const httpOptions = {
    headers: new HttpHeaders({'Content-Type': 'application/json'})
};

@Injectable({
    providedIn: 'root'
})
export class AuthService implements OnInit, OnDestroy {

    private loginUrl = environment.apiUrl + '/api/auth/login';
    private signupUrl = environment.apiUrl + '/api/auth/signup';

    userInfo: Subject<UserInfo> = new BehaviorSubject<UserInfo>(new UserInfo());

    signUpStatus: Subject<string> = new BehaviorSubject<string>('');
    loginStatus: Subject<string> = new BehaviorSubject<string>('');
    private loginStatusRefresher?: number;

    constructor(private http: HttpClient) {
    }

    public login(email: string, password: string): void {
        const credentials: AuthLoginInfo = new AuthLoginInfo(email, password);
        this.signUpStatus.next('');
        this.http.post<JwtResponse>(this.loginUrl, credentials, httpOptions)
            .pipe(catchError(HttpErrorHandler.handleError))
            // .pipe(shareReplay())
            .subscribe(
                (result: any) => {
                    this.saveAuthInStorage(result);
                    this.loginStatus.next('ok');
                },
                err => {
                    this.loginStatus.next(err);
                    this.logout();
                    // console.log(err);
                }
            );
    }

    public signUp(firstName: string, lastName: string, email: string, password: string): void {
        const signUpInfo = new SignUpInfo(firstName, lastName, email, password);
        this.http.post(this.signupUrl, signUpInfo, httpOptions).pipe(catchError(HttpErrorHandler.handleError))
            .subscribe(
                (result: any) => {
                    console.log(result);
                    this.signUpStatus.next('Registration successful. Check email to activate your account.')
                },
                err => {
                    this.signUpStatus.next(err);
                }
            );
    }

    public logout() {
        localStorage.removeItem("token");
        this.loginStatus.next('');
        this.refreshLoggedUserDetails();
    }

    private saveAuthInStorage(authResult: JwtResponse) {
        localStorage.setItem('token', JSON.stringify(authResult));
        this.refreshLoggedUserDetails();
    }

    public refreshLoggedUserDetails() {
        const token: JwtResponse = JSON.parse(<string>localStorage.getItem("token"));
        if (token && token.expiresAt && (new Date() < new Date(token.expiresAt))) {
            this.userInfo.next(new UserInfo(token.firstName, token.lastName, token.email, true));
        } else {
            this.userInfo.next(new UserInfo());
        }
    }

    ngOnInit(): void {
        this.loginStatusRefresher = setInterval(() => this.refreshLoggedUserDetails(), 10000);
    }

    ngOnDestroy(): void {
        clearInterval(this.loginStatusRefresher);
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
    email?: string;
    firstName?: string;
    lastName?: string
}

export class UserInfo {
    firstName: string;
    lastName: string;
    email: string;
    isLoggedIn: boolean;

    constructor(firstName?: string, lastName?: string, email?: string, isLoggedIn?: boolean) {
        this.firstName = firstName ? firstName : '';
        this.lastName = lastName ? lastName : '';
        this.email = email ? email : '';
        this.isLoggedIn = !!isLoggedIn;
    }
}
