package com.example.demo.repository;
import com.example.demo.model.TrackingRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface TrackingRecordRepository extends JpaRepository<TrackingRecord, Long> {
    List<TrackingRecord> findByApiName(String apiName);
}