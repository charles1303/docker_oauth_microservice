package com.projects.micro.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.projects.micro.models.RequestDetail;

@Repository
public interface RequestDetailRepository extends PagingAndSortingRepository <RequestDetail, Long>{
	
	@Query("select p from RequestDetail p where UPPER(p.requestName) like UPPER(?1) or " +
            "UPPER(p.longDescription) like UPPER(?1)")
    List search(String term);
}


