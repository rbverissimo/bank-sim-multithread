package org.example;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        System.out.println("Starting Bank simulation transferring money in a thread-safe way...");

        // 1. Create a Bank instance
        Bank bank = new Bank();

        // 2. Create 3 accounts and add them to the bank
        Account account1 = new Account("ACC001", 1000.00);
        Account account2 = new Account("ACC002", 1500.00);
        Account account3 = new Account("ACC003", 2000.00);
        Account account4 = new Account("ACC004", 2500.00);

        bank.addAccount(account1);
        bank.addAccount(account2);
        bank.addAccount(account3);
        bank.addAccount(account4);

        List<String> accountIds = Arrays.asList(account1.getId(), account2.getId(), account3.getId(), account4.getId());

        bank.printAllBalances();

        int numberOfThreads = 8;
        int transfersPerThread = 30;

        try (ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads)) {

            System.out.println("Initiating " + numberOfThreads + " transfer threads, each performing " + transfersPerThread + " transfers.");

            for (int i = 0; i < numberOfThreads; i++) {
                executor.submit(new TransferTask(bank, accountIds, transfersPerThread));
            }

            executor.shutdown();
            try {

                if (!executor.awaitTermination(90, TimeUnit.SECONDS)) {
                    System.err.println("Threads did not terminate in the specified time. Forcing shutdown.");
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                System.err.println("Main thread interrupted while waiting for executor to terminate.");
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }

        System.out.println("\nAll transfer operations completed.");

        bank.printAllBalances();

        double totalBalance = account1.getBalance() + account2.getBalance() + account3.getBalance() + account4.getBalance();
        System.out.printf("Total balance across all accounts: %.2f (Expected: 7000.00)%n", totalBalance);

        if (Math.abs(totalBalance - 7000.00) < 0.01) {
            System.out.println("Total balance is consistent. Thread safety appears to be working!");
        } else {
            System.err.println("WARNING: Total balance is inconsistent. There might be a thread safety issue!");
        }

        System.out.println("Bank Simulation Finished.");
    }
}