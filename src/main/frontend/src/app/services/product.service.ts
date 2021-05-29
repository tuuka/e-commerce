import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable} from 'rxjs';
import {ProductsResponse} from "../model/ProductsResponse";

@Injectable({
    providedIn: 'root'
})
export class ProductService {

    private baseUrl = 'http://localhost:8080/api/products';

    constructor(private httpClient: HttpClient) {
    }

    getProductList(parameters:HttpParams): Observable<ProductsResponse> {
        let url = (parameters.get("sku") || parameters.get("name"))? `${this.baseUrl}/search` : this.baseUrl;
        return this.httpClient.get<ProductsResponse>(url, {responseType: "json", params: parameters});
    }

}

