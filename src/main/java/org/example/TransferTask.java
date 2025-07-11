package org.example;

import java.util.List;
import java.util.Random;

/**
 * This class executes the runnable action that will transfer money between several accounts thread-safely
 */
public class TransferTask implements Runnable {

    private final Bank bank;
    private final List<String> accountsIds;
    private final int transfers;
    private final Random random;

    public TransferTask(Bank bank, List<String> accountsIds, int transfers) {
        this.bank = bank;
        this.accountsIds = accountsIds;
        this.transfers = transfers;
        this.random = new Random();
    }

    @Override
    public void run(){
        String threadName = Thread.currentThread().getName();
        System.out.println(threadName + " started transfer operations.");

        for (int i = 0; i < transfers; i++) {

            String fromId = accountsIds.get(random.nextInt(accountsIds.size()));
            String toId;
            do {
                toId = accountsIds.get(random.nextInt(accountsIds.size()));
            } while (fromId.equals(toId)); // Just ensure both accounts are different so that transfer can occur

            Account fromAccount = bank.getAccount(fromId);
            Account toAccount = bank.getAccount(toId);

            //Generate a random amount between 1.0 and 100.0
            double amount = 1 + (100 - 1) * random.nextDouble();

            bank.transfer(fromAccount, toAccount, amount);

            try {
                // Simulate some processing between transfers
                Thread.sleep(random.nextInt(100));
            } catch (InterruptedException e) {
                System.out.println(threadName + " was interrupted.");
                Thread.currentThread().interrupt();
                break;
            }
        }
        System.out.println(threadName + " finished transfer operations.");
    }
}

