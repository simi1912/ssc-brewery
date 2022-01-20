package guru.sfg.brewery.web.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
class BreweryControllerIT extends BaseIT{


    @Test
    void getAllBreweriesUserRole() throws Exception {
        mockMvc.perform(get("/brewery/breweries")
                .with(httpBasic("user","password")))
                .andExpect(status().isForbidden());
    }

    @Test
    void getAllBreweriesApiUserRole() throws Exception {
        mockMvc.perform(get("/brewery/api/v1/breweries")
                        .with(httpBasic("user","password")))
                .andExpect(status().isForbidden());
    }

    @Test
    void getAllBreweriesNOAUTH() throws Exception {
        mockMvc.perform(get("/brewery/breweries"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getAllBreweriesCustomerRole() throws Exception {
        mockMvc.perform(get("/brewery/breweries")
                        .with(httpBasic("scott","password")))
                .andExpect(status().isOk());
    }

    @Test
    void getAllBreweriesApiAdminRole() throws Exception {
        mockMvc.perform(get("/brewery/api/v1/breweries")
                        .with(httpBasic("simi","password")))
                .andExpect(status().isOk());
    }

    @Test
    void getAllBreweriesApiNOAUTH() throws Exception {
        mockMvc.perform(get("/brewery/api/v1/breweries"))
                .andExpect(status().isUnauthorized());
    }

}