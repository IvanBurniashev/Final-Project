package com.example.finalproject.controller;

import com.example.finalproject.dto.requestdto.OrderRequestDto;
import com.example.finalproject.dto.responsedto.OrderResponseDto;
import com.example.finalproject.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@Tag(name = "Order controller", description = "Controller for managing user's orders")
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/orders")
@Validated
public class OrderController {
    private final OrderService orderService;

    @Operation(summary = "Getting order by id", description = "Provides functionality for getting user's order by order id")
    @GetMapping(value = "/{orderId}")
    @ResponseStatus(HttpStatus.OK)
    public OrderResponseDto getOrderById(@PathVariable
                                             @Min(value = 1, message = "Invalid ID: Id must be greater than or equal to 1")
                                             @Parameter(description = "Order identifier") Long orderId) {
        return orderService.getOrderById(orderId);
    }

    @Operation(summary = "Getting order history", description = "Provides functionality for getting all orders of a user ")
    @GetMapping(value = "/history/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public Set<OrderResponseDto> getOrderHistoryByUserId(@PathVariable
                                                             @Min(value = 1, message = "Invalid ID: Id must be greater than or equal to 1")
                                                             @Parameter(description = "User identifier") Long userId) {
        return orderService.getOrderHistoryByUserId(userId);
    }

    @Operation(summary = "Inserting a new order", description = "Provides functionality for inserting a new order")
    @PostMapping(value = "/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void insertOrder(@RequestBody @Valid OrderRequestDto orderRequestDto,

                            @PathVariable
                            @Min(value = 1, message = "Invalid ID: Id must be greater than or equal to 1")
                            @Parameter(description = "User identifier") Long userId) {
        orderService.insertOrder(orderRequestDto, userId);
    }

    @Operation(summary = "Canceling an order", description = "Provides functionality for canceling an already placed order")
    @PutMapping(value = "/{orderId}")
    @ResponseStatus(HttpStatus.OK)
    public void cancelOrder(@PathVariable
                                @Min(value = 1, message = "Invalid ID: Id must be greater than or equal to 1")
                                @Parameter(description = "Order identifier") Long orderId){
        orderService.cancelOrder(orderId);
    }
}
