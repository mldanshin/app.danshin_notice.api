package net.danshin.danshin_notice_api.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
@SuppressWarnings("unused")
public class IndexController {
    public String index() {
        return "index";
    }
}
