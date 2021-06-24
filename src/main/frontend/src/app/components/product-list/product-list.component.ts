import {Component, OnInit} from '@angular/core';
import {Product} from "../../model/Product";
import {ProductService} from "../../services/product.service";
import {Page} from "../../model/Page";
import {HttpParams} from "@angular/common/http";
import {SelectOption} from "../select/select.component"
import {ActivatedRoute} from "@angular/router";
import {Category} from "../../model/Category";

@Component({
    selector: 'app-product-list',
    templateUrl: './product-list.component.html',
    styleUrls: ['./product-list.component.css']
})
export class ProductListComponent implements OnInit {

    categories?: Category[];
    products?: Product[];
    // links?: PagedListLinks;
    page: Page = new Page(6, 0, 0, 0);
    sort: string = '';
    name: string = '';
    currentCategoryId: number = 0;

    sortOptions: SelectOption[] = [
        {
            value: 'created',
            viewValue: 'Date'
        },
        {
            value: 'unitPrice',
            viewValue: 'Price'
        }
    ]

    pageSizeOptions: SelectOption[] = [6, 12, 24, 48].map(String)
        .map((s: string) => ({value: '' + s, viewValue: '' + s}))

    constructor(private productService: ProductService,
                private route: ActivatedRoute) {}

    ngOnInit(): void {
        this.route.paramMap.subscribe(() => {
            this.listCategories();
            this.listProducts();
        })
    }

    listCategories() {
        this.productService.getCategoriesList().subscribe(
            data => {
                this.categories = data._embedded.categories;
            }
        )
    }

    listProducts() {
        if (this.currentCategoryId) {
            this.name = '';
        }
        this.productService.getProductList(this.currentCategoryId, this.getRequestParams()).subscribe(
            data => {
                this.products = data._embedded.products;
                // this.links = data._links;
                this.page = data.page;
            }
        )
    }

    private getRequestParams() {
        return new HttpParams()
            .set("page", this.page.number)
            .set("size", this.page.size)
            .set("sort", this.sort)
            .set("name", this.name);
    }

    pageChanged(event: any) {
        this.page.number = event - 1;
        this.listProducts();
    }

    sortProducts($event: string) {
        this.sort = $event ? $event : '';
        this.listProducts();
    }

    pageSizeChanged($event: string) {
        this.page.size = +$event;
        this.page.number = 0;
        this.listProducts();
    }

    search(value: string) {
        this.name = value;
        this.listProducts();
    }

    changeCategory($event: number) {
        this.currentCategoryId = $event;
        this.listProducts();
    }
}



