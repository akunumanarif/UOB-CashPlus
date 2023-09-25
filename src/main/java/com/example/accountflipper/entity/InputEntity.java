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

    private Long accountNumber;

//    public InputEntity(String accountNumber, String amount) {
//        this.accountNumber = accountNumber;
//        this.amount = amount;
//    }

    private String amount;

    private Boolean isFlipped;

    private int feeCharge;

    public InputEntity(String inputRowValue, Long accountNumber, String amount, Boolean isFlipped, int feeCharge) {
        this.accountNumber = accountNumber;
        this.amount = amount;
        this.isFlipped = isFlipped;
        this.feeCharge = feeCharge;
    }
}
