package org.cgi.models;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Shop {
    private String id;
    private String name;
    private Boolean isRefused;
    private List<PaymentMethod> paymentMethods;

    @Override
    public String toString() {
        return id;
    }
}
