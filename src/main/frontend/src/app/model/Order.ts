export interface Order {
    id: number;
    trackingNumber: string;
    created: Date;
    status: string;
    totalQuantity: number;
    totalPrice: number;
}
