package com.example.bankmanagement.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public ModelAndView handleError(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        if (statusCode != null) {
            switch (statusCode) {
                case 400:
                    return new ModelAndView("error/validation_error");
                case 500:
                    return new ModelAndView("error/500");
                default:
                    return new ModelAndView("error/500");
            }
        }
        return new ModelAndView("error/500");
    }

  
}
