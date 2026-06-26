package com.abhishek.controller;

import com.abhishek.dto.PetStatisticsDTO;
import com.abhishek.service.PetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/pets")
@RestController
public class PetController {

    private final PetService petService;

    @GetMapping("/stats")
    public ResponseEntity<PetStatisticsDTO> getStatistics() {
        PetStatisticsDTO petStatisticsDTO = petService.getStatistics();
        return ResponseEntity.status(HttpStatus.OK).body(petStatisticsDTO);
    }

}