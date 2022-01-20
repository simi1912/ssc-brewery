package guru.sfg.brewery.web.controllers.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import guru.sfg.brewery.bootstrap.DefaultBreweryLoader;
import guru.sfg.brewery.domain.Beer;
import guru.sfg.brewery.domain.BeerOrder;
import guru.sfg.brewery.domain.Customer;
import guru.sfg.brewery.domain.security.User;
import guru.sfg.brewery.repositories.BeerOrderRepository;
import guru.sfg.brewery.repositories.BeerRepository;
import guru.sfg.brewery.repositories.CustomerRepository;
import guru.sfg.brewery.web.controllers.BaseIT;
import guru.sfg.brewery.web.model.BeerOrderDto;
import guru.sfg.brewery.web.model.BeerOrderLineDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
public class BeerOrderControllerIT extends BaseIT{

    public static final String API_ROOT = "/api/v1/customers/";

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    BeerOrderRepository beerOrderRepository;

    @Autowired
    BeerRepository beerRepository;

    @Autowired
    ObjectMapper objectMapper;

    Customer stPeteCustomer;
    Customer dunedinCustomer;
    Customer keyWestCustomer;
    List<Beer> loadedBeers;

    @BeforeEach
    void setUp() throws Exception {
        stPeteCustomer = customerRepository.findAllByCustomerName(DefaultBreweryLoader.ST_PETE_DISTRIBUTING).orElseThrow();
        dunedinCustomer = customerRepository.findAllByCustomerName(DefaultBreweryLoader.DUNEDIN_DISTRIBUTING).orElseThrow();
        keyWestCustomer = customerRepository.findAllByCustomerName(DefaultBreweryLoader.KEY_WEST_DISTRIBUTORS).orElseThrow();
        loadedBeers = beerRepository.findAll();
    }

    @Test
    void createOrderNoAuth() throws Exception {
        BeerOrderDto beerOrderDto = buildOrderDto(stPeteCustomer, loadedBeers.get(0).getId());

        mockMvc.perform(post(API_ROOT + stPeteCustomer.getId() + "/order")
                    .accept(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(beerOrderDto)))
                .andExpect(status().isUnauthorized());
    }

