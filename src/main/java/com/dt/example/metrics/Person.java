package com.dt.example.metrics;

/**
 * 人员及其组织维度。
 */
public record Person(String id, String name, String type, String level, String department) {
}