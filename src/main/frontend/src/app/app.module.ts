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
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { SelectComponent } from './components/select/select.component';
import {MatFormFieldModule} from "@angular/material/form-field";
import {MatSelectModule} from "@angular/material/select";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {RouterModule, Routes} from "@angular/router";
import { AsideComponent } from './components/aside/aside.component';
import { CategoryListComponent } from './components/category-list/category-list.component';


const routes: Routes = [
    {path: 'category/:id', component:ProductListComponent},
    {path: 'category', component:ProductListComponent},
    {path: 'products', component:ProductListComponent},
    {path: '', redirectTo:'/products', pathMatch:'full'},
    {path: '**', redirectTo:'/products', pathMatch:'full'},
]

@NgModule({
    declarations: [
        AppComponent,
        ProductListComponent,
        HeaderComponent,
        MainComponent,
        ProductCardComponent,
        SelectComponent,
        AsideComponent,
        CategoryListComponent,
    ],
    imports: [
        RouterModule.forRoot(routes),
        BrowserModule,
        AppRoutingModule,
        HttpClientModule,
        NgxPaginationModule,
        BrowserAnimationsModule,
        MatFormFieldModule,
        MatSelectModule,
        ReactiveFormsModule,
        FormsModule,
    ],
    providers: [ProductService],
    bootstrap: [AppComponent]
})
export class AppModule {
}
