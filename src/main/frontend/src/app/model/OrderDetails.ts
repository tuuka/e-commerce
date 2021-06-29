import {Product} from "./Product";
import {Address} from "./Purchase";
import {Order} from "./Order";
import {UserDetails} from "./user-details";

export interface OrderDetails {
    order: Order;
    orderProducts: OrderProduct[];
    shippingAddress: Address;
    user: UserDetails

}

export interface OrderProduct {
    product: Product;
    quantity: number;
}