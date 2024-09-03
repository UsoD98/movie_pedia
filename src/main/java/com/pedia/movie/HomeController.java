package com.pedia.movie;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;



@Controller
public class HomeController {
    @GetMapping("/new")
    public String showNew() {
        return "new";
    }
}
