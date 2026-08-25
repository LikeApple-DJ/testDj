package com.testdj.demo.sort;

import java.util.List;

public record SortRequest(List<Integer> numbers, boolean ascending, boolean unique) {
}