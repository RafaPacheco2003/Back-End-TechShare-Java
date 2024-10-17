package com.techmate.techmate.Service;

import java.util.List;
import java.util.Optional;

import com.techmate.techmate.DTO.MaterialsDTO;
import com.techmate.techmate.Entity.Materials;

public interface MaterialsService {

    MaterialsDTO createMaterials(MaterialsDTO materialsDTO);
    MaterialsDTO getMaterialsById(int materialsId);
    MaterialsDTO updateMaterials(int materialsId, MaterialsDTO materialsDTO);
    void deleteMaterials(int materialsId);
    List<MaterialsDTO> getAllMaterials();
   String getMaterialsNameById(int materialId);
}
