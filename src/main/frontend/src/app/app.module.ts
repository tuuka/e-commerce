import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {ProductListComponent} from './components/product-list/product-list.component';
import {HttpClientModule} from '@angular/common/http'
import {ProductService} from "./services/product.service";
import { HeaderComponent } from './components/header/header.component';
import { MainComponent } from './components/main/main.component';
import { ProductCardComponent } from './components/product-card/product-card.component';
import {NgxPaginationModule} from "ngx-pagination";

@NgModule({
    declarations: [
        AppComponent,
        ProductListComponent,
        HeaderComponent,
        MainComponent,
        ProductCardComponent,
    ],
    imports: [
        BrowserModule,
        AppRoutingModule,
        HttpClientModule,
        NgxPaginationModule,
    ],
    providers: [ProductService],
    bootstrap: [AppComponent]
})
export class AppModule {
}
