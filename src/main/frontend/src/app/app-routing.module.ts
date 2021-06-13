import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {ProductListComponent} from "./components/product-list/product-list.component";
import {AuthComponent} from "./components/auth/auth.component";
import {AccountComponent} from "./components/account/account.component";
import {ProductDetailsComponent} from "./components/product-details/product-details.component";
import {CartDetailsComponent} from "./components/cart-details/cart-details.component";

const routes: Routes = [
    {path: 'auth', component: AuthComponent},
    {path: 'account', component: AccountComponent},
    {path: 'cart', component: CartDetailsComponent},
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
