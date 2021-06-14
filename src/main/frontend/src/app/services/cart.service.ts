import {Injectable, OnInit} from '@angular/core';
import {CartItem} from "../model/CartItem";
import {Subject} from "rxjs";
import {AuthService} from "./auth.service";

@Injectable({
    providedIn: 'root'
})
export class CartService implements OnInit {

    cartItems: CartItem[] = [];
    totalPrice: Subject<number> = new Subject<number>();
    totalQuantity: Subject<number> = new Subject<number>();

    constructor(private authService: AuthService) {
    }

    addToCart(cartItem: CartItem) {

        let existingCart = this.findExistingItem(cartItem);
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
        this.setStorage();
        this.totalQuantity.next(totalQuantity);
        this.totalPrice.next(totalPrice);

    }

    decrementQuantity(cartItem: CartItem) {
        let existingCart = this.findExistingItem(cartItem);
        if (existingCart) {
            existingCart.quantity -= 1;
            if (existingCart.quantity < 1) this.removeItem(cartItem);
        }
        this.computeCartTotals();
    }

    incrementQuantity(cartItem: CartItem) {
        let existingCart = this.findExistingItem(cartItem);
        if (existingCart) existingCart.quantity += 1;
        this.computeCartTotals();
    }

    private findExistingItem(cartItem: CartItem) {
        return this.cartItems.find(item => item.id === cartItem.id);
    }

    // may be will add 'remove' button in template later
    removeItem(cartItem: CartItem) {
        let index = this.cartItems.findIndex(item => item.id === cartItem.id);
        if (index > -1) this.cartItems.splice(index, 1);
        this.computeCartTotals();
    }

    setStorage(): void {
        localStorage.setItem('cart', JSON.stringify(this.cartItems));
    }

    getCartFromStorage(): CartItem[] {
        let items: CartItem[] = JSON.parse(<string>localStorage.getItem("cart"))
        if (items && items.length > 0) this.cartItems = items;
        this.computeCartTotals();

        return this.cartItems;
    }

    getUsername(){
        return this.authService.getUsername();
    }

    ngOnInit(): void {
        this.getCartFromStorage();
    }

}
