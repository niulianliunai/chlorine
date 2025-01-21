package com.chlorine.compute.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MortgageCalculatorUtil {

    public static double calculateEqualInstallment(double principal, double annualInterestRate, int loanTermYears) {
        // 将年利率转换为月利率
        double monthlyInterestRate = annualInterestRate / 100.0 / 12.0;
        // 计算总期数（以月为单位）
        int numberOfPayments = loanTermYears * 12;

        // 使用等额本息还款公式
        double monthlyPayment = (principal * monthlyInterestRate) /
                (1 - Math.pow(1 + monthlyInterestRate, -numberOfPayments));

        return monthlyPayment;
    }

    public static List<Map<String, Double>> generateEqualInstallmentSchedule(double principal, double annualInterestRate, int loanTermYears) {
        // 将年利率转换为月利率
        double monthlyInterestRate = annualInterestRate / 100.0 / 12.0;
        // 计算总期数（以月为单位）
        int numberOfPayments = loanTermYears * 12;

        List<Map<String, Double>> schedule = new ArrayList<>();
        double remainingPrincipal = principal;

        for (int i = 1; i <= numberOfPayments; i++) {
            double interest = remainingPrincipal * monthlyInterestRate;
            double principalPayment = (calculateEqualInstallment(principal, annualInterestRate, loanTermYears) - interest);
            remainingPrincipal -= principalPayment;

            Map<String, Double> paymentDetails = Map.of(
                    "month", (double) i,
                    "monthlyPayment", calculateEqualInstallment(principal, annualInterestRate, loanTermYears),
                    "principalPayment", principalPayment,
                    "interestPayment", interest,
                    "remainingPrincipal", remainingPrincipal
            );
            schedule.add(paymentDetails);
        }

        return schedule;
    }

    public static double calculateEqualPrincipal(double principal, double annualInterestRate, int loanTermYears) {
        // 将年利率转换为月利率
        double monthlyInterestRate = annualInterestRate / 100.0 / 12.0;
        // 计算总期数（以月为单位）
        int numberOfPayments = loanTermYears * 12;

        // 等额本金
        double totalPrincipalPerMonth = principal / numberOfPayments;
        double totalPayment = 0.0;
        for (int i = 1; i <= numberOfPayments; i++) {
            double interest = (principal - (totalPrincipalPerMonth * (i - 1))) * monthlyInterestRate;
            totalPayment += (totalPrincipalPerMonth + interest);
        }
        return totalPayment / numberOfPayments;
    }

    public static List<Map<String, Double>> generateEqualPrincipalSchedule(double principal, double annualInterestRate, int loanTermYears) {
        // 将年利率转换为月利率
        double monthlyInterestRate = annualInterestRate / 100.0 / 12.0;
        // 计算总期数（以月为单位）
        int numberOfPayments = loanTermYears * 12;

        List<Map<String, Double>> schedule = new ArrayList<>();
        double remainingPrincipal = principal;

        for (int i = 1; i <= numberOfPayments; i++) {
            double interest = remainingPrincipal * monthlyInterestRate;
            double principalPayment = principal / numberOfPayments;
            double monthlyPayment = principalPayment + interest;
            remainingPrincipal -= principalPayment;

            Map<String, Double> paymentDetails = Map.of(
                    "month", (double) i,
                    "monthlyPayment", monthlyPayment,
                    "principalPayment", principalPayment,
                    "interestPayment", interest,
                    "remainingPrincipal", remainingPrincipal
            );
            schedule.add(paymentDetails);
        }

        return schedule;
    }
}
