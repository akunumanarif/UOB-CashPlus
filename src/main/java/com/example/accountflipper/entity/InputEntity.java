package com.example.accountflipper.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
public class InputEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String accountNumber;

    public InputEntity(Long id, String accountNumber, String amount) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.amount = amount;
    }

    private String amount;
}