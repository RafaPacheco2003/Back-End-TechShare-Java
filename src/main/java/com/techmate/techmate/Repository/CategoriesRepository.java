package com.techmate.techmate.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.techmate.techmate.Entity.Categories;

@Repository
public interface CategoriesRepository extends JpaRepository<Categories, Integer>{
    
    // Método para encontrar el nombre de la categoría por ID
    String findNameByCategoryId(int categoryId);
}
