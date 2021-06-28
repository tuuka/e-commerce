import {Component, OnInit} from '@angular/core';
import {OrderDetails} from "../../model/OrderDetails";
import {OrdersService} from "../../services/orders.service";
import {ActivatedRoute} from "@angular/router";

@Component({
    selector: 'app-order-details',
    templateUrl: './order-details.component.html',
    styleUrls: ['./order-details.component.css']
})
export class OrderDetailsComponent implements OnInit {

    orderProducts?: OrderDetails

    constructor(private orderService: OrdersService,
                private route: ActivatedRoute) {
    }

    ngOnInit(): void {
        // @ts-ignore
        let id: number = +this.route.snapshot.paramMap.get('id');
        this.getOrderById(id);
    }

    private getOrderById(id: number) {
        this.orderService.getOrderDetailsById(id).subscribe(
            data => {
                this.orderProducts = data;
                console.log(this.orderProducts)
            }
        )
    }

}
