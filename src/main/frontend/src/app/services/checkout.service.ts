import {Injectable} from '@angular/core';
import {Observable, of} from "rxjs";
import {CartItem} from "../model/CartItem";
import {catchError} from "rxjs/operators";
import {HttpErrorHandler} from "./http-error-handler";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {environment} from "../../environments/environment";

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

    public sendOrder(order: OrderFormDto): void {
        this.http.post(this.orderUrl, order, httpOptions)
            .pipe(catchError(HttpErrorHandler.handleError))
            // .pipe(shareReplay())
            .subscribe(
                (result: any) => {
                    console.log(result);
                },
                err => {
                    console.log(err);
                }
            );
    }

}

export class OrderFormDto {
    email: string;
    creditCard: Card;
    shippingAddress: Address;
    cartItems: CartItem[];


    constructor(email: string, card: Card, shippingAddress: Address, cartItems: CartItem[]) {
        this.email = email;
        this.creditCard = card;
        this.shippingAddress = shippingAddress;
        this.cartItems = cartItems;
    }
}

interface Card {
    type: string;
    number: string;
    code: string;
    expMonth: string;
    expYear: string;
}

interface Address {
    apartment: string;
    street: string;
    city: string;
    country: string;
    state: string;
    zip: string;
}