    @WithUserDetails("simi")
    @Test
    void createOrderUserAdmin() throws Exception {
        BeerOrderDto beerOrderDto = buildOrderDto(stPeteCustomer, loadedBeers.get(0).getId());

        mockMvc.perform(post(API_ROOT + stPeteCustomer.getId() + "/orders")
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(beerOrderDto)))
                .andExpect(status().isCreated());
    }

    @WithUserDetails(DefaultBreweryLoader.STPETE_USER)
    @Test
    void createOrderUserCustomer() throws Exception {
        BeerOrderDto beerOrderDto = buildOrderDto(stPeteCustomer, loadedBeers.get(0).getId());

        mockMvc.perform(post(API_ROOT + stPeteCustomer.getId() + "/orders")
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(beerOrderDto)))
                .andExpect(status().isCreated());
    }

    @WithUserDetails(DefaultBreweryLoader.KEYWEST_USER)
    @Test
    void createOrderUserCustomerNotAuth() throws Exception {
        BeerOrderDto beerOrderDto = buildOrderDto(stPeteCustomer, loadedBeers.get(0).getId());

        mockMvc.perform(post(API_ROOT + stPeteCustomer.getId() + "/orders")
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(beerOrderDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void listOrdersNoAuth() throws Exception {
        mockMvc.perform(get(API_ROOT + stPeteCustomer.getId() + "/orders"))
                .andExpect(status().isUnauthorized());
    }

    @WithUserDetails(value = "simi")
    @Test
    void listOrdersAdmin() throws Exception {
        mockMvc.perform(get(API_ROOT + stPeteCustomer.getId() + "/orders"))
                .andExpect(status().isOk());
    }

    @WithUserDetails(value = DefaultBreweryLoader.STPETE_USER)
    @Test
    void listOrdersCustomerAuth() throws Exception {
        mockMvc.perform(get(API_ROOT + stPeteCustomer.getId() + "/orders"))
                .andExpect(status().isOk());
    }

    @WithUserDetails(value = DefaultBreweryLoader.DUNEDIN_USER)
    @Test
    void listOrderCustomerNOTAuth() throws Exception {
        mockMvc.perform(get(API_ROOT + stPeteCustomer.getId() + "/orders"))
                .andExpect(status().isForbidden());
    }

    @Test
    void listOrderNoAuth() throws Exception {
        mockMvc.perform(get(API_ROOT + stPeteCustomer.getId() + "/orders"))
                .andExpect(status().isUnauthorized());
    }

    @Transactional
    @Test
    void getByOrderIdNotAuth() throws Exception {
        BeerOrder beerOrder = stPeteCustomer.getBeerOrders().stream()
                .findFirst().orElseThrow();

        mockMvc.perform(get(API_ROOT + stPeteCustomer.getId() +
                "/orders" + beerOrder.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Transactional
    @WithUserDetails("simi")
    @Test
    void getByOrderIdADMIN() throws Exception {
        BeerOrder beerOrder = stPeteCustomer.getBeerOrders().stream()
                .findFirst().orElseThrow();

        mockMvc.perform(get(API_ROOT + stPeteCustomer.getId() +
                        "/orders/" + beerOrder.getId()))
                .andExpect(status().is2xxSuccessful());
    }

    @Transactional
    @WithUserDetails(DefaultBreweryLoader.STPETE_USER)
    @Test
    void getByOrderIdCustomerAuth() throws Exception {
        BeerOrder beerOrder = stPeteCustomer.getBeerOrders().stream()
                .findFirst().orElseThrow();

        mockMvc.perform(get(API_ROOT + stPeteCustomer.getId() +
                        "/orders/" + beerOrder.getId()))
                .andExpect(status().is2xxSuccessful());
    }

    @Transactional
    @WithUserDetails(DefaultBreweryLoader.DUNEDIN_USER)
    @Test
    void getByOrderIdCustomerNOTAuth() throws Exception {
        BeerOrder beerOrder = stPeteCustomer.getBeerOrders().stream()
                .findFirst().orElseThrow();

        mockMvc.perform(get(API_ROOT + stPeteCustomer.getId() +
                        "/orders/" + beerOrder.getId()))
                .andExpect(status().isForbidden());
    }

    @Transactional
    @Test
    void pickupOrderNotAuth() throws Exception {
        BeerOrder beerOrder = stPeteCustomer.getBeerOrders().stream()
                .findFirst().orElseThrow();

        BeerOrderDto beerOrderDto = new BeerOrderDto();
        beerOrderDto.setId(beerOrder.getId());

        mockMvc.perform(put(API_ROOT + stPeteCustomer.getId() +
                        "/order/" + beerOrderDto.getId() + "/pickup")
                    .accept(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(beerOrderDto)))
                .andExpect(status().isUnauthorized());
    }

    @Transactional
    @WithUserDetails("simi")
    @Test
    void pickupOrderAdminAuth() throws Exception {
        BeerOrder beerOrder = stPeteCustomer.getBeerOrders().stream()
                .findFirst().orElseThrow();

        BeerOrderDto beerOrderDto = new BeerOrderDto();
        beerOrderDto.setId(beerOrder.getId());

        mockMvc.perform(put(API_ROOT + stPeteCustomer.getId() +
                        "/orders/" + beerOrderDto.getId() + "/pickup")
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(beerOrderDto)))
                .andExpect(status().isNoContent());
    }

    @Transactional
    @WithUserDetails(DefaultBreweryLoader.STPETE_USER)
    @Test
    void pickupOrderCustomerUserAuth() throws Exception {
        BeerOrder beerOrder = stPeteCustomer.getBeerOrders().stream()
                .findFirst().orElseThrow();

        BeerOrderDto beerOrderDto = new BeerOrderDto();
        beerOrderDto.setId(beerOrder.getId());

        mockMvc.perform(put(API_ROOT + stPeteCustomer.getId() +
                        "/orders/" + beerOrderDto.getId() + "/pickup")
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(beerOrderDto)))
                .andExpect(status().isNoContent());
    }

    @Transactional
    @WithUserDetails(DefaultBreweryLoader.DUNEDIN_USER)
    @Test
    void pickupOrderCustomerUserNOTAuth() throws Exception{
        BeerOrder beerOrder = stPeteCustomer.getBeerOrders().stream()
                .findFirst().orElseThrow();

        BeerOrderDto beerOrderDto = new BeerOrderDto();
        beerOrderDto.setId(beerOrder.getId());

        mockMvc.perform(put(API_ROOT + stPeteCustomer.getId() +
                        "/orders/" + beerOrderDto.getId() + "/pickup")
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(beerOrderDto)))
                .andExpect(status().isForbidden());
    }

    private BeerOrderDto buildOrderDto(Customer customer, UUID beerId) {
        List<BeerOrderLineDto> orderLines = List.of(BeerOrderLineDto.builder()
                .id(UUID.randomUUID())
                .beerId(beerId)
                .orderQuantity(5)
                .build());

        return  BeerOrderDto.builder()
                .customerId(customer.getId())
                .customerRef("123")
                .orderStatusCallbackUrl("http://example.com")
                .beerOrderLines(orderLines)
                .build();
    }

}
