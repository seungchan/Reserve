package com.example.reserve;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "reserve", path = "reserve")
public interface ReservationRepository extends PagingAndSortingRepository<Reservation, Long> {
	List<Reservation> findByGuestName(@Param("name") String name);
}
