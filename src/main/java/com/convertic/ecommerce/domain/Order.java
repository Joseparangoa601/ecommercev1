package com.convertic.ecommerce.domain;

import com.convertic.ecommerce.domain.enums.OrderStatus;
import com.convertic.ecommerce.domain.enums.ProductStatus;
import jakarta.persistence.*;
import lombok.*;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * A Order.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@Entity
@Table(name = "orders")
public class Order extends AbstractEntity {

    @NotNull
    @Column(name = "total_price", precision = 10, scale = 2, nullable = false)
    private BigDecimal price;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status;

    @Column(name = "shipped")
    private ZonedDateTime shipped;

    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(unique = true)
    private Payment payment;

    @Embedded
    private Address shipmentAddress;

    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private Set<OrderItem> orderItems;

    @OneToOne
    private Cart cart;
}

