package com.example.bankmanagement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import com.example.bankmanagement.BankManagementSystemApplication;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@Component
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

    @Autowired
    private ApplicationContext context;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
                                org.springframework.security.core.Authentication authentication) throws IOException, ServletException {
        // Optionally add a message or log that the user has logged out successfully.
        // Example: response.getWriter().write("Logout successful!");

        // Optionally restart the application
        restartApplication();

        // Redirect to the login page or any other page
        response.sendRedirect("/login?logout=true");
    }

    private void restartApplication() {
        Thread restartThread = new Thread(() -> {
            try {
                Thread.sleep(1000); // Optional delay before restart
                ((ConfigurableApplicationContext) context).close(); // Close current context
                BankManagementSystemApplication.main(new String[]{}); // Restart application
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        restartThread.setDaemon(false);
        restartThread.start();
    }
}
