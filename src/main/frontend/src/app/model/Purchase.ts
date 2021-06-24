import {CartItem} from "./CartItem";

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
        this.orderItems = cartItems.map(item => new OrderItem(item));
    }
}

class OrderItem {
    id: number = 0;
    // unitPrice: number = 0;
    quantity: number = 0;

    constructor(cartItem: CartItem) {
        if (cartItem) {
            this.id = cartItem.id;
            // this.unitPrice = cartItem.unitPrice;
            this.quantity = cartItem.quantity;
        }
    }
}

// interface Card {
//     type: string;
//     number: string;
//     code: string;
//     expMonth: string;
//     expYear: string;
// }

interface Address {
    apartment: string;
    street: string;
    city: string;
    country: string;
    state: string;
    zip: string;
}