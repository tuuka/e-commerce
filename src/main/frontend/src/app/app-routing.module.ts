import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {ProductListComponent} from "./components/product-list/product-list.component";
import {AuthComponent} from "./components/auth/auth.component";
import {AccountComponent} from "./components/account/account.component";
import {ProductDetailsComponent} from "./components/product-details/product-details.component";
import {CartDetailsComponent} from "./components/cart-details/cart-details.component";
import {CheckoutComponent} from "./components/checkout/checkout.component";
import {OrdersTableComponent} from "./components/orders-table/orders-table.component";
import {OrderDetailsComponent} from "./components/order-details/order-details.component";
import {UsersTableComponent} from "./components/users-table/users-table.component";

const routes: Routes = [
    {path: 'auth', component: AuthComponent},
    {path: 'account', component: AccountComponent,
        children: [
            {path: 'orders', component: OrdersTableComponent},
            {path: 'orders/:id', component: OrderDetailsComponent},
            {path: 'users', component: UsersTableComponent}
        ]
    },
    {path: 'cart', component: CartDetailsComponent},
    {path: 'checkout', component: CheckoutComponent},
    {path: 'products/:id', component: ProductDetailsComponent},
    {path: 'products', component: ProductListComponent},
    {path: '', redirectTo: '/products', pathMatch: 'full'},
    {path: '**', redirectTo: '/products', pathMatch: 'full'},
]

@NgModule({
    imports: [RouterModule.forRoot(routes)],
    exports: [RouterModule]
})
export class AppRoutingModule {
}
