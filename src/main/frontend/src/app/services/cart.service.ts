import {Injectable} from '@angular/core';
import {CartItem} from "../model/CartItem";
import {Subject} from "rxjs";

@Injectable({
    providedIn: 'root'
})
export class CartService {

    cartItems: CartItem[] = [];
    totalPrice: Subject<number> = new Subject<number>();
    totalQuantity: Subject<number> = new Subject<number>();

    constructor() {
    }

    addToCart(cartItem: CartItem) {

        let existingCart = this.cartItems.find(item => item.id === cartItem.id);
        if (existingCart) {
            existingCart.quantity += cartItem.quantity;
        } else {
            this.cartItems.push(cartItem);
        }
        this.computeCartTotals();

    }

    computeCartTotals() {

        let totalQuantity: number = 0;
        let totalPrice: number = 0;

        this.cartItems.forEach((item: CartItem) => {
            totalQuantity += item.quantity;
            totalPrice += item.quantity * item.unitPrice;
        })
        this.totalQuantity.next(totalQuantity);
        this.totalPrice.next(totalPrice);

    }

    removeItem(cartItem: CartItem) {
        let existingCart = this.cartItems.find(item => item.id === cartItem.id);
        if (existingCart) {
            existingCart.quantity -= 1;
            if (existingCart.quantity < 1) {
                let index = this.cartItems.indexOf(cartItem);
                this.cartItems.splice(index, 1);
            }
        }
        this.computeCartTotals();
    }
}

