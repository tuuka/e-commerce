import {Product} from "./Product";

export class CartItem {

    id: number = 0;
    name: string = '';
    imageUrl: string = '';
    unitPrice: number = 0;
    quantity: number = 0;

    constructor(
        product: Product | undefined
    ) {
        if (product) {
            this.id = product.id;
            this.name = product.name;
            this.imageUrl = product.imageUrl;
            this.unitPrice = product.unitPrice;
            this.quantity = 1;
        }
    }

}