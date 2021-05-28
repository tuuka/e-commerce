import {Product} from "./Product";
import {Page} from "./Page";
import {ProductsLinks} from "./ProductsLinks";

export class ProductsResponse {

    constructor(
        public _embedded: { products: Product[] },
        public _links: ProductsLinks,
        public page: Page
    ) {
    }

}
