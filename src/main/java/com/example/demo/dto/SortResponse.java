package com.example.demo.dto;

import java.util.List;

public class SortResponse {
    private List<Integer> original;
    private List<Integer> sorted;
    private int steps;

    public SortResponse() {}

    public SortResponse(List<Integer> original, List<Integer> sorted, int steps) {
        this.original = original;
        this.sorted = sorted;
        this.steps = steps;
    }

    public List<Integer> getOriginal() { return original; }
    public void setOriginal(List<Integer> original) { this.original = original; }
    public List<Integer> getSorted() { return sorted; }
    public void setSorted(List<Integer> sorted) { this.sorted = sorted; }
    public int getSteps() { return steps; }
    public void setSteps(int steps) { this.steps = steps; }
}
