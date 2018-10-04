package com.halycon.transactionserviceejbjaxrs.service.impl;


import com.halycon.transactionserviceejbjaxrs.domain.Transaction;
import com.halycon.transactionserviceejbjaxrs.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Singleton(mappedName = "TransactionService")
@Startup
public class TransactionService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @EJB(name = "TransactionInMemoryRepository")
    private TransactionRepository transactionRepository;

    public boolean validateForOlderTransactionTimestamp(Transaction transaction, Instant now) {
        return transaction.getTimestamp().isAfter(now.minus(60, ChronoUnit.SECONDS));
    }

    public boolean validateForFutureTransactionTimestamp(Transaction transaction, Instant now) {
        return !transaction.getTimestamp().isAfter(now);
    }

    public List<Transaction> findTransactionsByInstant(Instant instant) {
        return transactionRepository.findTransactionsByInstant(instant);
    }

    public void saveTransaction(Transaction transaction) {
        transactionRepository.save(transaction);
    }

    public void removeTransactions() {
        transactionRepository.deleteAll();
    }

    public boolean validateTransactionAmount(Transaction transaction) {
        try {
            transaction.setAmountDecimal(new BigDecimal(transaction.getAmount()));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void setTransactionRepository(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

}
