import {Product} from "./Product";

export class Category {

    constructor(
        public id: number,
        public name: string,
        public products: Product[],
        public _links: {
            self: { href: string },
            categories: { href: string },
            products: { href: string }
        }
    ) {
    }

}
