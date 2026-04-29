package com.delber.coworking_booking.service;

import com.delber.coworking_booking.model.Resource;
import com.delber.coworking_booking.model.ResourceType;
import com.delber.coworking_booking.repository.IResourcesRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ResourceService {

    /*createResource (admin)
getAllResources
getResourceById
(opcional) disableResource*/

private final IResourcesRepository rr;

@Transactional
@PreAuthorize("hasRole('ADMIN')")
public Resource createResources(String name, ResourceType tipo, Boolean active, int capacidad){
    Resource recurso= new Resource();
    recurso.setName(name);
    recurso.setType(tipo);
    recurso.setActive(active);
    recurso.setCapacity(capacidad);

    return rr.save(recurso);
}

@Transactional
@PreAuthorize("hasAnyRole('ADMIN')")
public Resource updateResources(Long resourceId, String name, ResourceType tipo, Boolean active, int capacidad){
    Resource recurso = rr.findById(resourceId)
            .orElseThrow(() -> new RuntimeException("Recurso no encontrado"));

    recurso.setName(name);
    recurso.setType(tipo);
    recurso.setActive(active);
    recurso.setCapacity(capacidad);

    return rr.save(recurso);
}
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @Transactional
    public List<Resource>getAllResources(){
    return rr.findByActiveTrue();

}
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public List<Resource>getInactiveResources(){
        return rr.findByActiveFalse();

    }

    @Transactional
    public Resource getResourcesById(Long resources_id){
        return rr.findById(resources_id)
                .orElseThrow(() -> new RuntimeException("Recurso no encontrado"));

    }
}
