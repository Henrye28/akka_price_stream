package com.henryye.cluster.controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping
public class PriceController {

    @GetMapping("/")
    public String homePage(Model model) {
        model.addAttribute("appName", "price");
        return "pricesheet";
    }

}
