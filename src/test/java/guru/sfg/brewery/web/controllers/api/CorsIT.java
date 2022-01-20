package guru.sfg.brewery.web.controllers.api;

import guru.sfg.brewery.web.controllers.BaseIT;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
public class CorsIT extends BaseIT{

    @WithUserDetails("simi")
    @Test
    void findBeersAUTH() throws Exception {
        mockMvc.perform(get("/api/v1/beer")
                    .header("Origin", "https://springframework.guru"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "*"));
    }

    @Test
    void findBeersPREFLIGHT() throws Exception {
        mockMvc.perform(options("/api/v1/beer")
                .header("Origin", "https://springframework.guru")
                .header("Access-Control-Allow-Origin", "GET"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "*"));
    }

    @Test
    void postBeersPREFLIGHT() throws Exception {
        mockMvc.perform(options("/api/v1/beer")
                        .header("Origin", "https://springframework.guru")
                        .header("Access-Control-Allow-Origin", "POST"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "*"));
    }

    @Test
    void putBeersPREFLIGHT() throws Exception {
        mockMvc.perform(options("/api/v1/beer")
                        .header("Origin", "https://springframework.guru")
                        .header("Access-Control-Allow-Origin", "put"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "*"));
    }

    @Test
    void deleteBeersPREFLIGHT() throws Exception {
        mockMvc.perform(options("/api/v1/beer")
                        .header("Origin", "https://springframework.guru")
                        .header("Access-Control-Allow-Origin", "DELETE"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "*"));
    }

}
