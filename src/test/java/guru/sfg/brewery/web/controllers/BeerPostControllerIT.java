package guru.sfg.brewery.web.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
public class BeerPostControllerIT extends BaseIT{

    @Test
    public void initCreationForm() throws Exception{
        mockMvc.perform(get("/beers/new")
                        .with(httpBasic("simi","password")))
                .andExpect(status().isOk())
                .andExpect(view().name("beers/createBeer"))
                .andExpect(model().attributeExists("beer"));
    }

//    @Test
//    public void findBeers() throws Exception{
//        mockMvc.perform(get("/api/v1/beer"))
//                .andExpect(status().isOk());
//    }

//    @Test
//    public void findBeerById() throws Exception{
//        mockMvc.perform(get("/api/v1/beer/f93b0cba-4f57-49a0-87ce-63f21b05feda"))
//                .andExpect(status().isOk());
//    }

//    @Test
//    public void findBeerByUpc() throws Exception {
//        mockMvc.perform(get("/api/v1/beerUpc/0631234200036"))
//                .andExpect(status().isOk());
//    }

}
