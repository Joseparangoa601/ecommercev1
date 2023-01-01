package com.convertic.ecommerce.service;

import com.convertic.ecommerce.domain.Order;
import com.convertic.ecommerce.domain.OrderItem;
import com.convertic.ecommerce.domain.enums.OrderStatus;
import com.convertic.ecommerce.repository.CartRepository;
import com.convertic.ecommerce.repository.OrderRepository;
import com.convertic.ecommerce.repository.PaymentRepository;
import com.convertic.ecommerce.web.dto.AddressDto;
import com.convertic.ecommerce.web.dto.CartDto;
import com.convertic.ecommerce.web.dto.OrderDto;
import com.convertic.ecommerce.web.dto.OrderItemDto;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
//@ApplicationScoped
@Transactional
public class OrderService {
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    CartRepository cartRepository;
    @Autowired
    PaymentRepository paymentRepository;
    public static OrderDto mapToDto(Order order){

        var ordertItem = order
                .getOrderItems()
                .stream()
                .map(OrderItemService::mapToDto)
                .collect(Collectors.toSet());

        return new OrderDto(
                order.getId(),
                order.getPrice(),
                order.getStatus().name(),
                order.getShipped(),
                order.getPayment().getId()!=null ? order.getPayment().getId() : null,
                AddressService.mapToDto(order.getShipmentAddress()),
                ordertItem,
                CartService.mapToDto(order.getCart())
        );
    }

    public List<OrderDto> findAll(){
        log.debug("Request to get all Orders");
       return orderRepository.findAll()
                .stream()
                .map(OrderService::mapToDto)
                .collect(Collectors.toList());
    }

    public OrderDto findById(Long id) {
        log.debug("Request to get Order : {}", id);
        return this.orderRepository.findById(id)
                .map(OrderService::mapToDto)
                .orElse(null);
    }

    public List<OrderDto> findAllByUser(Long id) {
        return this.orderRepository.findByCartCustomerId(id)
                .stream()
                .map(OrderService::mapToDto)
                .collect(Collectors.toList());
    }

    public OrderDto create(OrderDto orderDto){
        log.debug("Request to create Order : {}", orderDto);
        var cartId = orderDto.getCart().getId();
        var cart = cartRepository.findById(cartId)
                .orElseThrow(() ->
                        new IllegalStateException("The Cart with ID[" + cartId + "] was not found !"));

        return mapToDto(
                this.orderRepository.save(
                        new Order(
                                BigDecimal.ZERO,
                                OrderStatus.CREATION,
                                null,
                                null,
                                AddressService.createFromDto(orderDto.getShipmentAddress()),
                                Collections.emptySet(),
                                cart
                        )
                )
        );
    }

    public void delete(Long id) {
        log.debug("Request to delete Order : {}", id);
        var order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Order with ID[" + id + "] cannot be found!"));

        Optional.ofNullable(order.getPayment()).ifPresent(paymentRepository::delete);
        orderRepository.delete(order);
    }
    public boolean existsById(Long id) {
        return this.orderRepository.existsById(id);
    }
}
