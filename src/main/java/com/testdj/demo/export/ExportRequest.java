package com.testdj.demo.export;

import java.util.List;

public record ExportRequest(String tab,
                            String format,
                            String content,
                            String algorithm,
                            List<Integer> numbers,
                            Boolean ascending,
                            Boolean unique) {
}
