import {Component, OnInit} from '@angular/core';
import {Product} from "../../model/Product";
import {ProductService} from "../../services/product.service";
import {ProductsResponse} from "../../model/ProductsResponse";
import {ProductsLinks} from "../../model/ProductsLinks";
import {Page} from "../../model/Page";
import {HttpParams} from "@angular/common/http";

@Component({
    selector: 'app-product-list',
    templateUrl: './product-list.component.html',
    styleUrls: ['./product-list.component.sass']
})
export class ProductListComponent implements OnInit {

    products?: Product[];
    links?: ProductsLinks;
    page: Page = new Page(6,0,0,0);
    searchParams?: SearchParams;
    sort:string = '';

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
                console.log(data);
            }
        )
    }

    private getRequestParams():HttpParams{
        let params: HttpParams = new HttpParams();
        if (this.searchParams) {
            Object.entries(this.searchParams).forEach(([key, value])=>params.set(key,value));
        }
        params.set("page", this.page.number);
        params.set("size", this.page.size);
        params.set("sort", this.sort);
        return params;
    }

}

export interface SearchParams{
    sku:string;
    name:string;
}

