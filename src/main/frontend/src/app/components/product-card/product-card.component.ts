import {Component, Input, Renderer2} from '@angular/core';
import {Product} from "../../model/Product";
import {CartService} from "../../services/cart.service";
import {CartItem} from "../../model/CartItem";

@Component({
    selector: 'app-product-card',
    templateUrl: './product-card.component.html',
    styleUrls: ['./product-card.component.css']
})
export class ProductCardComponent {


    constructor(private renderer: Renderer2,
                private cartService: CartService) {
    }

    @Input() product?: Product;

    hover (event: { target: any; }) {
        this.renderer.addClass(event.target, 'mat-elevation-z5')
    }

    unHover (event: { target: any; }) {
        this.renderer.removeClass(event.target, 'mat-elevation-z5')
    }

    addToCart(product: Product) {
        this.cartService.addToCart(new CartItem(product));
    }
}
