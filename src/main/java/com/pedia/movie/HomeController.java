package com.pedia.movie;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;



@Controller
public class HomeController {
    @GetMapping("/")
    public String home()  {

    return "home";
    }
    @GetMapping("/new")
    public String showNew() {
        return "new";
    }
}
