import {Product} from "./Product";
import {Address} from "./Purchase";

export interface OrderDetails {
    id: number;
    orderProducts: OrderProduct[];
    shippingAddress: Address;
    status: string;
    totalQuantity: number;
    totalPrice: number;

}

interface OrderProduct {
    product: Product;
    quantity: number;
}