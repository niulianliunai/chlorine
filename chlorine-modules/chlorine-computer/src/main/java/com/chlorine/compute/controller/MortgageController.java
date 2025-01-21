package com.chlorine.compute.controller;

import com.chlorine.base.mvc.dto.CommonResult;
import com.chlorine.compute.util.MortgageCalculatorUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class MortgageController {

    @GetMapping("/calculate")
    public CommonResult<List<Map<String, Double>>> calculateMortgage(
            @RequestParam("principal") double principal,
            @RequestParam("annualInterestRate") double annualInterestRate,
            @RequestParam("loanTermYears") int loanTermYears,
            @RequestParam("repaymentType") String repaymentType) {

        if ("equalPrincipal".equals(repaymentType)) {
            // 等额本金
            return CommonResult.success(MortgageCalculatorUtil.generateEqualPrincipalSchedule(principal, annualInterestRate, loanTermYears));
        } else if ("equalInstallment".equals(repaymentType)) {
            // 等额本息
            return CommonResult.success(MortgageCalculatorUtil.generateEqualInstallmentSchedule(principal, annualInterestRate, loanTermYears));
        } else {
            throw new IllegalArgumentException("Invalid repayment type: " + repaymentType);
        }
    }
}
