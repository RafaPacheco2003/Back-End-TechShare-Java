package com.techmate.techmate.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;

import com.techmate.techmate.DTO.MaterialsDTO;
import com.techmate.techmate.Entity.Materials;

import jakarta.servlet.http.HttpServletRequest;

public interface MaterialsService {

    MaterialsDTO createMaterials(MaterialsDTO materialsDTO, MultipartFile iMultipartFile);
    MaterialsDTO getMaterialsById(int materialsId);
    MaterialsDTO updateMaterials(int materialsId, MaterialsDTO materialsDTO, MultipartFile iMultipartFile);
    void deleteMaterials(int materialsId);
    List<MaterialsDTO> getAllMaterials();
    
   String getMaterialsNameById(int materialId);


   
}
