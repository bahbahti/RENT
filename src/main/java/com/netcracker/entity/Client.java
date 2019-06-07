package com.netcracker.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "client")
@Data
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "second_name", nullable = false)
    private String secondName;


}
