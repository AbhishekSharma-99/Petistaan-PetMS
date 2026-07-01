package com.abhishek.controller;

import com.abhishek.dto.PetDTO;
import com.abhishek.dto.PetStatisticsDTO;
import com.abhishek.dto.UpdatePetDTO;
import com.abhishek.service.PetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/pets")
@RestController
public class PetController {

    private final PetService petService;

    @PostMapping
    public ResponseEntity<Integer> savePet(@RequestBody PetDTO petDTO){
        Integer petId = petService.savePet(petDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(petId);
    }

    @GetMapping("/{petId}")
    public ResponseEntity<PetDTO> findPet(@PathVariable Integer petId){
        return ResponseEntity.status(HttpStatus.OK).body(petService.findPet(petId));
    }

    @PatchMapping("/{petId}")
    public ResponseEntity<Void> updatePet(@PathVariable Integer petId, @RequestBody UpdatePetDTO updatePetDTO){
        petService.updatePetDetails(petId, updatePetDTO.name());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/{petId}")
    public ResponseEntity<Void> deletePet(@PathVariable Integer petId){
        petService.deletePet(petId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/stats")
    public ResponseEntity<PetStatisticsDTO> getStatistics() {
        PetStatisticsDTO petStatisticsDTO = petService.getStatistics();
        return ResponseEntity.status(HttpStatus.OK).body(petStatisticsDTO);
    }



}