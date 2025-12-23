package com.fpl.edu.shoeStore.payment.mapper;

import com.fpl.edu.shoeStore.payment.entity.Payment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface PaymentMapper {

    List<Payment> findAll();

    Payment findById(@Param("id") Integer id);

    Payment findByTransactionRef(@Param("transactionRef") String transactionRef);
    
    Payment findByOrderId(@Param("orderId") Integer orderId);

    int insert(Payment payment);

    int update(Payment payment);
    
    int updatePaymentStatus(
            @Param("transactionRef") String transactionRef,
            @Param("status") String status,
            @Param("gatewayTransactionId") String gatewayTransactionId,
            @Param("bankCode") String bankCode
    );

    int deleteById(@Param("id") int id);

    List<Payment> findAllPaged(
            @Param("paymentId") Integer paymentId,
            @Param("orderId") Integer orderId,
            @Param("payerId") Integer payerId,
            @Param("paymentMethod") String paymentMethod,
            @Param("paymentDate") LocalDateTime paymentDate,
            @Param("status") String status,
            @Param("amount") BigDecimal amount,
            @Param("transactionRef") String transactionRef,
            @Param("offset") int offset,
            @Param("size") int size
    );

    long countAll(
            @Param("paymentId") Integer paymentId,
            @Param("orderId") Integer orderId,
            @Param("payerId") Integer payerId,
            @Param("paymentMethod") String paymentMethod,
            @Param("paymentDate") LocalDateTime paymentDate,
            @Param("status") String status,
            @Param("amount") BigDecimal amount,
            @Param("transactionRef") String transactionRef
    );

}