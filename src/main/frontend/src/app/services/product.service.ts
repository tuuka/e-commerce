import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Category} from "../model/Category";
import {Product} from "../model/Product";
import {PagedListLinks} from "../model/PagedListLinks";
import {Page} from "../model/Page";

@Injectable({
    providedIn: 'root'
})
export class ProductService {

    private baseProductsUrl = 'http://localhost:8080/api/products';
    private baseCategoriesUrl = 'http://localhost:8080/api/categories';

    constructor(private httpClient: HttpClient) {
    }

    getProductList(category_id:number, parameters: HttpParams): Observable<ProductsResponse> {
        console.log(`category_id=${category_id}; \nparameters:\n${parameters}`);
        let url;
        if (category_id) {
            url = `${this.baseCategoriesUrl}/${category_id}/products`
        } else {
            url = (parameters.get("sku") || parameters.get("name")) ? `${this.baseProductsUrl}/search` : this.baseProductsUrl;
        }
        console.log(url);
        return this.httpClient.get<ProductsResponse>(url, {responseType: "json", params: parameters});
    }

    getCategoriesList(): Observable<CategoriesResponse> {
        return this.httpClient.get<CategoriesResponse>(this.baseCategoriesUrl);
    }

}

class ProductsResponse {
    constructor(
        public _embedded: { products: Product[] },
        public _links: PagedListLinks,
        public page: Page
    ) {
    }
}

class CategoriesResponse {
    constructor(
        public _embedded: { categories: Category[] },
    ) {
    }
}

