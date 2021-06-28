import {Injectable, OnInit} from '@angular/core';
import {Observable} from "rxjs";
import {catchError} from "rxjs/operators";
import {HttpErrorHandler} from "./http-error-handler";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {environment} from "../../environments/environment";
import {Order} from "../model/Order";
import {OrderDetails} from "../model/OrderDetails";

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

    public getAccountOrders(): Observable<Order[]> {
        return this.http.get<Order[]>(this.orderUrl, httpOptions)
            .pipe(catchError(HttpErrorHandler.handleError));
    }

    public getOrderDetailsById(id: number): Observable<OrderDetails> {
        return this.http.get<OrderDetails>(`${this.orderUrl}/${id}`)
            .pipe(catchError(HttpErrorHandler.handleError));
    }

}

