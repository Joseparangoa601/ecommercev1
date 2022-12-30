package com.convertic.ecommerce.service;

import com.convertic.ecommerce.domain.Order;
import com.convertic.ecommerce.domain.OrderItem;
import com.convertic.ecommerce.repository.OrderItemRepository;
import com.convertic.ecommerce.repository.OrderRepository;
import com.convertic.ecommerce.repository.ProductRepository;
import com.convertic.ecommerce.web.dto.OrderDto;
import com.convertic.ecommerce.web.dto.OrderItemDto;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.LifecycleState;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
//@ApplicationScoped //Specifies that this class is application scoped.
//@ApplicationScoped
@Transactional
public class OrderItemService {
    @Autowired
    OrderItemRepository orderItemRepository;
    public static OrderItemDto mapToDto(OrderItem orderItem){
        return new OrderItemDto(
                orderItem.getId(),
                orderItem.getQuantity(),
                orderItem.getProduct().getId(),
                orderItem.getOrder().getId()
        );
    }

    public OrderItemDto findById(Long id){
        log.debug("Request to get OrderItem : {}", id);
        return this.orderItemRepository.findById(id).
                map(OrderItemService::mapToDto).
                orElse(null);
    }
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    ProductRepository productRepository;

    //Adicionar un item a la orden, i.e., ingresar un item al carrito
    public OrderItemDto create(OrderItemDto orderItemDto){
        log.debug("Request to create OrderItem : {}", orderItemDto);
        //var order = orderRepository.findById(orderItemDto.getId())
        //        .orElseThrow(()->new IllegalStateException("The Order does not exist!"));
        var order = orderRepository.findById(orderItemDto.getId())
                .orElseGet(Order::new);

        var product = productRepository.findById(orderItemDto.getProductId())
                .orElseThrow(()->new IllegalStateException("The Product does not exist!"));

        var orderItem = orderItemRepository.save(
                new OrderItem(
                orderItemDto.getQuantity(),
                product,
                order
        ));
        //order.setPrice((order.getPrice().add(orderItem.getOrder().getPrice())));
        BigDecimal q = BigDecimal.valueOf((orderItem.getQuantity()));
        order.setPrice((order.getPrice().add(orderItem.getOrder().getPrice()).multiply(q)));
        this.orderRepository.save(order);
        return mapToDto(orderItem);
    }

    //Eliminar un item de la orden/carrito
    public void delete(Long id) {
        log.debug("Request to delete OrderItem : {}", id);
        var  orderItem = orderItemRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("The OrderItem does not exist!"));

        //*restar a la orden el precio del item
        var order = orderItem.getOrder();
        order.setPrice(order.getPrice()
                .subtract(orderItem.getProduct().getPrice()));

        //*quitar de la orden el item que se removió
        order.getOrderItems().remove(orderItem);

        this.orderRepository.save(order);

    }

    public List<OrderItemDto> findByOrderId(Long id){
        log.debug("Request to get all OrderItems of OrderId {}", id);
        return this.orderItemRepository.findById(id)
                        .stream()
                        .map(orderItem -> mapToDto(orderItem))
                        //.map(orderItem -> mapToDto(OrderItemService::mapToDto))
                        .collect(Collectors.toList());
    }

}
