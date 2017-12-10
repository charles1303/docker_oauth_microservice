package com.projects.micro.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.netflix.hystrix.*;
import com.projects.micro.repositories.RequestDetailRepository;

@Service
public class RequestService {
	
	private static final int TIMEOUT = 60000;
	private static final String GROUP = "requests";
	
	@Autowired
	private final RequestDetailRepository repository;
	
	public RequestService(RequestDetailRepository repository){
		this.repository = repository;
		
	}
	
	public boolean isValidRequest(Long id) {
		// TODO Auto-generated method stub
		return true;
	}
	
	public Map<String, Map<String, Object>> getRequests(){
		List<Callable<AsyncResponse>> callables = new ArrayList<>();
        callables.add(new BackendServiceCallable("requests", getRequests()));
        return doBackendAsyncServiceCall(callables);
	}
	
	private static Map<String, Map<String, Object>> doBackendAsyncServiceCall(List<Callable<AsyncResponse>> callables) {
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        try {
            List<Future<AsyncResponse>> futures = executorService.invokeAll(callables);
            executorService.shutdown();
            executorService.awaitTermination(TIMEOUT, TimeUnit.MILLISECONDS);
            Map<String, Map<String, Object>> result = new HashMap<>();
            for (Future<AsyncResponse> future : futures) {
                AsyncResponse response = future.get();
                result.put(response.serviceKey, response.response);
            }
            return result;
        } catch (InterruptedException|ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
	
	 @Cacheable
	    private HystrixCommand<Map<String, Object>> getProductDetails(String productId) {
	        return new HystrixCommand<Map<String, Object>>(
	                HystrixCommand.Setter
	                        .withGroupKey(HystrixCommandGroupKey.Factory.asKey(GROUP))
	                        .andCommandKey(HystrixCommandKey.Factory.asKey("getRequests"))
	                        .andCommandPropertiesDefaults(
	                                HystrixCommandProperties.Setter()
	                                        .withExecutionIsolationThreadTimeoutInMilliseconds(TIMEOUT)
	                        )
	        ) {
	            @Override
	            protected Map<String, Object> run() throws Exception {
	                return (HashMap)repository.findAll();
	            }
	            @Override
	            protected Map getFallback() {
	                return new HashMap<>();
	            }
	        };
	    }
	
	private static class AsyncResponse {
        private final String serviceKey;
        private final Map<String, Object> response;
        AsyncResponse(String serviceKey, Map<String, Object> response) {
            this.serviceKey = serviceKey;
            this.response = response;
        }
    }
    private static class BackendServiceCallable implements Callable<AsyncResponse> {
        private final String serviceKey;
        private final HystrixCommand<Map<String, Object>> hystrixCommand;
        public BackendServiceCallable(String serviceKey, Map<String, Map<String, Object>> map) {
            this.serviceKey = serviceKey;
            this.hystrixCommand = (HystrixCommand<Map<String, Object>>) map;
        }
        @Override
        public AsyncResponse call() throws Exception {
            return new AsyncResponse(serviceKey, hystrixCommand.execute());
        }
    }

	

}
