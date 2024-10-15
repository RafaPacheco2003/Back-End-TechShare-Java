package com.techmate.techmate.Service;

import java.util.List;

import com.techmate.techmate.DTO.MaterialsDTO;


public interface MaterialsService {

    MaterialsDTO createMaterials(MaterialsDTO materialsDTO);
    MaterialsDTO getMaterialsById(int materialsID);
    MaterialsDTO updateMaterials(int materialsID, MaterialsDTO materialsDTO);
    void deleteMaterials(int materialsID);
    List<MaterialsDTO> getAllMaterials();


    
}
