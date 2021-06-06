import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';
import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {ProductListComponent} from './components/product-list/product-list.component';
import {HttpClientModule} from '@angular/common/http'
import {ProductService} from "./services/product.service";
import {HeaderComponent} from './components/header/header.component';
import {MainComponent} from './components/main/main.component';
import {ProductCardComponent} from './components/product-card/product-card.component';
import {NgxPaginationModule} from "ngx-pagination";
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {SelectComponent} from './components/select/select.component';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {CategoryListComponent} from './components/category-list/category-list.component';
import {LayoutModule} from '@angular/cdk/layout';
import {AuthComponent} from './components/auth/auth.component';
import {SignUpComponent} from './components/sign-up/sign-up.component';
import {LoginComponent} from './components/login/login.component';
import {AngularMaterialModule} from "./agular-material.module";
import {FlexModule} from "@angular/flex-layout";
import {MAT_FORM_FIELD_DEFAULT_OPTIONS} from "@angular/material/form-field";

@NgModule({
    declarations: [
        AppComponent,
        ProductListComponent,
        HeaderComponent,
        MainComponent,
        ProductCardComponent,
        SelectComponent,
        CategoryListComponent,
        AuthComponent,
        SignUpComponent,
        LoginComponent,
    ],
    imports: [
        BrowserModule,
        AppRoutingModule,
        HttpClientModule,
        NgxPaginationModule,
        BrowserAnimationsModule,
        ReactiveFormsModule,
        FormsModule,
        LayoutModule,
        AngularMaterialModule,
        FlexModule
    ],
    providers: [ProductService,
        {provide: MAT_FORM_FIELD_DEFAULT_OPTIONS, useValue: {floatLabel: 'always'}}
    ],
    bootstrap: [AppComponent],
    // schemas: [NO_ERRORS_SCHEMA]
})
export class AppModule {
}
