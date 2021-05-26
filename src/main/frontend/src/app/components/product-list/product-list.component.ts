import {Component, OnInit} from '@angular/core';
import {Product} from "../../model/Product";
import {ProductService} from "../../services/product.service";

@Component({
    selector: 'app-product-list',
    templateUrl: './product-list.component.html',
    styleUrls: ['./product-list.component.sass']
})
export class ProductListComponent implements OnInit {

    products: Product[] | undefined;
    // = [
    //   new Product(1,"sku1", "name1", "desc1", 1.0, "/img1.jpg", true, 10, new Date(), new Date(), {id:1, name:"cat1"}),
    //   new Product(2, "sku2", "name2", "desc2", 1.0, "/img2.jpg", true, 10, new Date(), new Date(), {id:2, name:"cat2"}),
    //   new Product(3, "sku3", "name3", "desc3", 1.0, "/img3.jpg", true, 10, new Date(), new Date(), {id:3, name:"cat3"})
    // ]

    constructor(private productService: ProductService) {
    }

    ngOnInit(): void {
        this.listProducts();
    }

    listProducts() {
        this.productService.getProductList().subscribe(
            data => {
                this.products = data;
            }
        )
    }
}
