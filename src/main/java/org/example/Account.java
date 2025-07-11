package org.example;

import java.util.concurrent.locks.ReentrantLock;

public class Account {

    private final String id;
    private double balance;
    private final ReentrantLock lock;

    public Account(String id, double balance) {
        this.id = id;
        this.balance = balance;
        lock = new ReentrantLock();
    }

    public String getId() {
        return id;
    }

    public Double getBalance() {
        return balance;
    }

    public ReentrantLock getLock() {
        return lock;
    }

    public void deposit(double amount) {
        if (amount <= 0) {
            System.err.println("Deposit amount must be positive.");
            return;
        }

        lock.lock();
        try {
            balance += amount;
        } finally {
            lock.unlock();
        }
    }

    public boolean withdraw(double amount) {
        if (amount <= 0) {
            System.err.println("Withdrawal amount must be positive.");
            return false;
        }

        lock.lock();
        try {
            if (balance >= amount) {
                balance -= amount;
                return true;
            } else {
                return false; // Can't withdraw if balance doesn't have enough funds
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public String toString() {
        return "Account{" +
                "id='" + id + '\'' +
                ", balance=" + String.format("%.2f", balance) +
                '}';
    }
}
