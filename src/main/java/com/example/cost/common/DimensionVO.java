package com.example.cost.common;

import lombok.Data;
import java.util.List;

@Data
public class DimensionVO {
    private List<String> departments;
    private List<String> projects;
    private List<String> businessLines;
    private List<PersonnelOption> personnel;

    @Data
    public static class PersonnelOption {
        private Long id;
        private String name;

        public PersonnelOption(Long id, String name) {
            this.id = id;
            this.name = name;
        }
    }
}