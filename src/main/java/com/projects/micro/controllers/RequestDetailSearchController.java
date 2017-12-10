package com.projects.micro.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.projects.micro.repositories.RequestDetailRepository;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/search")
public class RequestDetailSearchController {
	private final RequestDetailRepository repository;
    @Autowired
    public RequestDetailSearchController(RequestDetailRepository repository) {
        this.repository = repository;
    }
    @RequestMapping(method = RequestMethod.GET)
    public List search(@RequestParam("q") String queryTerm) {
        List productDetails = repository.search("%"+queryTerm+"%");
        return productDetails == null ? new ArrayList<>() : productDetails;
    }

}
