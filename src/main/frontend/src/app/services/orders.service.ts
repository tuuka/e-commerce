import {Injectable, OnInit} from '@angular/core';
import {Observable} from "rxjs";
import {catchError} from "rxjs/operators";
import {HttpErrorHandler} from "./http-error-handler";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {environment} from "../../environments/environment";
import {OrderItem} from "../model/OrderItem";

const httpOptions = {
    headers: new HttpHeaders({'Content-Type': 'application/json'})
};

@Injectable({
    providedIn: 'root'
})
export class OrdersService implements OnInit{

    private orderUrl = environment.apiUrl + '/api/orders';

    constructor(private http: HttpClient) {
    }

    ngOnInit(): void {
    }

    public getAccountOrders(): Observable<OrderItem[]> {
        return this.http.get<OrderItem[]>(this.orderUrl, httpOptions)
            .pipe(catchError(HttpErrorHandler.handleError));
    }


}

