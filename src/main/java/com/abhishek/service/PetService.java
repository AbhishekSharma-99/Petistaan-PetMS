package com.abhishek.service;

import com.abhishek.dto.PetDTO;
import com.abhishek.dto.PetStatisticsDTO;

public interface PetService {

    Integer savePet(PetDTO petDTO);

    PetDTO findPet(Integer petId);

    void updatePetDetails(Integer petId, String petName);

    void deletePet(Integer petId);

    PetStatisticsDTO getStatistics();

}
