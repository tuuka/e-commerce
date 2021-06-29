import {Injectable, OnDestroy, OnInit} from '@angular/core';
import {HttpClient, HttpHeaders, HttpParams} from "@angular/common/http";
import {environment} from "../../environments/environment";
import {BehaviorSubject, Observable, Subject} from "rxjs";
import {catchError} from "rxjs/operators";
import {HttpErrorHandler} from "./http-error-handler";
import {UserRoles} from "../config";
import {UserDetails} from "../model/user-details";
import {Router} from "@angular/router";

const httpOptions = {
    headers: new HttpHeaders({'Content-Type': 'application/json'})
};

@Injectable({
    providedIn: 'root'
})
export class AuthService implements OnInit, OnDestroy {

    private loginUrl = environment.apiUrl + '/api/auth/login';
    private signupUrl = environment.apiUrl + '/api/auth/signup';
    private accountUrl = environment.apiUrl + '/api/users';

    userInfo: Subject<UserInfo> = new BehaviorSubject<UserInfo>(new UserInfo());
    userRole: Subject<string> = new BehaviorSubject<string>('');

    // messages to display after login/registration
    signUpStatus: Subject<string> = new BehaviorSubject<string>('');
    loginStatus: Subject<string> = new BehaviorSubject<string>('');

    // for setInterval
    private loginStatusRefresher?: number;

    constructor(private http: HttpClient, private router: Router) {
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
                () => {
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
        this.router.navigateByUrl('/');
    }

    public getAccountDetail(email: string): Observable<UserDetails> {
        return this.http.get<UserDetails>(this.accountUrl,
            {params: new HttpParams().set('email', email)});
    }

    public getAccounts(): Observable<UserDetails[]> {
        return this.http.get<UserDetails[]>(this.accountUrl);
    }

    private saveAuthInStorage(authResult: JwtResponse) {
        localStorage.setItem('token', JSON.stringify(authResult));
        this.refreshLoggedUserDetails();
    }

    public refreshLoggedUserDetails() {
        const token: JwtResponse = JSON.parse(<string>localStorage.getItem("token"));

        // if token expired remove it and redirect at home page
        if (token && token.expiresAt && (new Date() > new Date(token.expiresAt))) {
            localStorage.removeItem("token");
            this.router.navigateByUrl('/');
            return;
        }
        if (token) {
            let roles = token.authorities ? token.authorities.reduce((acc: string[], auth) => {
                acc.push(auth.toLowerCase().replace("role_", ""));
                return acc;
            }, []) : []

            // assign 'most significant' role (less index - more significant)
            for (let r of UserRoles) {
                if (roles.includes(r)) {
                    this.userInfo.next(new UserInfo(token.firstName, token.lastName, token.email, roles, true));
                    this.userRole.next(r);
                    return;
                }
            }
        }
        this.userInfo.next(new UserInfo());
        this.userRole.next('');
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
    authorities?: string[];
    firstName?: string;
    lastName?: string
}

export class UserInfo {
    firstName: string;
    lastName: string;
    email: string;
    roles: string[];
    isLoggedIn: boolean;

    constructor(firstName?: string, lastName?: string, email?: string, roles?: string[], isLoggedIn?: boolean) {
        this.firstName = firstName ? firstName : '';
        this.lastName = lastName ? lastName : '';
        this.email = email ? email : '';
        this.roles = roles ? roles : [];
        this.isLoggedIn = !!isLoggedIn;
    }
}
