package com.samurnin.service;

import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service(value = "revenueRecognitionService")
public class RevenueRecognitionServiceImpl implements RevenueRecognitionService {

    @Override
    public void recognizedRevenue(long contractNumber, LocalDate asOf) {
        Money result = Money.zero(CurrencyUnit.USD);
        ResultSet rs = db.findRecognitionsFor(contractNumber, asOf);
        while (rs.next()) {
            result = result.add(Money.dollars(rs.getBigDecimal("amount")));
        }
        return result;
    }

    @Override
    public void calculateRevenueRecognitions(long contractNumber) {
        ResultSet contracts = db.findContract(contractNumber);
        contracts.next();
        Money totalRevenue = Money.dollars(contracts.getBigDecimal("revenue"));
        LocalDate recognitionDate = new MfDate(contracts.getDate("dateSigned"));
        String type = contracts.getString("type");
        if (type.equals("S")) {
            Money[] allocation = totalRevenue.allocate(3);
            db.insertRecognition
                    (contractNumber, allocation[0], recognitionDate);
            db.insertRecognition
                    (contractNumber, allocation[1], recognitionDate.addDays(60));
            db.insertRecognition
                    (contractNumber, allocation[2], recognitionDate.addDays(90));
        } else if (type.equals("W")) {
            db.insertRecognition(contractNumber, totalRevenue, recognitionDate);
        } else if (type.equals("D")) {
            Money[] allocation = totalRevenue.allocate(3);
            db.insertRecognition
                    (contractNumber, allocation[0], recognitionDate);
            db.insertRecognition
                    (contractNumber, allocation[1], recognitionDate.addDays(30));
            db.insertRecognition
                    (contractNumber, allocation[2], recognitionDate.addDays(60));
        }
    }
}
