package com.abhishek.service.impl;

import com.abhishek.dto.PetCategoryStatisticsDTO;
import com.abhishek.dto.PetGenderStatisticsDTO;
import com.abhishek.dto.PetStatisticsDTO;
import com.abhishek.enums.Gender;
import com.abhishek.enums.PetType;
import com.abhishek.repository.PetRepository;
import com.abhishek.service.PetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class PetServiceImpl implements PetService {

    private final PetRepository petRepository;

    @Override
    public PetStatisticsDTO getStatistics() {
        PetStatisticsDTO petStatisticsDTO = new PetStatisticsDTO();
        List<Object[]> rows = petRepository.fetchStatistics();
        for (Object[] row : rows) {
            String category = (String) row[0];
            Gender gender = (Gender) row[1];
            PetType type = (PetType) row[2];
            long count = (Long) row[3];
            petStatisticsDTO.incrementTotal(count);
            PetCategoryStatisticsDTO petCategoryStatisticsDTO = petStatisticsDTO.getOrCreateCategory(category);
            petCategoryStatisticsDTO.incrementTotal(count);
            PetGenderStatisticsDTO petGenderStatisticsDTO = petCategoryStatisticsDTO.getOrCreateGender(gender);
            petGenderStatisticsDTO.incrementTotal(count);
            petGenderStatisticsDTO.mergeOrCreateType(type, count);
        }
        return petStatisticsDTO;
    }

}
