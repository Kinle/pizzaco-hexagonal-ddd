package com.pizzaco.order.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "orders")
@NoArgsConstructor
@AllArgsConstructor
public class OrderEntity {
    @Id private UUID id;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private String deliveryAddress;

    @Column(nullable = false)
    private String customerContact;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal deliveryFee;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal total;

    @Column(nullable = false)
    private Integer etaMinutes;

    private String paymentReference;

    @Column(nullable = false)
    private Instant placedAt;

    @Lob
    @Column(nullable = false, columnDefinition = "CLOB")
    private String itemsJson;
}
