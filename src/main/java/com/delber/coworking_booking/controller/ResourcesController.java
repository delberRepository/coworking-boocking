package com.delber.coworking_booking.controller;

import com.delber.coworking_booking.dto.resources.CreateResourceRequest;
import com.delber.coworking_booking.dto.resources.ResourceResponse;
import com.delber.coworking_booking.model.Resource;
import com.delber.coworking_booking.model.ResourceType;
import com.delber.coworking_booking.service.ResourceService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/resources")
@SecurityRequirement(name = "bearerAuth")
public class ResourcesController {

    private final ResourceService rs;

    public ResourcesController(ResourceService rs) {
        this.rs = rs;
    }

    @PostMapping
    public ResponseEntity<ResourceResponse> createResource(@Valid @RequestBody CreateResourceRequest request) {
        Resource resource = rs.createResources(
                request.name(),
                parseType(request.type()),
                request.active() != null ? request.active() : true,
                request.capacity()
        );

        return ResponseEntity.ok(toResponse(resource));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResourceResponse> updateResource(
            @PathVariable Long id,
            @Valid @RequestBody CreateResourceRequest request
    ) {
        Resource resource = rs.updateResources(
                id,
                request.name(),
                parseType(request.type()),
                request.active() != null ? request.active() : true,
                request.capacity()
        );

        return ResponseEntity.ok(toResponse(resource));
    }

    @GetMapping
    public ResponseEntity<List<ResourceResponse>> getAllResources() {
        List<ResourceResponse> response = rs.getAllResources().stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/inactive")
    public ResponseEntity<List<ResourceResponse>> getInactiveResources() {
        List<ResourceResponse> response = rs.getInactiveResources().stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(response);
    }


    @GetMapping("/{id}")
    public ResponseEntity<ResourceResponse> getResourceById(@PathVariable Long id) {
        return ResponseEntity.ok(toResponse(rs.getResourcesById(id)));
    }

    private ResourceType parseType(String type) {
        return ResourceType.valueOf(type.toUpperCase());
    }

    private ResourceResponse toResponse(Resource resource) {
        return new ResourceResponse(
                resource.getId(),
                resource.getName(),
                resource.getType().name(),
                resource.getCapacity(),
                resource.getActive()
        );
    }
}
