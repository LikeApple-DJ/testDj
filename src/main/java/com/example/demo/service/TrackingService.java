package com.example.demo.service;
import com.example.demo.model.TrackingRecord;
import com.example.demo.model.User;
import com.example.demo.repository.TrackingRecordRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;
@Service
public class TrackingService {
    private final TrackingRecordRepository trackingRepo;
    private final UserRepository userRepository;
    public TrackingService(TrackingRecordRepository trackingRepo, UserRepository userRepository) {
        this.trackingRepo = trackingRepo;
        this.userRepository = userRepository;
    }
    private static final int MAX_REPORT_RECORDS = 1000;

    public List<Map<String, Object>> getReport(String dimension) {
        List<TrackingRecord> records = trackingRepo.findAll(PageRequest.of(0, MAX_REPORT_RECORDS)).getContent();
        Map<Long, User> userMap = userRepository.findAll().stream()
                .collect(Collectors.toMap(User::getId, u -> u));
        Map<String, List<TrackingRecord>> grouped = new LinkedHashMap<>();
        for (TrackingRecord record : records) {
            User user = userMap.get(record.getUserId());
            String key = "未设置";
            if (user != null) {
                key = switch (dimension) {
                    case "personType" -> user.getPersonType() != null ? user.getPersonType() : "未设置";
                    case "personLevel" -> user.getPersonLevel() != null ? user.getPersonLevel() : "未设置";
                    case "personDept" -> user.getPersonDept() != null ? user.getPersonDept() : "未设置";
                    default -> "未设置";
                };
            }
            grouped.computeIfAbsent(key, k -> new ArrayList<>()).add(record);
        }
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, List<TrackingRecord>> entry : grouped.entrySet()) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("label", entry.getKey());
            item.put("callCount", entry.getValue().size());
            item.put("details", entry.getValue().stream().map(r -> {
                Map<String, Object> detail = new LinkedHashMap<>();
                detail.put("apiName", r.getApiName());
                detail.put("callTime", r.getCallTime() != null ? r.getCallTime().toString() : "");
                detail.put("paramsJson", r.getParamsJson());
                return detail;
            }).collect(Collectors.toList()));
            result.add(item);
        }
        return result;
    }
}