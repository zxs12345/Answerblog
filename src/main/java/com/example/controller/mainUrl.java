package com.example.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class mainUrl {
@RequestMapping("/")
    public String index(){
        return "index";
    }
    @RequestMapping("/listx")
    public String list(){
        return "list";
    }
    @RequestMapping("/showx")
    public String show(){
        return "show";
    }
    @RequestMapping("/errorx")
    public String error(){
        return "404";
    }

   
}
