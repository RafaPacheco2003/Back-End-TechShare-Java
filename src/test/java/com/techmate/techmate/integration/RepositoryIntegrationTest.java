package com.techmate.techmate.integration;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.repository.CrudRepository;
import org.springframework.test.context.ActiveProfiles;

import com.techmate.techmate.repository.BorrowRepository;
import com.techmate.techmate.repository.CategoriesRepository;
import com.techmate.techmate.repository.MaterialsRepository;
import com.techmate.techmate.repository.MovementsRepository;
import com.techmate.techmate.repository.RoleRepository;
import com.techmate.techmate.repository.SubCategoriesRepository;
import com.techmate.techmate.repository.UsuarioRepository;

@SpringBootTest
@ActiveProfiles("test")
class RepositoryIntegrationTest {

    @Autowired(required = false)
    private BorrowRepository borrowRepository;

    @Autowired(required = false)
    private MaterialsRepository materialsRepository;

    @Autowired(required = false)
    private MovementsRepository movementsRepository;

    @Autowired(required = false)
    private RoleRepository roleRepository;

    @Autowired(required = false)
    private CategoriesRepository categoriesRepository;

    @Autowired(required = false)
    private SubCategoriesRepository subCategoriesRepository;

    @Autowired(required = false)
    private UsuarioRepository usuarioRepository;

    @Test
    @DisplayName("BorrowRepository should be available")
    void borrowRepositoryShouldBeAvailable() {
        assertNotNull(borrowRepository);
    }

    @Test
    @DisplayName("MaterialsRepository should be available")
    void materialsRepositoryShouldBeAvailable() {
        assertNotNull(materialsRepository);
    }

    @Test
    @DisplayName("MovementsRepository should be available")
    void movementsRepositoryShouldBeAvailable() {
        assertNotNull(movementsRepository);
    }

    @Test
    @DisplayName("RoleRepository should be available")
    void roleRepositoryShouldBeAvailable() {
        assertNotNull(roleRepository);
    }

    @Test
    @DisplayName("CategoriesRepository should be available")
    void categoriesRepositoryShouldBeAvailable() {
        assertNotNull(categoriesRepository);
    }

    @Test
    @DisplayName("SubCategoriesRepository should be available")
    void subCategoriesRepositoryShouldBeAvailable() {
        assertNotNull(subCategoriesRepository);
    }

    @Test
    @DisplayName("UsuarioRepository should be available")
    void usuarioRepositoryShouldBeAvailable() {
        assertNotNull(usuarioRepository);
    }

    @Test
    @DisplayName("All repositories should implement CrudRepository interface")
    void repositoriesShouldImplementCrudRepository() {
        assertTrue(borrowRepository instanceof CrudRepository);
        assertTrue(materialsRepository instanceof CrudRepository);
        assertTrue(movementsRepository instanceof CrudRepository);
        assertTrue(roleRepository instanceof CrudRepository);
        assertTrue(categoriesRepository instanceof CrudRepository);
        assertTrue(subCategoriesRepository instanceof CrudRepository);
        assertTrue(usuarioRepository instanceof CrudRepository);
    }

    @Test
    @DisplayName("BorrowRepository should have findAll method")
    void borrowRepositoryShouldHaveFindAll() {
        assertTrue(borrowRepository.findAll() instanceof Iterable);
    }

    @Test
    @DisplayName("MaterialsRepository should have findAll method")
    void materialsRepositoryShouldHaveFindAll() {
        assertTrue(materialsRepository.findAll() instanceof Iterable);
    }

    @Test
    @DisplayName("MovementsRepository should have findAll method")
    void movementsRepositoryShouldHaveFindAll() {
        assertTrue(movementsRepository.findAll() instanceof Iterable);
    }

    @Test
    @DisplayName("RoleRepository should have findAll method")
    void roleRepositoryShouldHaveFindAll() {
        assertTrue(roleRepository.findAll() instanceof Iterable);
    }

    @Test
    @DisplayName("CategoriesRepository should have findAll method")
    void categoriesRepositoryShouldHaveFindAll() {
        assertTrue(categoriesRepository.findAll() instanceof Iterable);
    }

    @Test
    @DisplayName("SubCategoriesRepository should have findAll method")
    void subCategoriesRepositoryShouldHaveFindAll() {
        assertTrue(subCategoriesRepository.findAll() instanceof Iterable);
    }

    @Test
    @DisplayName("UsuarioRepository should have findAll method")
    void usuarioRepositoryShouldHaveFindAll() {
        assertTrue(usuarioRepository.findAll() instanceof Iterable);
    }

    @Test
    @DisplayName("All repositories should be properly wired")
    void allRepositoriesShouldBeProperlyWired() {
        assertAll(
                () -> assertNotNull(borrowRepository),
                () -> assertNotNull(materialsRepository),
                () -> assertNotNull(movementsRepository),
                () -> assertNotNull(roleRepository),
                () -> assertNotNull(categoriesRepository),
                () -> assertNotNull(subCategoriesRepository),
                () -> assertNotNull(usuarioRepository)
        );
    }
}
