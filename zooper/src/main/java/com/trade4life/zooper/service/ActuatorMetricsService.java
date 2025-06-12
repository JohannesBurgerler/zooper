package com.trade4life.zooper.service;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ActuatorMetricsService {
    private MeterRegistry meterRegistry;

    public Map<String, String> getSelectedMetrics() {
        Map<String, String> metrics = new HashMap<>();

        metrics.put("system.cpu.usage", getMetricValue("system.cpu.usage"));
        metrics.put("jvm.memory.used", getMetricValue("jvm.memory.used"));
        metrics.put("process.uptime", getMetricValue("process.uptime"));

        return metrics;
    }

    private String getMetricValue(String metricKey){
        return meterRegistry.find(metricKey).gauges().stream().findFirst().map(g ->String.format("%.2f", g.value())).orElse("N/A");
    }
}
