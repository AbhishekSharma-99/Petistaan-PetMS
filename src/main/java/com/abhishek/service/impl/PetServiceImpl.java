package com.abhishek.service.impl;

import com.abhishek.dto.PetCategoryStatisticsDTO;
import com.abhishek.dto.PetDTO;
import com.abhishek.dto.PetGenderStatisticsDTO;
import com.abhishek.dto.PetStatisticsDTO;
import com.abhishek.entity.Pet;
import com.abhishek.enums.Gender;
import com.abhishek.enums.PetType;
import com.abhishek.exception.PetNotFoundException;
import com.abhishek.repository.PetRepository;
import com.abhishek.service.PetService;
import com.abhishek.utils.PetMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class PetServiceImpl implements PetService {

    private final PetRepository petRepository;
    private final PetMapper petMapper;
    @Value("${pet.not.found}")
    private String PetNotFound;

    @Override
    public Integer savePet(PetDTO petDTO) {
        Pet pet = petMapper.petDTOToPet(petDTO);
        Pet savedPet = petRepository.save(pet);
        return savedPet.getId();
    }

    @Override
    public PetDTO findPet(Integer petId) {
      return petRepository.findById(petId)
                .map(petMapper::petToPetDTO)
                .orElseThrow(() -> new PetNotFoundException(String.format(PetNotFound, petId)));
    }

    @Override
    public void updatePetDetails(Integer petId, String petName) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new PetNotFoundException(String.format(PetNotFound, petId)));
        pet.setName(petName);
        petRepository.save(pet);
    }

    @Override
    public void deletePet(Integer petId) {
        boolean petExists = petRepository.existsById(petId);
        if(!petExists){
            throw new PetNotFoundException(String.format(PetNotFound, petId));
        }
        else{
            petRepository.deleteById(petId);
        }
    }

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
