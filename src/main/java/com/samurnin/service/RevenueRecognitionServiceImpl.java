package com.samurnin.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service(value = "revenueRecognitionService")
public class RevenueRecognitionServiceImpl implements RevenueRecognitionService {

    @Override
    public void recognizedRevenue(long contractNumber, LocalDate asOf) {

    }

    @Override
    public void calculateRevenueRecognitions(long contractNumber) {

    }
}
