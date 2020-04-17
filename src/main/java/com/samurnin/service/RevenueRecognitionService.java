package com.samurnin.service;

import org.joda.money.Money;

import java.time.LocalDate;

public interface RevenueRecognitionService {

    Money recognizedRevenue(long contractNumber, LocalDate asOf);

    void calculateRevenueRecognitions(long contractNumber);
}
