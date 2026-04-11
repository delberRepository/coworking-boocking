package com.delber.coworking_booking.service;

import com.delber.coworking_booking.model.Resource;
import com.delber.coworking_booking.model.ResourceType;
import com.delber.coworking_booking.repository.IResourcesRepository;
import com.delber.coworking_booking.repository.IUserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ResourceService {

    /*createResource (admin)
getAllResources
getResourceById
(opcional) disableResource*/

private IResourcesRepository rr;
private IUserRepository ur;

@Transactional
public Resource createResources(String name, ResourceType tipo, Boolean active, int capacidad){
    Resource recurso= new Resource();

    return rr.save(recurso);
}
@Transactional
    public List<Resource>getAllResources(){
    return rr.findByActiveTrue();

}
    @Transactional
    public List<Resource>disableResources(){
        return rr.findByActiveFalse();

    }

    @Transactional
    public Resource getResourcesById(Long resources_id){
        return rr.findByResourceId(resources_id);

    }
}
