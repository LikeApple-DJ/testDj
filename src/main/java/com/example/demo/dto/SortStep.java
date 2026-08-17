package com.example.demo.dto;

public class SortStep {
    private int round;
    private int[] array;

    public SortStep(int round, int[] array) {
        this.round = round;
        this.array = array;
    }

    public int getRound() { return round; }
    public int[] getArray() { return array; }
}