package com.convertic.ecommerce.service;

import com.convertic.ecommerce.domain.Order;
import com.convertic.ecommerce.domain.Payment;
import com.convertic.ecommerce.domain.enums.OrderStatus;
import com.convertic.ecommerce.domain.enums.PaymentStatus;
import com.convertic.ecommerce.repository.OrderRepository;
import com.convertic.ecommerce.repository.PaymentRepository;
import com.convertic.ecommerce.web.dto.PaymentDto;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
//@ApplicationScoped
@Transactional
public class PaymentService {
    @Autowired
    PaymentRepository paymentRepository;
    @Autowired
    OrderRepository orderRepository;

    public static PaymentDto mapToDto(Payment payment, Long orderId){
        if (payment != null){
            return new PaymentDto(
                    payment.getId(),
                    payment.getPaypalPaymentId(),
                    payment.getStatus().name(),
                    orderId
            );
        }
        return null;
    }

    //Paso 11, como Payment tiene el atributo orderId, traer la orden por paymentId
    public Order findOrderByPaymentId(Long paymentId){
        return this.orderRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new IllegalStateException("No Order exists for the Payment ID " + paymentId));
    }

    //Buscar pagos menores un cierto valor
    public List<PaymentDto> findByPriceRange(Double max){
        return this.paymentRepository.findAllByAmountBetween(BigDecimal.ZERO, BigDecimal.valueOf(max))
                .stream()
                .map(payment -> mapToDto(payment, findOrderByPaymentId(payment.getId()).getId()))
                .collect(Collectors.toList());
    }

    public PaymentDto findById(Long paymentId) {
        log.debug("Request to get Payment : {}", paymentId);

        //orden asociada a paymentId
        var order = findOrderByPaymentId(paymentId);

        return this.paymentRepository.findById(paymentId)
                .map(payment -> mapToDto(payment, order.getId()))
                .orElse(null);
    }

    public PaymentDto create(PaymentDto paymentDto) {
        log.debug("Request to create Payment : {}", paymentDto);

       var order = this.orderRepository.findById(paymentDto.getOrderId())
               .orElseThrow(() -> new IllegalStateException("The Order does not exist!"));

        var payment = this.paymentRepository.save(new Payment(
                        paymentDto.getPaypalPaymentId(),
                        PaymentStatus.valueOf(paymentDto.getStatus()),
                        order.getPrice()));

        // *** CON EL PAGO REALIZADO CAMBIA EL ESTADO DE LA ORDEN ***
        order.setStatus(OrderStatus.PAID);
        orderRepository.save(order);
        return mapToDto(payment, order.getId());
    }

    public void delete(Long id) {
        log.debug("Request to delete Payment : {}", id);
        this.paymentRepository.deleteById(id);
    }
}