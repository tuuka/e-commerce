import {Injectable} from '@angular/core';
import {Observable, of} from "rxjs";
import {catchError} from "rxjs/operators";
import {HttpErrorHandler} from "./http-error-handler";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {environment} from "../../environments/environment";
import {Purchase} from "../model/Purchase";

const httpOptions = {
    headers: new HttpHeaders({'Content-Type': 'application/json'})
};

@Injectable({
    providedIn: 'root'
})
export class CheckoutService {

    private orderUrl = environment.apiUrl + '/api/orders';

    constructor(private http: HttpClient) {
    }

    getCreditCardMonths(): Observable<number[]> {
        let data: number[] = [];
        for (let i = 1; i <= 12; i++) {
            data.push(i)
        }
        return of(data);
    }

    getCreditCardYears(): Observable<number[]> {
        let data: number[] = [];
        const startYear = new Date().getFullYear();
        const endYear = startYear + 10;
        for (let i = startYear; i <= endYear; i++) {
            data.push(i)
        }
        return of(data);
    }

    public sendOrder(order: Purchase): Observable<any> {
        return this.http.post(this.orderUrl, order, httpOptions)
            .pipe(catchError(HttpErrorHandler.handleError));
    }

}

