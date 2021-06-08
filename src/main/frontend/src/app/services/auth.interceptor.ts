import {Injectable} from '@angular/core';
import {HTTP_INTERCEPTORS, HttpHandler, HttpInterceptor, HttpRequest} from '@angular/common/http';
import {JwtResponse} from "./auth.service";

const TOKEN_HEADER_KEY = 'Authorization';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {

    constructor() {
    }

    intercept(request: HttpRequest<unknown>, next: HttpHandler) {
        let authRequest = request;
        const jwtResponse: JwtResponse = JSON.parse(<string>localStorage.getItem("token"));
        if (jwtResponse) {
            const token = jwtResponse.token;
            authRequest = request.clone(
                {headers: request.headers.set(TOKEN_HEADER_KEY, 'Bearer ' + token)});
        }
        return next.handle(authRequest);

    }
}

export const httpInterceptorProviders = [
    {provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true}
]