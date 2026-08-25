package com.testdj.demo.sort;

import com.testdj.demo.common.ErrorCode;
import com.testdj.demo.exception.BusinessException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

@Service
public class BubbleSortService {

    public SortResponse sort(SortRequest request) {
        if (request.numbers() == null || request.numbers().isEmpty()) {
            throw new BusinessException(ErrorCode.SORT_NUMBERS_EMPTY, ErrorCode.SORT_NUMBERS_EMPTY_MSG);
        }
        List<Integer> input = new ArrayList<>(request.numbers());
        List<Integer> output = new ArrayList<>(input);
        int n = output.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                int cmp = output.get(j).compareTo(output.get(j + 1));
                if ((request.ascending() && cmp > 0) || (!request.ascending() && cmp < 0)) {
                    int temp = output.get(j);
                    output.set(j, output.get(j + 1));
                    output.set(j + 1, temp);
                }
            }
        }
        if (request.unique()) {
            output = new ArrayList<>(new LinkedHashSet<>(output));
        }
        return new SortResponse(input, output);
    }
}
