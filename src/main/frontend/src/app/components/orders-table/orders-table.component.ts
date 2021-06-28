import {Component, OnInit} from '@angular/core';
import {Order} from "../../model/Order";
import {OrdersService} from "../../services/orders.service";
import {AuthService} from "../../services/auth.service";

@Component({
    selector: 'app-orders-table',
    templateUrl: './orders-table.component.html',
    styleUrls: ['./orders-table.component.css']
})
export class OrdersTableComponent implements OnInit {

    userMainRole: string = '';
    orderItems: Order[] = [];
    displayedColumns: string[] = ['id', 'created', 'status', 'quantity', 'total'];
    totalPrice: number = 0;
    totalQuantity: number = 0;

    constructor(private authService: AuthService,
                private ordersService: OrdersService) {
    }

    ngOnInit(): void {
        this.authService.userRole.subscribe(role => {
            this.userMainRole = role;
            if (['admin', 'manager'].includes(this.userMainRole)) {
                this.displayedColumns = ['id', 'created', 'status', 'quantity', 'total', 'action'];
            } else {
                this.displayedColumns = ['id', 'created', 'status', 'quantity', 'total'];
            }
        });
        this.authService.refreshLoggedUserDetails()

        this.ordersService.getAccountOrders().subscribe(
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

    remove(order: Order) {
        // this.removeOrder.emit(order);
    }

    edit(order: Order) {
        // this.editOrder.emit(order);
    }
}
