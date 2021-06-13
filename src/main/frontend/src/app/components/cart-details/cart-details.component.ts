import {Component, OnInit, ViewChild} from '@angular/core';
import {CartService} from "../../services/cart.service";
import {CartItem} from "../../model/CartItem";
import {MatTable} from "@angular/material/table";

@Component({
    selector: 'app-cart-details',
    templateUrl: './cart-details.component.html',
    styleUrls: ['./cart-details.component.css']
})
export class CartDetailsComponent implements OnInit {

    cartItems: CartItem[] = [];
    totalPrice: number = 0;
    totalQuantity: number = 0;
    displayedColumns: string[] = ['actions', 'image', 'name', 'price', 'quantity', 'total'];

    constructor(private cartService: CartService) {
    }

    @ViewChild(MatTable) table?: MatTable<CartItem>;

    ngOnInit(): void {
        this.getCartDetails();
    }

    private getCartDetails() {
        this.cartItems = this.cartService.cartItems;
        this.cartService.totalPrice.subscribe(data => this.totalPrice = data);
        this.cartService.totalQuantity.subscribe(data => this.totalQuantity = data);
        this.cartService.computeCartTotals();
    }

    removeItem(cartItem: CartItem) {
        this.cartService.removeItem(cartItem);
        if (this.table) this.table.renderRows();
    }
}
