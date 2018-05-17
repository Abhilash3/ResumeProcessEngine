package com.epam;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ApplicationController {

    @RequestMapping(value = "/")
    public String index() {
        return "index";
    }

    @RequestMapping(value = "/conf")
    public String conf() {
        return "conf";
    }

    @RequestMapping(value = "/issues")
    public String issues() {
        return "issues";
    }
}
