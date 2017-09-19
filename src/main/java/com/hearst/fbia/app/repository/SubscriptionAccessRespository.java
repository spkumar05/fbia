package com.hearst.fbia.app.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.hearst.fbia.app.domain.SubscriptionAccess;

@Repository
public interface SubscriptionAccessRespository extends PagingAndSortingRepository<SubscriptionAccess, String> {

}