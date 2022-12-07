package org.cgi.models;

import java.rmi.server.UID;
import java.time.LocalDate;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Order {
    String id = new UID().toString();
    final SmartOrder contract;
    final int price;
    LocalDate date = LocalDate.now();

    @Override
    public String toString() {
        return id;
    }
}
