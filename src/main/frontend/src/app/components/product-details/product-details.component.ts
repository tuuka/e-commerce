import {Component, OnInit} from '@angular/core';
import {Product} from "../../model/Product";
import {ProductService} from "../../services/product.service";
import {ActivatedRoute} from "@angular/router";
import {CartService} from "../../services/cart.service";
import {CartItem} from "../../model/CartItem";

@Component({
    selector: 'app-product-details',
    templateUrl: './product-details.component.html',
    styleUrls: ['./product-details.component.css']
})
export class ProductDetailsComponent implements OnInit {

    product?: Product;
    quantityToOrder: number = 1;

    constructor(private productService: ProductService,
                private route: ActivatedRoute,
                private cartService: CartService) {
    }

    ngOnInit(): void {
        this.route.paramMap.subscribe(() => {
            this.handleProductDetails();
        })
    }

    private handleProductDetails() {
        // @ts-ignore
        const productId: number = +this.route.snapshot.paramMap.get('id');
        this.productService.getProduct(productId).subscribe(
            (res: any) => {
                this.product = res;
                if (this.product?.unitsInStock == 0) this.quantityToOrder = 0;
            });
    }

    decrement() {
        if (this.quantityToOrder > 1) this.quantityToOrder--;
    }

    increment() {
        // @ts-ignore
        if (this.quantityToOrder < this.product.unitsInStock) this.quantityToOrder++;
    }

    addToCart() {
        let cartItem = new CartItem(this.product);
        cartItem.quantity = this.quantityToOrder;
        this.cartService.addToCart(cartItem);
    }
}
