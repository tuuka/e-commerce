import {Component, OnInit} from '@angular/core';
import {ProductService} from "../../services/product.service";
import {Category} from "../../model/Category";

@Component({
    selector: 'app-category-list',
    templateUrl: './category-list.component.html',
    styleUrls: ['./category-list.component.css']
})
export class CategoryListComponent implements OnInit {

    categories?: Category[];

    constructor(private productService: ProductService) {
    }

    ngOnInit(): void {
        this.listCategories();
    }

    listCategories() {
        this.productService.getCategoriesList().subscribe(
            data => {
                this.categories = data._embedded.categories;
            }
        )
    }

}
