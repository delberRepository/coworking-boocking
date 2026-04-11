package com.delber.coworking_booking.repository;

import com.delber.coworking_booking.model.Booking;
import com.delber.coworking_booking.model.Resource;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IResourcesRepository extends JpaRepository<Resource, Long> {

    List<Resource> findByActiveTrue();
    List<Resource> findByActiveFalse();
    Resource findByResourceId(Long resourceId);
}
