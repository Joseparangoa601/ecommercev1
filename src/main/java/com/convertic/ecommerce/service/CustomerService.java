package com.convertic.ecommerce.service;

import com.convertic.ecommerce.domain.Customer;
import com.convertic.ecommerce.repository.CustomerRepository;
import com.convertic.ecommerce.web.dto.CustomerDto;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
//@Slf4j
//@ApplicationScoped //Specifies that this class is application scoped.
@Transactional
public class CustomerService {

    /*
      - The logger.info method prints a logging message in the console
      - There are seven different levels of logging: TRACE, DEBUG, INFO, WARN, ERROR, FATAL,
        and OFF. You can configure the level of logging in your Spring Boot application.
        properties file.
      - If we set the logging level to DEBUG, we can see log messages from levels that are log level
        DEBUG or higher (that is DEBUG, INFO, WARN, and ERROR).
      - When you run the project with DEBUG, you can't see the TRACE messages anymore as it is with INFO.
        DEBUG might be a good setting for a development version of your application. The default logging
         level is INFO if you don't define anything else.
     */
    private final Logger log = LoggerFactory.getLogger(CustomerService.class);
    @Autowired
    CustomerRepository customerRepository;

    public static CustomerDto mapToDto(Customer customer){
        return new CustomerDto(
                customer.getId(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getEmail(),
                customer.getTelephone()
        );
    }

    //Crear un cliente nuevo en la BD y devolverlo a la vista - Dto
    public CustomerDto create(CustomerDto customerDto) {
        log.debug("Request to create Customer : {}", customerDto);
        return mapToDto(
                this.customerRepository.save(
                        new Customer(
                                customerDto.getFirstName(),
                                customerDto.getLastName(),
                                customerDto.getEmail(),
                                customerDto.getTelephone(),
                                Collections.emptySet(),
                                Boolean.TRUE
                        )
                )
        );
    }

    //Obtener un listado de todos los clientes de la BD
    public List<CustomerDto> findAll(){
        log.debug("Request to get all Customers");
        return this.customerRepository.findAll().stream()
                .map(CustomerService::mapToDto).
                collect(Collectors.toList());
    }

    //Obtener un cliente por id de la BD
    public CustomerDto findById(Long id){
        log.debug("Request to get Customer : {}", id);
        return this.customerRepository.findById(id)
                .map(CustomerService::mapToDto)
                .orElse(null);
    }

    //Listado de todos los clientes activos en la BD
    public List<CustomerDto> findAllActive(){
        log.debug("Request to get all active customers");
        return this.customerRepository.findAllByEnabled(true)
                .stream()
                .map(CustomerService::mapToDto)
                .collect(Collectors.toList());
    }

    //Listado de todos los clientes inactivos en la BD
    public List<CustomerDto> findAllInactive(){
        log.debug("Request to get all inactive customers");
        return customerRepository.findAllByEnabled(false)
                .stream()
                .map(CustomerService::mapToDto)
                .collect(Collectors.toList());
    }

    //Borrar cliente de la BD == a cambiar enable TRUE to FALSE
    public void delete(Long id) {
        log.debug("Request to delete Customer : {}", id);
        var customer = this.customerRepository.findById(id).
                orElseThrow(() -> new IllegalStateException("Cannot find Customer with id " + id));
        customer.setEnabled(false);
        this.customerRepository.save(customer);
    }
}
