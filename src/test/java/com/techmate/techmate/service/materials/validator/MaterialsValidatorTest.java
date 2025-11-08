package com.techmate.techmate.service.materials.validator;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.techmate.techmate.repository.MaterialsRepository;
import com.techmate.techmate.repository.RoleRepository;
import com.techmate.techmate.repository.SubCategoriesRepository;

@ExtendWith(MockitoExtension.class)
public class MaterialsValidatorTest {

    @Mock
    MaterialsRepository materialsRepository;

    @Mock
    RoleRepository roleRepository;

    @Mock
    SubCategoriesRepository subCategoriesRepository;

    @InjectMocks
    MaterialsValidator validator;

    @Test
    void validateUniqueName_whenExists_throws() {
        when(materialsRepository.findByName("X")).thenReturn(new com.techmate.techmate.entity.Materials());
    assertThatThrownBy(() -> validator.validateUniqueName("X")).isInstanceOf(com.techmate.techmate.exception.ValidationException.class);
    }

    @Test
    void validateUniqueName_whenNotExists_ok() {
        when(materialsRepository.findByName("Y")).thenReturn(null);
        // should not throw
        validator.validateUniqueName("Y");
    }

    @Test
    void validateRolesExist_missing_throws() {
        when(roleRepository.findById(1)).thenReturn(java.util.Optional.empty());
        // now should throw NotFoundException
    assertThatThrownBy(() -> validator.validateRolesExist(java.util.Arrays.asList(1))).isInstanceOf(com.techmate.techmate.exception.NotFoundException.class);
    }

    @Test
    void validateSubCategoryExists_missing_throws() {
        when(subCategoriesRepository.findById(2)).thenReturn(java.util.Optional.empty());
        // now should throw NotFoundException
    assertThatThrownBy(() -> validator.validateSubCategoryExists(2)).isInstanceOf(com.techmate.techmate.exception.NotFoundException.class);
    }
}
