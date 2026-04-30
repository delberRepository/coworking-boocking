package com.delber.coworking_booking.controller;

import com.delber.coworking_booking.exception.GlobalExceptionHandler;
import com.delber.coworking_booking.model.Resource;
import com.delber.coworking_booking.model.ResourceType;
import com.delber.coworking_booking.security.JwtAuthenticationFilter;
import com.delber.coworking_booking.service.CustomUserDetailsService;
import com.delber.coworking_booking.service.ResourceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ResourcesController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class ResourcesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ResourceService resourceService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void createResourceShouldReturnBadRequestWhenBodyIsInvalid() throws Exception {
        String body = """
                {
                  "name": "",
                  "type": "",
                  "capacity": 0,
                  "active": true
                }
                """;

        mockMvc.perform(post("/resources")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Datos de entrada no validos"))
                .andExpect(jsonPath("$.fields").isArray());
    }

    @Test
    void createResourceShouldReturnBadRequestWhenTypeIsUnknown() throws Exception {
        String body = """
                {
                  "name": "Mesa grande",
                  "type": "desk",
                  "capacity": 8,
                  "active": true
                }
                """;

        mockMvc.perform(post("/resources")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Valor no valido: No enum constant com.delber.coworking_booking.model.ResourceType.DESK"));
    }

    @Test
    void createResourceShouldReturnOkWhenBodyIsValid() throws Exception {
        String body = """
                {
                  "name": "Sala Norte",
                  "type": "SALA_DANZA",
                  "capacity": 10,
                  "active": true
                }
                """;

        Resource resource = new Resource();
        resource.setId(3L);
        resource.setName("Sala Norte");
        resource.setType(ResourceType.SALA_DANZA);
        resource.setCapacity(10);
        resource.setActive(true);

        when(resourceService.createResources(eq("Sala Norte"), eq(ResourceType.SALA_DANZA), eq(true), eq(10)))
                .thenReturn(resource);

        mockMvc.perform(post("/resources")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.name").value("Sala Norte"))
                .andExpect(jsonPath("$.type").value("SALA_DANZA"))
                .andExpect(jsonPath("$.capacity").value(10))
                .andExpect(jsonPath("$.active").value(true));
    }
}
