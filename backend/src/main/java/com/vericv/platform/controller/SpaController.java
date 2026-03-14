package com.vericv.platform.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SpaController {

    @RequestMapping(value = { "/login", "/register", "/dashboard", "/directory", "/directory/**", "/profile/**",
            "/cv/**" })
    public String forward() {
        return "forward:/index.html";
    }
}