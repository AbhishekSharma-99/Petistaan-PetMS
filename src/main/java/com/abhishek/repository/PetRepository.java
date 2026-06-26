package com.abhishek.repository;

import com.abhishek.entity.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PetRepository extends JpaRepository<Pet, Integer> {

    @Query("""
                SELECT
                    CASE
                     WHEN TYPE(p) = DomesticPet THEN 'DOMESTIC'
                     WHEN TYPE(p) = WildPet THEN 'WILD'
                    END,
                    p.gender,
                    p.petType,
                    COUNT(p)
                FROM Pet p
                GROUP BY
                     CASE
                         WHEN TYPE(p) = DomesticPet THEN 'DOMESTIC'
                         WHEN TYPE(p) = WildPet THEN 'WILD'
                     END,
                p.gender,
                p.petType
    """)
    List<Object[]> fetchStatistics();

}
