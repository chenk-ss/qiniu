package com.chenk.qiniu.controller;

import com.chenk.qiniu.service.QiNiuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author: chenke
 * @since: 2021/5/31
 */
@Controller
@RequestMapping("/")
public class HomeController {


    @Autowired
    private QiNiuService qiNiuService;

    @GetMapping(value = "/")
    private String index() {
        return "index";
    }

    @GetMapping("/list")
    public String queryList(Model m, @RequestParam("page") int page, @RequestParam("size") int size) {
        size = size == 0 ? 20 : size;
        m.addAttribute("resultList", qiNiuService.listFromDB(page, size));
        return "list";
    }
}
