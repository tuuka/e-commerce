package net.tuuka.ecommerce.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;

//@CrossOrigin
@Controller
public class HomeController {

    @RequestMapping("/")
    public String index(){
        return "index";
    }

}
