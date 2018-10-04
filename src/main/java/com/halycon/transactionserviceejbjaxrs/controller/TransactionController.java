package com.halycon.transactionserviceejbjaxrs.controller;

import com.halycon.transactionserviceejbjaxrs.domain.Transaction;
import com.halycon.transactionserviceejbjaxrs.domain.TransactionStatistics;
import com.halycon.transactionserviceejbjaxrs.service.StatisticsService;
import com.halycon.transactionserviceejbjaxrs.service.impl.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Path("/")
@Singleton
public class TransactionController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @EJB(name = "TransactionService")
    private TransactionService transactionService;

    @EJB(name = "TransactionStatisticsService")
    private StatisticsService<TransactionStatistics> transactionStatisticsService;

    @POST
    @Path("transactions")
    public Response transaction_post(Transaction transaction, @Context UriInfo uriInfo) {
        Instant now = Instant.now();

        if (!transactionService.validateForOlderTransactionTimestamp(transaction, now))
            return Response.noContent().build();
        else if (!transactionService.validateForFutureTransactionTimestamp(transaction, now))
            return Response.status(422).build();
        else if (!transactionService.validateTransactionAmount(transaction))
            return Response.status(422).build();
        else {
            transactionService.saveTransaction(transaction);
            UriBuilder builder = uriInfo.getAbsolutePathBuilder();
            builder.path(Long.toString(transaction.getTimestamp().toEpochMilli()));
            return Response.created(builder.build()).build();
        }
    }

    @DELETE
    @Path("transactions")
    public Response removeTransactions() {
        transactionService.removeTransactions();
        return Response.noContent().build();

    }

    @GET
    @Path("statistics")
    @Produces(MediaType.APPLICATION_JSON)
    public Response statistics() {

        Instant instantOfRequest = Instant.now();

        TransactionStatistics transactionStatistics = transactionStatisticsService.
                getStatisticsOfaTimePeriod(instantOfRequest.minus(1, ChronoUnit.MINUTES),
                        instantOfRequest);

        return Response.ok(transactionStatistics).build();
    }
}
