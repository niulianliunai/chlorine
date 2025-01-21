package com.chlorine.compute.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

public class TaxCalculatorUtil {

    // 税率表
    private static final double[] TAX_RATES = {0.03, 0.1, 0.2, 0.25, 0.3, 0.35, 0.45};
    // 速算扣除数
    private static final int[] QUICK_DEDUCTION = {0, 210, 1410, 2660, 4410, 7160, 15160};

    // 起征点（基本减除费用）
    private static final BigDecimal BASIC_DEDUCTION = new BigDecimal("5000");

    // 社保缴纳比例（假设）
    private static final BigDecimal SOCIAL_SECURITY_RATE = new BigDecimal("0.08");
    private static final BigDecimal HOUSING_FUND_RATE = new BigDecimal("0.12");

    public static Map<String, BigDecimal> calculateTax(BigDecimal income, BigDecimal socialSecurityBase, BigDecimal housingFundBase, BigDecimal additionalDeduction) {
        if (income.compareTo(BASIC_DEDUCTION) <= 0) {
            return createResultMap(BigDecimal.ZERO);
        }

        // 计算社保和公积金
        BigDecimal socialSecurity = socialSecurityBase.multiply(SOCIAL_SECURITY_RATE).setScale(2, RoundingMode.HALF_UP);
        BigDecimal housingFund = housingFundBase.multiply(HOUSING_FUND_RATE).setScale(2, RoundingMode.HALF_UP);

        // 应纳税所得额
        BigDecimal taxableIncome = income.subtract(BASIC_DEDUCTION)
                .subtract(socialSecurity)
                .subtract(housingFund)
                .subtract(additionalDeduction);

        if (taxableIncome.compareTo(BigDecimal.ZERO) < 0) {
            taxableIncome = BigDecimal.ZERO;
        }

        int level = getTaxLevel(taxableIncome);
        BigDecimal tax = taxableIncome.multiply(new BigDecimal(TAX_RATES[level]))
                .subtract(new BigDecimal(QUICK_DEDUCTION[level]));

        Map<String, BigDecimal> result = createResultMap(tax);
        result.put("socialSecurity", socialSecurity);
        result.put("housingFund", housingFund);
        return result;
    }

    private static int getTaxLevel(BigDecimal taxableIncome) {
        if (taxableIncome.compareTo(new BigDecimal("960000")) > 0) return 6; // 超过96万的部分
        if (taxableIncome.compareTo(new BigDecimal("360000")) > 0) return 5; // 超过36万至96万的部分
        if (taxableIncome.compareTo(new BigDecimal("144000")) > 0) return 4; // 超过14.4万至36万的部分
        if (taxableIncome.compareTo(new BigDecimal("30000")) > 0) return 3;  // 超过3万至14.4万的部分
        if (taxableIncome.compareTo(new BigDecimal("12000")) > 0) return 2;  // 超过1.2万至3万的部分
        if (taxableIncome.compareTo(new BigDecimal("3600")) > 0) return 1;   // 超过3600至1.2万的部分
        return 0; // 不超过3600元的部分
    }

    private static Map<String, BigDecimal> createResultMap(BigDecimal tax) {
        Map<String, BigDecimal> resultMap = new HashMap<>();
        resultMap.put("tax", tax);
        return resultMap;
    }
}
