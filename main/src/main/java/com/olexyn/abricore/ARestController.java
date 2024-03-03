package com.olexyn.abricore;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController()
@RequestMapping("/api")
public class ARestController {

    @GetMapping("/describe")
    public String describe(@RequestParam String subject) throws IOException {
        return MainApp.describe(subject);
    }
}
