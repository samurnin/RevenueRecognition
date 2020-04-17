package com.samurnin.service;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface RevenueRecognitionService {

    BigDecimal recognizedRevenue(long contractNumber, LocalDate asOf);

    void calculateRevenueRecognitions(long contractNumber);
}
