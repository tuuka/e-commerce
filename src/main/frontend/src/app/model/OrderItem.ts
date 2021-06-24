export interface OrderItem {
    id: number;
    trackingNumber: string;
    created: Date;
    status: string;
    totalQuantity: number;
    totalPrice: number;

}