package com.mallconnect.repository;

import com.mallconnect.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByOrderNumber(String orderNumber);

    Page<Order> findByBuyerId(Long buyerId, Pageable pageable);

    List<Order> findByBuyerId(Long buyerId);

    @org.springframework.data.jpa.repository.Query(
        "SELECT DISTINCT o FROM Order o JOIN o.items oi JOIN oi.product p WHERE p.seller.id = :sellerId")
    Page<Order> findOrdersBySellerId(@org.springframework.data.repository.query.Param("sellerId") Long sellerId, Pageable pageable);

    @org.springframework.data.jpa.repository.Query(
        "SELECT COUNT(DISTINCT o) FROM Order o JOIN o.items oi JOIN oi.product p WHERE p.seller.id = :sellerId")
    long countBySellerId(@org.springframework.data.repository.query.Param("sellerId") Long sellerId);

    @org.springframework.data.jpa.repository.Query(
        "SELECT DISTINCT o FROM Order o JOIN o.items oi JOIN oi.product p WHERE p.seller.id = :sellerId")
    List<Order> findBySellerId(@org.springframework.data.repository.query.Param("sellerId") Long sellerId);
}
