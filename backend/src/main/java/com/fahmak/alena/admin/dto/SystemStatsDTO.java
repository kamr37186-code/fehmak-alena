package com.fahmak.alena.admin.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SystemStatsDTO {
    private int concurrentUsers;
    private double systemUptime; // e.g. 99.98
    private String monthlyRevenue; // e.g. "$1.2M"
    
    // Growth metrics
    private double userGrowthTrend; // e.g. +12.5
    private double revenueGrowthTrend; // e.g. +8.2
    
    // Additional metrics
    private double proConversion;
    private double ltvAverage;
}
