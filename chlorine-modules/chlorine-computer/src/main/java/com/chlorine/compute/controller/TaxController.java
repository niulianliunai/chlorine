package com.chlorine.compute.controller;

import com.chlorine.compute.util.TaxCalculatorUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Map;

@RestController
public class TaxController {

    @GetMapping("/calculate-tax")
    public Map<String, BigDecimal> calculateTax(
            @RequestParam("income") BigDecimal income,
            @RequestParam("socialSecurityBase") BigDecimal socialSecurityBase,
            @RequestParam("housingFundBase") BigDecimal housingFundBase,
            @RequestParam("additionalDeduction") BigDecimal additionalDeduction) {

        return TaxCalculatorUtil.calculateTax(income, socialSecurityBase, housingFundBase, additionalDeduction);
    }
}
