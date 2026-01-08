package com.example.bankmanagement.service;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Service;

import com.example.bankmanagement.BankManagementSystemApplication;

@Service
public class RestartService {

	private final ApplicationContext context;

    public RestartService(ApplicationContext context) {
        this.context = context;
    }

    public void restart() {
        Thread restartThread = new Thread(() -> {
            try {
                Thread.sleep(1000); // Optionally wait a second before restarting
                ((ConfigurableApplicationContext) context).close(); // Close current context
                BankManagementSystemApplication.main(new String[]{}); // Restart the application with the actual main class name
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        restartThread.setDaemon(false);
        restartThread.start();
    }
}
