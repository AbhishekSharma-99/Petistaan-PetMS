package com.abhishek.dto;

import com.abhishek.enums.PetType;
import lombok.Getter;
import lombok.ToString;

import java.util.EnumMap;
import java.util.Map;

@ToString
@Getter
public class PetGenderStatisticsDTO {

    private long total;
    private final Map<PetType, Long> type = new EnumMap<>(PetType.class);

    public void incrementTotal(long count) {
        this.total += count;
    }

    public void mergeOrCreateType(PetType type, long count) {
        this.type.merge(type, count, Long::sum);
    }

}
