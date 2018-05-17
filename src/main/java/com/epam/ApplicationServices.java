package com.epam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/service")
public class ApplicationServices {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationServices.class);

    @PostMapping(value = "/log/{level}", consumes = MediaType.TEXT_PLAIN_VALUE)
    public void log(@PathVariable String level, @RequestBody String msg) {
        switch (level.toLowerCase()) {
            case "info":
                logger.info(msg);
                break;
            case "warn":
                logger.warn(msg);
                break;
            case "error":
                logger.error(msg);
                break;
            case "debug":
                logger.debug(msg);
                break;
            case "trace":
                logger.trace(msg);
                break;
            default:
                logger.error("Unknown level: {}, with msg: {}", level, msg);
                break;
        }
    }
}
