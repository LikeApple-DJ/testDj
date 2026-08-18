package com.example.demo.dto;

import java.util.List;

public class BubbleSortResponse {
    private List<Integer> original;
    private List<Integer> sorted;
    private List<SortStep> steps;
    private int comparisons;

    public BubbleSortResponse(List<Integer> original, List<Integer> sorted,
                               List<SortStep> steps, int comparisons) {
        this.original = original;
        this.sorted = sorted;
        this.steps = steps;
        this.comparisons = comparisons;
    }

    public List<Integer> getOriginal() { return original; }
    public List<Integer> getSorted() { return sorted; }
    public List<SortStep> getSteps() { return steps; }
    public int getComparisons() { return comparisons; }

    public static class SortStep {
        private int round;
        private List<Integer> array;

        public SortStep(int round, List<Integer> array) {
            this.round = round;
            this.array = array;
        }

        public int getRound() { return round; }
        public List<Integer> getArray() { return array; }
    }
}