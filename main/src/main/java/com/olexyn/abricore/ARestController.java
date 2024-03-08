package com.olexyn.abricore;


import com.olexyn.abricore.store.runtime.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController()
@RequestMapping("/api")
public class ARestController {


    private final EventService eventService;

    @Autowired
    public ARestController(EventService eventService) {
        this.eventService = eventService;
    }


    @GetMapping("/describe")
    public String describe(@RequestParam String subject) throws IOException {
        return eventService.describe(subject);
    }

}
