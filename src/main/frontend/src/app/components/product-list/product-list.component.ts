import {Component, OnInit} from '@angular/core';
import {Product} from "../../model/Product";
import {ProductService} from "../../services/product.service";
import {ProductsLinks} from "../../model/ProductsLinks";
import {Page} from "../../model/Page";
import {HttpParams} from "@angular/common/http";

@Component({
    selector: 'app-product-list',
    templateUrl: './product-list.component.html',
    styleUrls: ['./product-list.component.css'],
})
export class ProductListComponent implements OnInit {

    products?: Product[];
    links?: ProductsLinks;
    page: Page = new Page(5, 0, 0, 0);
    sort: string = '';
    sku: string = '';
    name: string = '';

    constructor(private productService: ProductService) {
    }

    ngOnInit(): void {
        this.listProducts();
    }

    listProducts() {
        this.productService.getProductList(this.getRequestParams()).subscribe(
            data => {
                this.products = data._embedded.products;
                this.links = data._links;
                this.page = data.page;
            }
        )
    }

    private getRequestParams() {
        return new HttpParams()
            .set("page", this.page.number)
            .set("size", this.page.size)
            .set("sort", this.sort)
            .set("sku", this.sku)
            .set("name", this.name);
    }

    pageChanged(event: any) {
        this.page.number = event - 1;
        this.listProducts();
    }
}

