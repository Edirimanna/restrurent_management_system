package com.cafe.wrapper;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserWrapper {
    private Integer id;
    private String name;
    private String email;
    private String contactNumber;
    private String status;

    public UserWrapper(Integer id, String name, String email, String contactNumber, String status) {
        this.id = id;
        this.status = status;
        this.contactNumber = contactNumber;
        this.email = email;
        this.name = name;
    }
}
