package com.techmate.techmate.Service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.techmate.techmate.DTO.MaterialsDTO;


public interface MaterialsService {

    MaterialsDTO createMaterials(MaterialsDTO materialsDTO, MultipartFile iMultipartFile);
    MaterialsDTO getMaterialsById(int materialsId);
    MaterialsDTO updateMaterials(int materialsId, MaterialsDTO materialsDTO, MultipartFile iMultipartFile);
    void deleteMaterials(int materialsId);
    List<MaterialsDTO> getAllMaterials();
    List<MaterialsDTO> getAllMaterialsSortedByPrice(boolean ascending);

    
   String getMaterialsNameById(int materialId);


   
}
