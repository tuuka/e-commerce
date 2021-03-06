import {CartItem} from "./CartItem";
import {OrderItem} from "./OrderItem";

export class Purchase {
    email: string;
    // creditCard: Card;
    shippingAddress: Address;
    orderItems: OrderItem[];


    constructor(email: string,
                // card: Card,
                shippingAddress: Address,
                cartItems: CartItem[]) {
        this.email = email;
        // this.creditCard = card;
        this.shippingAddress = shippingAddress;
        this.orderItems = cartItems.map(item => new OrderItem(item.id, item.quantity));
    }
}


// interface Card {
//     type: string;
//     number: string;
//     code: string;
//     expMonth: string;
//     expYear: string;
// }

export interface Address {
    apartment: string;
    street: string;
    city: string;
    country: string;
    state: string;
    zip: string;
}