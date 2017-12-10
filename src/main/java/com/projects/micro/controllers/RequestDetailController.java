package com.projects.micro.controllers;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.DataBinder;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projects.micro.models.RequestDetail;
import com.projects.micro.repositories.RequestDetailRepository;
import com.projects.micro.validators.RequestDetailValidator;

@RestController
@RequestMapping("/requests")
public class RequestDetailController {
	
	private final RequestDetailRepository repository;
	private final RequestDetailValidator validator;
	private final ObjectMapper objectMapper;
	
    @Autowired
    public RequestDetailController(RequestDetailRepository repository, RequestDetailValidator validator, ObjectMapper objectMapper) {
        this.repository = repository;
        this.validator = validator;
        this.objectMapper = objectMapper;
    }
    
    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.addValidators(validator);
    }
    
    @RequestMapping(value = "/",method = RequestMethod.GET, produces = "application/json")
    public Iterable findAll(@RequestParam(value = "page", defaultValue = "0", required = false) int page,
            @RequestParam(value = "count", defaultValue = "10", required = false) int count,
            @RequestParam(value = "order", defaultValue = "ASC", required = false) Sort.Direction direction,
            @RequestParam(value = "sort", defaultValue = "requestName", required = false) String sortProperty) {
    	System.out.println("In Here....");
	Page result = repository.findAll(new PageRequest(page, count, new Sort(direction, sortProperty)));
	return result.getContent();
	}
    
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = "application/json")
    public RequestDetail find(@PathVariable Long id){
    	RequestDetail detail = repository.findOne(id);
        if (detail == null) {
            throw new RequestNotFoundException();
        } else {
            return detail;
        }
    }
    
    @RequestMapping(method = RequestMethod.POST)
    public RequestDetail create(@RequestBody @Valid RequestDetail detail) {
        return repository.save(detail);
    }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public HttpEntity update(@PathVariable Long id, HttpServletRequest request) throws IOException {
        RequestDetail existing = find(id);
        RequestDetail updated = objectMapper.readerForUpdating(existing).readValue(request.getReader());
        MutablePropertyValues propertyValues = new MutablePropertyValues();
        //propertyValues.add("productId", updated.getProductId());
        propertyValues.add("requestName", updated.getRequestName());
        propertyValues.add("shortDescription", updated.getShortDescription());
        propertyValues.add("longDescription", updated.getLongDescription());
        //propertyValues.add("inventoryId", updated.getInventoryId());
        DataBinder binder = new DataBinder(updated);
        binder.addValidators(validator);
        binder.bind(propertyValues);
        binder.validate();
        if (binder.getBindingResult().hasErrors()) {
            return new ResponseEntity<>(binder.getBindingResult().getAllErrors(), HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity<>(updated, HttpStatus.ACCEPTED);
        }
    }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public HttpEntity delete(@PathVariable Long id) {
        RequestDetail detail = find(id);
        repository.delete(detail);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
    @ResponseStatus(HttpStatus.NOT_FOUND)
    static class RequestNotFoundException extends RuntimeException {
    }

}
