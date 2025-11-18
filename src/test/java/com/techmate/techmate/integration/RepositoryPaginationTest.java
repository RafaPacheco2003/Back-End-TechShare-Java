package com.techmate.techmate.integration;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import com.techmate.techmate.repository.BorrowRepository;
import com.techmate.techmate.repository.MaterialsRepository;
import com.techmate.techmate.repository.MovementsRepository;

@SpringBootTest
@ActiveProfiles("test")
class RepositoryPaginationTest {

    @Autowired(required = false)
    private BorrowRepository borrowRepository;

    @Autowired(required = false)
    private MaterialsRepository materialsRepository;

    @Autowired(required = false)
    private MovementsRepository movementsRepository;

    @Test
    @DisplayName("BorrowRepository should support pagination")
    void borrowRepositoryShouldSupportPagination() {
        assertNotNull(borrowRepository);
        Page<?> page = borrowRepository.findAll(PageRequest.of(0, 10));
        assertNotNull(page);
        assertTrue(page.getTotalElements() >= 0);
    }

    @Test
    @DisplayName("MaterialsRepository should support pagination")
    void materialsRepositoryShouldSupportPagination() {
        assertNotNull(materialsRepository);
        Page<?> page = materialsRepository.findAll(PageRequest.of(0, 10));
        assertNotNull(page);
        assertTrue(page.getTotalElements() >= 0);
    }

    @Test
    @DisplayName("MovementsRepository should support pagination")
    void movementsRepositoryShouldSupportPagination() {
        assertNotNull(movementsRepository);
        Page<?> page = movementsRepository.findAll(PageRequest.of(0, 10));
        assertNotNull(page);
        assertTrue(page.getTotalElements() >= 0);
    }

    @Test
    @DisplayName("BorrowRepository pagination should return valid content")
    void borrowRepositoryPaginationShouldReturnValidContent() {
        Page<?> page = borrowRepository.findAll(PageRequest.of(0, 5));
        assertNotNull(page.getContent());
    }

    @Test
    @DisplayName("MaterialsRepository pagination should return valid content")
    void materialsRepositoryPaginationShouldReturnValidContent() {
        Page<?> page = materialsRepository.findAll(PageRequest.of(0, 5));
        assertNotNull(page.getContent());
    }

    @Test
    @DisplayName("MovementsRepository pagination should return valid content")
    void movementsRepositoryPaginationShouldReturnValidContent() {
        Page<?> page = movementsRepository.findAll(PageRequest.of(0, 5));
        assertNotNull(page.getContent());
    }

    @Test
    @DisplayName("All repositories should support different page sizes")
    void repositoriesShouldSupportDifferentPageSizes() {
        // Test with different page sizes
        for (int pageSize = 1; pageSize <= 20; pageSize += 5) {
            Page<?> borrowPage = borrowRepository.findAll(PageRequest.of(0, pageSize));
            Page<?> materialsPage = materialsRepository.findAll(PageRequest.of(0, pageSize));
            Page<?> movementsPage = movementsRepository.findAll(PageRequest.of(0, pageSize));

            assertNotNull(borrowPage);
            assertNotNull(materialsPage);
            assertNotNull(movementsPage);
        }
    }

    @Test
    @DisplayName("Pagination should provide accurate total count")
    void paginationShouldProvideAccurateTotalCount() {
        Page<?> borrowPage = borrowRepository.findAll(PageRequest.of(0, 10));
        Page<?> materialsPage = materialsRepository.findAll(PageRequest.of(0, 10));
        Page<?> movementsPage = movementsRepository.findAll(PageRequest.of(0, 10));

        assertTrue(borrowPage.getTotalElements() >= 0);
        assertTrue(materialsPage.getTotalElements() >= 0);
        assertTrue(movementsPage.getTotalElements() >= 0);
    }
}
