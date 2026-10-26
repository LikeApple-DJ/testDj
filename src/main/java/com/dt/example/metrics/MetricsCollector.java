package com.dt.example.metrics;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 埋点收集器，进程内内存暂存调用事件。
 */
@Component
public class MetricsCollector {

    private final List<Event> events = new CopyOnWriteArrayList<>();

    public record Event(String callerId, String api, Instant timestamp) {
    }

    public void record(String callerId, String api) {
        events.add(new Event(callerId, api, Instant.now()));
    }

    public List<Event> all() {
        return List.copyOf(events);
    }
}