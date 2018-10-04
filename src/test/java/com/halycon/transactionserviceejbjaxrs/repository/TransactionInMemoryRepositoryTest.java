package com.halycon.transactionserviceejbjaxrs.repository;

import com.halycon.transactionserviceejbjaxrs.domain.Transaction;
import com.halycon.transactionserviceejbjaxrs.domain.TransactionStatistics;
import com.halycon.transactionserviceejbjaxrs.repository.impl.TransactionInMemoryRepository;
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
import java.math.RoundingMode;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@RunWith(Arquillian.class)
public class TransactionInMemoryRepositoryTest {

    private TransactionRepository transactionRepository;

    @Before
    public void init() {
        transactionRepository = new TransactionInMemoryRepository();
    }

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class).
                addClasses(TransactionInMemoryRepository.class, Transaction.class, TransactionRepository.class).
                addAsWebInfResource(EmptyAsset.INSTANCE, ArchivePaths.create("beans.xml"));
    }

    @Test
    public void should_SaveRandomTransactionAndFetchIt_Successfully() {

        transactionRepository.deleteAll();

        Instant now = Instant.now();
        transactionRepository.save(new Transaction(BigDecimal.valueOf(200).setScale(2, RoundingMode.HALF_UP), now));
        transactionRepository.save(new Transaction(BigDecimal.valueOf(203).setScale(2, RoundingMode.HALF_UP), now));

        List<Transaction> transactionList = transactionRepository.findTransactionsByInstant(now);
        Assert.assertNotNull(" transaction list null", transactionList);
        Assert.assertEquals("saved amount is not 200",
                BigDecimal.valueOf(200).setScale(2, RoundingMode.HALF_UP), transactionList.get(0).getAmountDecimal());
        Assert.assertEquals("saved amount is not 203",
                BigDecimal.valueOf(203).setScale(2, RoundingMode.HALF_UP), transactionList.get(1).getAmountDecimal());
    }

    @Test
    public void should_fetchTransactionsForLast60Seconds_Successfully() {

        Instant now = Instant.now();
        for (int i = 90; i >= 0; i--) {
            transactionRepository.save(new Transaction(BigDecimal.valueOf(10 + 2 * i).setScale(2, RoundingMode.HALF_UP),
                    now.minus(1 + i, ChronoUnit.SECONDS)));
        }
        List<Transaction> transactionList = transactionRepository.
                findAllBetweenTimestamps(now.minus(1, ChronoUnit.MINUTES), now);

        Assert.assertNotNull(" transaction list is null", transactionList);
        Assert.assertEquals(" transaction list size is not 60", 60, transactionList.size());
        Assert.assertEquals(" oldest transaction amount is not 128",
                BigDecimal.valueOf(128).setScale(2, RoundingMode.HALF_UP), transactionList.get(0).getAmountDecimal());
        Assert.assertEquals(" most recent transaction amount is not 10",
                BigDecimal.valueOf(10).setScale(2, RoundingMode.HALF_UP), transactionList.get(59).getAmountDecimal());
    }

    @Test
    public void should_deleteAllTransaction_Successfully() {
        Instant now = Instant.now();
        transactionRepository.save(new Transaction(BigDecimal.valueOf(200).setScale(2, RoundingMode.HALF_UP), now));
        transactionRepository.deleteAll();
        List<Transaction> transactionList = transactionRepository.findTransactionsByInstant(now);
        Assert.assertNull(" transaction list is not null", transactionList);
    }


}
