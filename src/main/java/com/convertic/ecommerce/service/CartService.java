package com.convertic.ecommerce.service;

import com.convertic.ecommerce.domain.Cart;
import com.convertic.ecommerce.repository.CartRepository;
import com.convertic.ecommerce.repository.ProductRepository;
import com.convertic.ecommerce.web.dto.CartDto;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

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
    ProductRepository productRepository;


}