package com.techmate.techmate.Service;

import java.util.List;

import com.techmate.techmate.DTO.MaterialsDTO;


public interface MaterialsService {

    MaterialsDTO createMaterials(MaterialsDTO materialsDTO);
    MaterialsDTO getMaterialsById(int materialsId);
    MaterialsDTO updateMaterials(int materialsId, MaterialsDTO materialsDTO);
    void deleteMaterials(int materialsId);
    List<MaterialsDTO> getAllMaterials();


    
}
