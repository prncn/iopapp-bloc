package org.cgi.models;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Device {
    String id;
    String haId;
    String name;
    String description;
    String type;
    List<SmartOrder> smartOrders = new ArrayList<>();

    public boolean addSmartOrder(SmartOrder s) {
        return smartOrders.add(s);
    }

    public void updateSmartOrder(int index, SmartOrder s) {
        smartOrders.set(index, s);
    }

    public void deleteSmartOrder(int index) {
        smartOrders.remove(index);
    }

    @Override
    public String toString() {
        return id;
    }
}
