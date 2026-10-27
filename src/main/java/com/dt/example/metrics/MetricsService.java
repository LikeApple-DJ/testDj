package com.dt.example.metrics;

import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

/**
 * 埋点聚合服务，按维度与图表形态输出报表数据。
 */
@Service
public class MetricsService {

    private static final DateTimeFormatter TIME_FORMAT =
        DateTimeFormatter.ofPattern("HH:mm").withZone(ZoneId.systemDefault());
    private static final List<String> API_ORDER =
        List.of("/api/helloWorld", "/api/hash", "/api/sort/bubble");
    private static final Set<String> DIMENSIONS =
        Set.of("personType", "personLevel", "personDepartment", "time");
    private static final Set<String> CHART_TYPES =
        Set.of("line", "pie", "bar");

    private final MetricsCollector collector;
    private final PersonDictionary dictionary;

    public MetricsService(MetricsCollector collector, PersonDictionary dictionary) {
        this.collector = collector;
        this.dictionary = dictionary;
    }

    public MetricsQueryResponse query(String dimension, String chartType) {
        if (dimension == null || !DIMENSIONS.contains(dimension)) {
            throw new IllegalArgumentException("unsupported dimension: " + dimension);
        }
        if (chartType == null || !CHART_TYPES.contains(chartType)) {
            throw new IllegalArgumentException("unsupported chartType: " + chartType);
        }
        if (!"line".equals(chartType) && "time".equals(dimension)) {
            throw new IllegalArgumentException("dimension time 仅支持 chartType=line");
        }
        List<MetricsCollector.Event> events = collector.all();
        return switch (chartType) {
            case "pie" -> pie(dimension, events);
            case "bar" -> bar(dimension, events);
            case "line" -> line(events);
            default -> throw new IllegalArgumentException("unsupported chartType: " + chartType);
        };
    }

    private MetricsQueryResponse pie(String dimension, List<MetricsCollector.Event> events) {
        LinkedHashMap<String, Long> counts = grouped(dimension, events);
        List<String> categories = List.copyOf(counts.keySet());
        List<Double> values = categories.stream()
            .map(c -> (double) counts.get(c)).toList();
        return new MetricsQueryResponse(dimension, "pie", categories,
            List.of(new ChartSeries("调用次数", values)), events.size());
    }

    private MetricsQueryResponse bar(String dimension, List<MetricsCollector.Event> events) {
        LinkedHashMap<String, Long> counts = grouped(dimension, events);
        List<String> categories = List.copyOf(counts.keySet());
        List<ChartSeries> series = new ArrayList<>();
        for (String api : API_ORDER) {
            List<Double> values = categories.stream()
                .map(c -> (double) events.stream()
                    .filter(e -> e.api().equals(api)
                        && dimensionValue(e, dimension).equals(c))
                    .count())
                .toList();
            series.add(new ChartSeries(apiName(api), values));
        }
        return new MetricsQueryResponse(dimension, "bar", categories, series, events.size());
    }

    private MetricsQueryResponse line(List<MetricsCollector.Event> events) {
        TreeMap<String, Long> buckets = new TreeMap<>();
        for (MetricsCollector.Event e : events) {
            buckets.merge(TIME_FORMAT.format(e.timestamp()), 1L, Long::sum);
        }
        List<String> categories = List.copyOf(buckets.keySet());
        List<Double> values = categories.stream()
            .map(c -> (double) buckets.get(c)).toList();
        return new MetricsQueryResponse("time", "line", categories,
            List.of(new ChartSeries("调用次数", values)), events.size());
    }

    private LinkedHashMap<String, Long> grouped(String dimension,
                                                List<MetricsCollector.Event> events) {
        LinkedHashMap<String, Long> counts = new LinkedHashMap<>();
        for (MetricsCollector.Event e : events) {
            counts.merge(dimensionValue(e, dimension), 1L, Long::sum);
        }
        return counts;
    }

    private String dimensionValue(MetricsCollector.Event e, String dimension) {
        Person p = dictionary.find(e.callerId());
        return switch (dimension) {
            case "personType" -> p.type();
            case "personLevel" -> p.level();
            case "personDepartment" -> p.department();
            default -> throw new IllegalArgumentException("unsupported dimension: " + dimension);
        };
    }

    private String apiName(String api) {
        return switch (api) {
            case "/api/helloWorld" -> "helloworld";
            case "/api/hash" -> "hash";
            case "/api/sort/bubble" -> "bubble";
            default -> api;
        };
    }
}