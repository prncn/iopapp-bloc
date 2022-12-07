package org.cgi.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Address {
    String id;
    String firstName;
    String lastName;
    String streetName;
    Integer streetNo;
    Integer postCode;
    String city;
    boolean primary;

    @Override
    public String toString() {
        return id;
    }
}
