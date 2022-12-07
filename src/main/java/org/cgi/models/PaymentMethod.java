package org.cgi.models;

import java.rmi.server.UID;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class PaymentMethod {
    String id = new UID().toString();;
    @NonNull
    String type;
    @NonNull
    String details;

    @Override
    public String toString() {
        return id;
    }
}
