package com.example.ae5.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Invoice {
    private Long id;
    private String number;
    private String client;
    private LocalDate date;
    private Double amount;
    private String status;
}
