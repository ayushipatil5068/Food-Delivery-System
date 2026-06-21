package com.cyberweb.fdms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cyberweb.fdms.entity.Restaurant;
import com.cyberweb.fdms.entity.User;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
	List<Restaurant> findByOwner(User owner);

	List<Restaurant> findByAvailableTrue();
}
