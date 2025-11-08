package com.techmate.techmate.demo;

import com.techmate.techmate.entity.Borrow;
import com.techmate.techmate.repository.BorrowRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

/**
 * DEMO: Comparación de queries ANTES vs DESPUÉS
 * 
 * Puedes eliminarlo después de verificar las mejoras.
 * 
 * Para ejecutar: mvnw spring-boot:run
 */
// @Component // ← Descomentar para ejecutar al arranque
public class OptimizationDemo implements CommandLineRunner {
    
    @Autowired
    private BorrowRepository borrowRepository;
    
    @Override
    public void run(String... args) throws Exception {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("🚀 DEMO: Optimizaciones JOIN FETCH");
        System.out.println("=".repeat(80) + "\n");
        
        demonstrateN1Problem();
        demonstrateOptimizedQuery();
        demonstratePagination();
        
        System.out.println("\n" + "=".repeat(80));
        System.out.println("✅ DEMO COMPLETADO - Revisa los logs arriba");
        System.out.println("=".repeat(80) + "\n");
    }
    
    /**
     * ANTES: Método que causa N+1
     */
    private void demonstrateN1Problem() {
        System.out.println("\n📍 DEMO 1: Método LEGACY (causa N+1)");
        System.out.println("-".repeat(80));
        System.out.println("⚠️  Usando: borrowRepository.findAll()");
        System.out.println("⚠️  Observa cuántas queries se ejecutan en los logs...\n");
        
        List<Borrow> borrows = borrowRepository.findAll();
        
        // Acceder a las relaciones (esto dispara queries adicionales)
        borrows.forEach(borrow -> {
            borrow.getDetails().forEach(detail -> {
                detail.getMaterials().getName(); // Query adicional
            });
        });
        
        System.out.println("✅ Datos cargados: " + borrows.size() + " préstamos");
        System.out.println("⚠️  ¿Cuántas queries viste? Probablemente MUCHAS!\n");
    }
    
    /**
     * DESPUÉS: Método optimizado
     */
    private void demonstrateOptimizedQuery() {
        System.out.println("\n📍 DEMO 2: Método OPTIMIZADO (1 query)");
        System.out.println("-".repeat(80));
        System.out.println("✅ Usando: borrowRepository.findAllOptimized()");
        System.out.println("✅ Observa que SOLO hay 1 query con joins...\n");
        
        List<Borrow> borrows = borrowRepository.findAllOptimized();
        
        // Acceder a las relaciones (YA están cargadas, sin queries adicionales)
        borrows.forEach(borrow -> {
            borrow.getDetails().forEach(detail -> {
                detail.getMaterials().getName(); // ✅ No genera query adicional
            });
        });
        
        System.out.println("✅ Datos cargados: " + borrows.size() + " préstamos");
        System.out.println("✅ ¿Cuántas queries? SOLO 1 query con joins!\n");
    }
    
    /**
     * BONUS: Paginación optimizada
     */
    private void demonstratePagination() {
        System.out.println("\n📍 DEMO 3: Paginación OPTIMIZADA");
        System.out.println("-".repeat(80));
        System.out.println("✅ Usando: borrowRepository.findAllOptimizedPaginated()");
        System.out.println("✅ Trae página 0, con 5 elementos...\n");
        
        Page<Borrow> page = borrowRepository.findAllOptimizedPaginated(
            PageRequest.of(0, 5)
        );
        
        System.out.println("✅ Página: " + page.getNumber());
        System.out.println("✅ Elementos en página: " + page.getNumberOfElements());
        System.out.println("✅ Total de páginas: " + page.getTotalPages());
        System.out.println("✅ Total de elementos: " + page.getTotalElements());
        System.out.println("✅ Queries ejecutadas: 2 (1 para datos + 1 para count)\n");
    }
}

