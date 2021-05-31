package com.chenk.qiniu.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author: chenke
 * @since: 2021/5/31
 */
@Controller
@RequestMapping("/")
public class HomeController {

    @GetMapping(value = "/")
    private String index() {
        return "index";
    }
}
