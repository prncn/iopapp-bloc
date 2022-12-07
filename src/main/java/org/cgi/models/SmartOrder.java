package org.cgi.models;

import java.rmi.server.UID;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SmartOrder {
    String id = new UID().toString();
    String productName;
    String productCategory;
    String linkedTo;
    Integer unitPrice;
    Integer monthlyLimit;
    Shop merchant;
    PaymentMethod paymentMethod;
    Address address;
    Boolean isLowSupplyNotification;
    Boolean isAutomaticOrdering;
    Boolean isPaused;
    List<String> orders;

    public boolean addOrder(Order order) {
        return orders.add(order.id);
    }

    @Override
    public String toString() {
        return id;
    }
}
