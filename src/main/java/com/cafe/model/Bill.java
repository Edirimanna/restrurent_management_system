package com.cafe.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.io.Serializable;

@Data
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "bill")
public class Bill implements Serializable {

    private static final long serialVersionUID =1L;

    @jakarta.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "uuid")
    private String uuid;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "contactnumber")
    private String contactNumber;

    @Column(name = "paymentmethod")
    private String paymentMethod;

    @Column(name = "total")
    private Integer total;

    @Column(name = "productdetails")
    private String productDetails;

    @Column(name = "createdby")
    private String createdBy;

}
