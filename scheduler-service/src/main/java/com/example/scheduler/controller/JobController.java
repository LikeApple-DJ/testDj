package com.example.scheduler.controller;

import com.example.scheduler.dto.JobInfoDTO;
import com.example.scheduler.dto.PageQuery;
import com.example.scheduler.dto.PageResult;
import com.example.scheduler.entity.JobLog;
import com.example.scheduler.service.JobService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/job")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping("/add")
    public ResponseEntity<Map<String, Long>> addJob(@RequestBody JobInfoDTO jobInfo) {
        Long jobId = jobService.addJob(jobInfo);
        return ResponseEntity.ok(Map.of("jobId", jobId));
    }

    @PostMapping("/update")
    public ResponseEntity<Map<String, Boolean>> updateJob(@RequestBody JobInfoDTO jobInfo) {
        boolean result = jobService.updateJob(jobInfo);
        return ResponseEntity.ok(Map.of("success", result));
    }

    @PostMapping("/delete")
    public ResponseEntity<Map<String, Boolean>> deleteJob(@RequestBody Map<String, Long> request) {
        boolean result = jobService.deleteJob(request.get("jobId"));
        return ResponseEntity.ok(Map.of("success", result));
    }

    @PostMapping("/trigger")
    public ResponseEntity<Map<String, Boolean>> triggerJob(@RequestBody Map<String, Long> request) {
        boolean result = jobService.triggerJob(request.get("jobId"));
        return ResponseEntity.ok(Map.of("success", result));
    }

    @PostMapping("/pause")
    public ResponseEntity<Map<String, Boolean>> pauseJob(@RequestBody Map<String, Long> request) {
        boolean result = jobService.pauseJob(request.get("jobId"));
        return ResponseEntity.ok(Map.of("success", result));
    }

    @PostMapping("/resume")
    public ResponseEntity<Map<String, Boolean>> resumeJob(@RequestBody Map<String, Long> request) {
        boolean result = jobService.resumeJob(request.get("jobId"));
        return ResponseEntity.ok(Map.of("success", result));
    }

    @GetMapping("/list")
    public ResponseEntity<PageResult<JobInfoDTO>> listJobs(PageQuery query) {
        PageResult<JobInfoDTO> result = jobService.listJobs(query);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/log/list")
    public ResponseEntity<PageResult<JobLog>> listJobLogs(PageQuery query) {
        PageResult<JobLog> result = jobService.listJobLogs(query);
        return ResponseEntity.ok(result);
    }
}