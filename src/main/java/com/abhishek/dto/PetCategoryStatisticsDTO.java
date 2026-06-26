package com.abhishek.dto;

import com.abhishek.enums.Gender;
import lombok.Getter;
import lombok.ToString;

import java.util.EnumMap;
import java.util.Map;

@ToString
@Getter
public class PetCategoryStatisticsDTO {

    private long total;
    private final Map<Gender, PetGenderStatisticsDTO> gender = new EnumMap<>(Gender.class);

    public void incrementTotal(long count) {
        this.total += count;
    }

    public PetGenderStatisticsDTO getOrCreateGender(Gender gender) {
        return this.gender.computeIfAbsent(
                gender, _ -> new PetGenderStatisticsDTO());
    }

}