package com.delber.coworking_booking.controller;

import com.delber.coworking_booking.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/resources")

public class ResourcesController {

    private final ResourceService rs;


    public ResourcesController(ResourceService rs) {

        this.rs = rs;
    }
}
