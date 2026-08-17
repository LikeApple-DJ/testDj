package com.example.demo.dto;

import java.util.List;

public class SortResponse {
    private int[] originalArray;
    private int[] sortedArray;
    private List<SortStep> steps;
    private int totalRounds;
    private int swapCount;

    public int[] getOriginalArray() { return originalArray; }
    public void setOriginalArray(int[] originalArray) { this.originalArray = originalArray; }
    public int[] getSortedArray() { return sortedArray; }
    public void setSortedArray(int[] sortedArray) { this.sortedArray = sortedArray; }
    public List<SortStep> getSteps() { return steps; }
    public void setSteps(List<SortStep> steps) { this.steps = steps; }
    public int getTotalRounds() { return totalRounds; }
    public void setTotalRounds(int totalRounds) { this.totalRounds = totalRounds; }
    public int getSwapCount() { return swapCount; }
    public void setSwapCount(int swapCount) { this.swapCount = swapCount; }
}