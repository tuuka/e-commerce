import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Category} from "../model/Category";
import {Product} from "../model/Product";
import {PagedListLinks} from "../model/PagedListLinks";
import {Page} from "../model/Page";
import {environment} from "../../environments/environment";

@Injectable({
    providedIn: 'root'
})
export class ProductService {

    private baseProductsUrl = environment.apiUrl + '/api/products';
    private baseCategoriesUrl = environment.apiUrl + '/api/categories';

    constructor(private httpClient: HttpClient) {
    }

    getProductList(category_id: number, parameters: HttpParams): Observable<ProductsResponse> {
        let url;
        if (category_id) {
            url = `${this.baseCategoriesUrl}/${category_id}/products`
        } else {
            url = parameters.get("name") ? `${this.baseProductsUrl}/search` : this.baseProductsUrl;
        }
        return this.httpClient.get<ProductsResponse>(url, {responseType: "json", params: parameters});
    }

    getCategoriesList(): Observable<CategoriesResponse> {
        return this.httpClient.get<CategoriesResponse>(this.baseCategoriesUrl);
    }

    getProduct(productId: number) {
        const url = `${this.baseProductsUrl}/${productId}`;
        return this.httpClient.get(url, {responseType: "json"})

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

