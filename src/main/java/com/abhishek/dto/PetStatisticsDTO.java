package com.abhishek.dto;

import lombok.Getter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

@ToString
@Getter
public class PetStatisticsDTO {

    private long total;
    private final Map<String, PetCategoryStatisticsDTO> category = new HashMap<>();

    public void incrementTotal(long count) {
        this.total += count;
    }

    public PetCategoryStatisticsDTO getOrCreateCategory(String category) {
        return this.category.computeIfAbsent(category, _ -> new PetCategoryStatisticsDTO());
    }

}