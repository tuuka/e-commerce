import {Component, OnInit, ViewChild} from '@angular/core';
import {CartService} from "../../services/cart.service";
import {CartItem} from "../../model/CartItem";
import {MatTable} from "@angular/material/table";
import {environment} from "../../../environments/environment";

@Component({
    selector: 'app-cart-details',
    templateUrl: './cart-details.component.html',
    styleUrls: ['./cart-details.component.css']
})
export class CartDetailsComponent implements OnInit {

    cartItems: CartItem[] = [];
    totalPrice: number = 0;
    totalQuantity: number = 0;
    displayedColumns: string[] = ['image', 'name', 'price', 'quantity', 'total'];

    constructor(private cartService: CartService) {
    }

    @ViewChild(MatTable) table?: MatTable<CartItem>;

    ngOnInit(): void {
        this.getCartDetails();
    }

    private getCartDetails() {
        this.cartService.totalPrice.subscribe(data => this.totalPrice = data);
        this.cartService.totalQuantity.subscribe(data => this.totalQuantity = data);
        this.cartItems = this.cartService.getCartFromStorage();
    }

    decrementQuantity(cartItem: CartItem) {
        this.cartService.decrementQuantity(cartItem);
        if (this.table) this.table.renderRows();
    }

    incrementQuantity(cartItem: CartItem) {
        this.cartService.incrementQuantity(cartItem);
        if (this.table) this.table.renderRows();
    }

    isLoggedIn(){
        return this.cartService.getUsername() != environment.anonymousName;
    }
}
