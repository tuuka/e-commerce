import {Component, OnInit, ViewChild} from '@angular/core';
import {CartService} from "../../services/cart.service";
import {CartItem} from "../../model/CartItem";
import {AuthService} from "../../services/auth.service";
import {ProductsTableComponent} from "../products-table/products-table.component";

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
    isLoggedIn: boolean = false;

    constructor(private cartService: CartService, private authService: AuthService) {
    }

    @ViewChild(ProductsTableComponent) table?: ProductsTableComponent;

    ngOnInit(): void {
        this.getCartDetails();
        this.authService.userRole.subscribe(role => {
            this.isLoggedIn = !!role;
        })
        this.cartService.totalPrice.subscribe(data => this.totalPrice = data);
        this.cartService.totalQuantity.subscribe(data => this.totalQuantity = data);
    }

    private getCartDetails() {
        this.cartItems = this.cartService.getCartFromStorage();
    }

    decrementQuantity(cartItem: CartItem) {
        this.cartService.decrementQuantity(cartItem);
        this.table?.renderRows()
    }

    incrementQuantity(cartItem: CartItem) {
        this.cartService.incrementQuantity(cartItem);
        this.table?.renderRows()
    }

}
