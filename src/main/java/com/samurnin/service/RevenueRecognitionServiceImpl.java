package com.samurnin.service;

import com.samurnin.gateway.ContractDetails;
import com.samurnin.gateway.Gateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Service(value = "revenueRecognitionService")
public class RevenueRecognitionServiceImpl implements RevenueRecognitionService {

    private Gateway gateway;

    @Autowired
    public RevenueRecognitionServiceImpl(Gateway gateway) {
        this.gateway = gateway;
    }

    @Override
    public BigDecimal recognizedRevenue(long contractNumber, LocalDate asOf) {
        return gateway.findRecognitionsFor(contractNumber, asOf);
    }

    @Override
    public void calculateRevenueRecognitions(long contractNumber) {
        ContractDetails contract = gateway.findContract(contractNumber);
        BigDecimal totalRevenue = contract.getRevenue();
        LocalDate recognitionDate = contract.getDateSigned();
        String type = contract.getType();
        BigDecimal allocation = totalRevenue.divide(new BigDecimal("3"), 2, RoundingMode.HALF_DOWN);
        switch (type) {
            case "S":
                gateway.insertRecognition(contractNumber, allocation, recognitionDate);
                gateway.insertRecognition(contractNumber, allocation, recognitionDate.plusDays(60));
                gateway.insertRecognition(contractNumber, allocation, recognitionDate.plusDays(90));
                break;
            case "D":
                gateway.insertRecognition(contractNumber, allocation, recognitionDate);
                gateway.insertRecognition(contractNumber, allocation, recognitionDate.plusDays(30));
                gateway.insertRecognition(contractNumber, allocation, recognitionDate.plusDays(60));
                break;
            default:
                gateway.insertRecognition(contractNumber, totalRevenue, recognitionDate);
                break;
        }
    }
}
