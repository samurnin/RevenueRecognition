package com.samurnin;

import com.samurnin.gateway.DatabaseHelper;
import com.samurnin.gateway.Gateway;
import com.samurnin.service.RevenueRecognitionService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Main {

    private static Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        ConfigurableApplicationContext context = new ClassPathXmlApplicationContext(
                "classpath:META-INF/spring/applicationContext.xml");

        logger.info("Welcome to Revenue Recognition app!");

        RevenueRecognitionService service = context.getBean(RevenueRecognitionService.class);
        DatabaseHelper helper = context.getBean(DatabaseHelper.class);
        Gateway gateway = context.getBean(Gateway.class);

        logger.info("Creating the project database... ");
        helper.createDb();

        logger.info("Creating Company database... ");
        gateway.createTables();
        int spreadSheetId = gateway.addProduct("My Spreadsheet", "S");
        int dataBaseId = gateway.addProduct("My DB", "D");
        int wordId = gateway.addProduct("My Word", "W");

        int contractOnSpreadSheetId = gateway.addContract(spreadSheetId, new BigDecimal("100000.00"), LocalDate.of(2019, 12, 1));
        int contractOnDatabaseId = gateway.addContract(dataBaseId, new BigDecimal("200000.00"), LocalDate.of(2019, 12, 1));
        int contractOnWordId = gateway.addContract(wordId, new BigDecimal("5000.00"), LocalDate.of(2019, 12, 1));
        service.calculateRevenueRecognitions(contractOnSpreadSheetId);
        service.calculateRevenueRecognitions(contractOnDatabaseId);
        service.calculateRevenueRecognitions(contractOnWordId);
        logger.info(service.recognizedRevenue(contractOnSpreadSheetId, LocalDate.of(2020, 1, 1)));
        logger.info(service.recognizedRevenue(contractOnSpreadSheetId, LocalDate.of(2020, 2, 1)));
        logger.info(service.recognizedRevenue(contractOnDatabaseId, LocalDate.of(2020, 1, 1)));
        logger.info(service.recognizedRevenue(contractOnDatabaseId, LocalDate.of(2019, 12, 29)));
        logger.info(service.recognizedRevenue(contractOnWordId, LocalDate.now()));

        logger.info("Deleting the project database... ");
        helper.deleteDb();
        context.close();
    }
}
