package com.epam.resume_gateway;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ResumeController {

    @RequestMapping(value = "/")
    public String index() {
        return "index";
    }

}
