package com.halycon.transactionserviceejbjaxrs.service;

import com.halycon.transactionserviceejbjaxrs.domain.Transaction;
import com.halycon.transactionserviceejbjaxrs.domain.TransactionStatistics;
import com.halycon.transactionserviceejbjaxrs.repository.TransactionRepository;
import com.halycon.transactionserviceejbjaxrs.repository.impl.TransactionInMemoryRepository;
import com.halycon.transactionserviceejbjaxrs.service.impl.TransactionService;
import com.halycon.transactionserviceejbjaxrs.service.impl.TransactionStatisticsService;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@RunWith(Arquillian.class)
public class TransactionStatisticsServiceTest {

    private TransactionStatisticsService transactionStatisticsStatisticsService;

    private TransactionRepository transactionRepository;


    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class).
                addClasses(TransactionInMemoryRepository.class, Transaction.class, TransactionStatistics.class,
                        TransactionRepository.class, TransactionStatisticsService.class, StatisticsService.class).
                addAsWebInfResource(EmptyAsset.INSTANCE, ArchivePaths.create("beans.xml"));
    }

    @Before
    public void init() {
        transactionStatisticsStatisticsService = new TransactionStatisticsService();
        transactionRepository = new TransactionInMemoryRepository();
        transactionStatisticsStatisticsService.setTransactionRepository(transactionRepository);
    }

    @Test
    public void getStatisticsOfaTimePeriod_WithoutTransactions_ReturnsEmptytObject() {
        Instant now = Instant.now();

        TransactionStatistics emptyTransactionStatistics = new TransactionStatistics();

        TransactionStatistics transactionStatistics = transactionStatisticsStatisticsService.
                getStatisticsOfaTimePeriod(now.minus(1, ChronoUnit.MINUTES), now);

        Assert.assertEquals(" transaction object is not empty ", emptyTransactionStatistics, transactionStatistics);
    }

    @Test
    public void getStatisticsOfaTimePeriod_WithTransactionDataOnRepository_ReturnsCorrectObject() {
        Instant now = Instant.now();

        transactionRepository.deleteAll();
        fillRepositoryWithObjects(now);

        TransactionStatistics predefinedTransactionStatistics = new TransactionStatistics();
        predefinedTransactionStatistics.setSum("4140.00");
        predefinedTransactionStatistics.setAvg("69.00");
        predefinedTransactionStatistics.setMax("128.00");
        predefinedTransactionStatistics.setMin("10.00");
        predefinedTransactionStatistics.setCount(60);

        TransactionStatistics transactionStatistics = transactionStatisticsStatisticsService.
                getStatisticsOfaTimePeriod(now.minus(1, ChronoUnit.MINUTES), now);

        Assert.assertEquals(" transaction object values not equals to values ", predefinedTransactionStatistics,
                transactionStatistics);
    }


    private void fillRepositoryWithObjects(Instant now) {
        for (int i = 90; i >= 0; i--) {
            transactionRepository.save(new Transaction(BigDecimal.valueOf(10 + 2 * i), now.minus(1 + i, ChronoUnit.SECONDS)));
        }
    }
}
