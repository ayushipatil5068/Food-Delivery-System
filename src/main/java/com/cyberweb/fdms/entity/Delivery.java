package com.cyberweb.fdms.entity;



import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "deliveries")
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Order
    @OneToOne
    @JoinColumn(name = "order_id")
    private Order order;

    // Delivery agent
    @ManyToOne
    @JoinColumn(name = "delivery_agent_id")
    private User deliveryAgent;

    private String status;  
    // ASSIGNED, PICKED_UP, DELIVERED

    private LocalDateTime pickupTime;
    private LocalDateTime deliveryTime;

    public Delivery() {}

    // Getters & Setters
    public Long getId() {
        return id;
    }

    public Order getOrder() {
        return order;
    }
    
    public void setOrder(Order order) {
        this.order = order;
    }

    public User getDeliveryAgent() {
        return deliveryAgent;
    }
    
    public void setDeliveryAgent(User deliveryAgent) {
        this.deliveryAgent = deliveryAgent;
    }

    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getPickupTime() {
        return pickupTime;
    }
    
    public void setPickupTime(LocalDateTime pickupTime) {
        this.pickupTime = pickupTime;
    }

    public LocalDateTime getDeliveryTime() {
        return deliveryTime;
    }
    
    public void setDeliveryTime(LocalDateTime deliveryTime) {
        this.deliveryTime = deliveryTime;
    }
}

