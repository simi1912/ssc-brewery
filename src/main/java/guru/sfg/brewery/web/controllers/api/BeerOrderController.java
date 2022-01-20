package guru.sfg.brewery.web.controllers.api;

import guru.sfg.brewery.services.BeerOrderService;
import guru.sfg.brewery.web.model.BeerOrderDto;
import guru.sfg.brewery.web.model.BeerOrderPagedList;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequestMapping("/api/v1/customers/{customerId}")
@RestController
public class BeerOrderController{

    private static final Integer DEFAULT_PAGE_NUMBER = 0;
    private static final Integer DEFAULT_PAGE_SIZE = 25;

    private final BeerOrderService beerOrderService;

    public BeerOrderController(BeerOrderService beerOrderService) {
        this.beerOrderService = beerOrderService;
    }

    @PreAuthorize("hasAnyAuthority('order.read') OR " +
            "hasAnyAuthority('customer.order.read') AND " +
            "@beerOrderAuthenticationManager.customerIdMatches(authentication, #customerId)")
    @GetMapping("/orders")
    public BeerOrderPagedList listOrders(@PathVariable("customerId") UUID customerId,
                                         @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                         @RequestParam(value = "pageSize", required = false) Integer pageSize){
        if(pageNumber == null || pageNumber < 0)
            pageNumber = DEFAULT_PAGE_NUMBER;
        if(pageSize == null || pageSize < 0)
            pageSize = DEFAULT_PAGE_SIZE;

        return  beerOrderService.listOrders(customerId, PageRequest.of(pageNumber, pageSize));
    }

    @PreAuthorize("hasAnyAuthority('order.create') OR " +
            "hasAnyAuthority('customer.order.create') AND " +
            "@beerOrderAuthenticationManager.customerIdMatches(authentication, #customerId)")
    @PostMapping("/orders")
    @ResponseStatus(HttpStatus.CREATED)
    public BeerOrderDto placeOrder(@PathVariable UUID customerId,
                                   @RequestBody BeerOrderDto beerOrderDto){
        return beerOrderService.placeOrder(customerId, beerOrderDto);
    }

    @PreAuthorize("hasAnyAuthority('order.read') OR " +
            "hasAnyAuthority('customer.order.read') AND " +
            "@beerOrderAuthenticationManager.customerIdMatches(authentication, #customerId)")
    @GetMapping("/orders/{orderId}")
    public BeerOrderDto getOrder(@PathVariable UUID customerId,
                                 @PathVariable UUID orderId){
        return beerOrderService.getOrderById(customerId, orderId);
    }

    @PreAuthorize("hasAnyAuthority('order.create') OR " +
            "hasAnyAuthority('customer.order.create') AND " +
            "@beerOrderAuthenticationManager.customerIdMatches(authentication, #customerId)")
    @PutMapping("/orders/{orderId}/pickup")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void pickupOrder(@PathVariable UUID customerId,
                            @PathVariable UUID orderId){
        beerOrderService.pickupOrder(customerId, orderId);
    }

}