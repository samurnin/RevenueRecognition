package com.samurnin.service;

import java.time.LocalDate;

public interface RevenueRecognitionService {

    void recognizedRevenue (long contractNumber, LocalDate asOf);

    void calculateRevenueRecognitions (long contractNumber);
}
