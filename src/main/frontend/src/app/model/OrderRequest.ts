import {Address} from "./Purchase";
import {OrderItem} from "./OrderItem";

export class OrderRequest {
    constructor(
        public orderId?: number,
        public status?: string,
        public shippingAddress?: Address,
        public orderItems?: OrderItem[]
    ) {
    }
}