package com.delber.coworking_booking.repository;

import com.delber.coworking_booking.model.Resource;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IResourcesRepository extends JpaRepository<Resource, Long> {
}
