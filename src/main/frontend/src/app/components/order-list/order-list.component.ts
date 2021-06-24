import {Component, OnInit} from '@angular/core';
import {OrderItem} from "../../model/OrderItem";
import {OrdersService} from "../../services/orders.service";
import {AuthService} from "../../services/auth.service";

@Component({
    selector: 'app-order-list',
    templateUrl: './order-list.component.html',
    styleUrls: ['./order-list.component.css']
})
export class OrderListComponent implements OnInit {

    orderItems: OrderItem[] = [];
    displayedColumns: string[] = ['id', 'created', 'status', 'quantity', 'total'];
    totalPrice: number = 0;
    totalQuantity: number = 0;
    isLoggedIn: boolean = false;

    constructor(private orderService: OrdersService,
                private authService: AuthService) {
    }

    ngOnInit(): void {
        this.orderService.getAccountOrders().subscribe(
            data => {
                this.orderItems = data;
                this.getTotals();
            },
            err => console.log(err)
        )
    }

    private getTotals() {
        this.totalQuantity = this.orderItems.reduce(
            (acc, item) => {
                acc += item.totalQuantity;
                return acc;
            }, 0)
        this.totalPrice = this.orderItems.reduce(
            (acc, item) => {
                acc += item.totalPrice;
                return acc;
            }, 0)
    }

}
