package com.convertic.ecommerce.service;

import com.convertic.ecommerce.domain.Cart;
import com.convertic.ecommerce.domain.enums.CartStatus;
import com.convertic.ecommerce.repository.CartRepository;
import com.convertic.ecommerce.repository.CustomerRepository;
import com.convertic.ecommerce.web.dto.CartDto;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

/*
@Slf4j
The Lombok annotation used to generate a logger in the class.
When used, you have a static final log field, initialized to
the name of your class, which you can then use to write log
statements.

La anotación @Transactional proporciona a la aplicación la
capacidad de controlar de forma declarativa los límites de
las transacciones.
 */

@Slf4j
//@ApplicationScoped //Specifies that this class is application scoped.
@Transactional
public class CartService {
    @Autowired
    CartRepository cartRepository;
    @Autowired
    CustomerRepository customerRepository;

    public CartService(CartRepository cartRepository, CustomerRepository customerRepository) {
        this.cartRepository = cartRepository;
        this.customerRepository = customerRepository;
    }

    public static CartDto mapToDto(Cart cart){
        return new CartDto(
                cart.getId(),
                CustomerService.mapToDto(cart.getCustomer()),
                cart.getStatus().name()
                );
    }

    //Listar todos los carritos activos
    public List<CartDto> findAllNewCarts() {
        return cartRepository.findByStatus(CartStatus.NEW)
                .stream()
                .map(CartService::mapToDto)
                .collect(Collectors.toList());
    }

    //Crear un nuevo carrito en la BD si el cliente no tiene uno activo
    public Cart create(Long customerId){
        if(!getActiveCart(customerId)){
            var customer = customerRepository.findById(customerId)
                    .orElseThrow(()-> new IllegalStateException("The Customer does not exist!"));
            var cart = new Cart(customer, CartStatus.NEW);
            return this.cartRepository.save(cart);
    }else {
            throw new IllegalStateException("There is already an active cart");
        }
    }

    //Devolver al front el carrito creado para el customerId
    public CartDto createDto(Long customerId){
        return CartService.mapToDto(this.create(customerId));
    }

    //metodo para saber si un customer tiene un carrito activo
    public boolean getActiveCart(Long customerId){
        var carts = cartRepository.findByStatusAndCustomerId(CartStatus.NEW, customerId)
                .parallelStream()
                .findAny()
                .isPresent();
        if (carts) {
            return true;
        }
        return false;

    };

    //Listar todos los carritos cancelados
    public List<CartDto> findAllCanceledCarts() {
        return cartRepository.findByStatus(CartStatus.CANCELED)
                .stream()
                .map(CartService::mapToDto)
                .collect(Collectors.toList());
    }

    //Listar todos los carritos confirmados
    public List<CartDto> findAllConfirmedCarts() {
        return cartRepository.findByStatus(CartStatus.CONFIRMED)
                .stream()
                .map(CartService::mapToDto)
                .collect(Collectors.toList());
    }

    //Buscar un carrito por id
    public CartDto findById(Long id) {
        log.debug("Request to get Cart : {}", id);
        return this.cartRepository.findById(id)
                .map(CartService::mapToDto)
                .orElse(null);
    }

    //borrar carrito
    public void delete(Long id) {
        log.debug("Request to delete Cart : {}", id);
        var cart = this.cartRepository.findById(id)
                .orElseThrow(()-> new IllegalStateException("\"Cannot find cart with id \" + id"));
        cart.setStatus(CartStatus.CANCELED);
        this.cartRepository.save(cart);
    }

}