package com.convertic.ecommerce.repository;

import com.convertic.ecommerce.domain.Cart;
import com.convertic.ecommerce.domain.enums.CartStatus;
import com.convertic.ecommerce.web.dto.CartDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    List<Cart> findByStatus(CartStatus status);

    List<Cart> findByStatusAndCustomerId(CartStatus status, Long customerId);

}