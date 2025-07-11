package org.example;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class Bank {

    private final Map<String, Account> accounts;

    public Bank() {
        this.accounts = new ConcurrentHashMap<>();
    }

    public void addAccount(Account account) {
        accounts.put(account.getId(), account);
    }

    public Account getAccount(String accountId) {
        return accounts.get(accountId);
    }

    /**
     *
     * Thread-safely takes two accounts and transfer money between them if possible.
     * Thread-safety is assured by locking the smallest ID account first always.
     *
     * @param fromAccount the account from which money will be withdrawn
     * @param toAccount account that will receive money
     * @param amount the amount transferred between both
     * @return true if transfer ocurred successfully
     */
    public boolean transfer(Account fromAccount, Account toAccount, double amount) {

        if (fromAccount.getId().equals(toAccount.getId())) {
            System.out.printf("[%s] Cannot transfer from %s to itself.%n", Thread.currentThread().getName(), fromAccount.getId());
            return false;
        }

        if (amount <= 0) {
            System.err.printf("[%s] Transfer amount must be positive. Amount: %.2f%n", Thread.currentThread().getName(), amount);
            return false;
        }

        // The account with the smallest ID should the one to take the lock first to avoid deadlocks
        Account firstLockAccount;
        Account secondLockAccount;

        if (fromAccount.getId().compareTo(toAccount.getId()) < 0) {
            firstLockAccount = fromAccount;
            secondLockAccount = toAccount;
        } else {
            firstLockAccount = toAccount;
            secondLockAccount = fromAccount;
        }

        // Acquire locks in the determined order
        ReentrantLock firstLock = firstLockAccount.getLock();
        ReentrantLock secondLock = secondLockAccount.getLock();

        firstLock.lock();
        try {
            secondLock.lock();
            try {
                if (fromAccount.getBalance() >= amount) {
                    fromAccount.withdraw(amount);
                    toAccount.deposit(amount);

                    System.out.printf("[%s] Transferred %.2f from %s (%.2f) to %s (%.2f).%n",
                            Thread.currentThread().getName(), amount,
                            fromAccount.getId(), fromAccount.getBalance(),
                            toAccount.getId(), toAccount.getBalance());
                    return true;
                } else {
                    System.out.printf("[%s] Failed to transfer %.2f from %s (ID: %s). Insufficient funds. Current balance: %.2f%n",
                            Thread.currentThread().getName(), amount, fromAccount.getId(), fromAccount.getId(), fromAccount.getBalance());
                    return false;
                }
            } finally {
                secondLock.unlock();
            }
        } finally {
            firstLock.unlock();
        }
    }


    public void printAllBalances() {
        System.out.println("\n--- Current Account Balances ---");
        accounts.values().forEach(account ->
                System.out.printf("Account %s: %.2f%n", account.getId(), account.getBalance())
        );
        System.out.println("--------------------------------\n");
    }
}
