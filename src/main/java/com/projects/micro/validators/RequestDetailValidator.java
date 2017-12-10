package com.projects.micro.validators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.*;

import com.projects.micro.models.RequestDetail;
import com.projects.micro.services.RequestService;

@Component
public class RequestDetailValidator implements Validator {
	
	 private final RequestService requestService;
	    @Autowired
	    public RequestDetailValidator(RequestService requestService) {
	        this.requestService = requestService;
	    }

	@Override
	public boolean supports(Class<?> clazz) {
		return RequestDetail.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		RequestDetail detail = (RequestDetail)target;
        if (!requestService.isValidRequest(detail.getId())) {
            errors.rejectValue("requestId", "request.id.invalid", "Request ID is invalid");
        }
		
	}

}
