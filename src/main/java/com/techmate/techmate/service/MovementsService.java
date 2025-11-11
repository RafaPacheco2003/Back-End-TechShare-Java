package com.techmate.techmate.service;

import org.springframework.stereotype.Service;

/**
 * ✅ Interface Segregation Principle (ISP)
 * 
 * Interfaz consolidada que agrupa las interfaces segregadas.
 * Esto mantiene compatibilidad hacia atrás mientras permite que los clientes
 * dependan solo de las interfaces específicas que necesitan.
 * 
 * Clientes que necesitan:
 * - Solo CRUD → Implementan IMovementCrudService
 * - Solo Queries → Implementan IMovementQueryService
 * - Solo Token → Implementan ITokenService
 * - Todo → Implementan MovementsService (como ahora)
 * 
 * @author TechShare Team
 * @version 1.0 (Refactored with ISP)
 */
@Service
public interface MovementsService extends IMovementCrudService, IMovementQueryService, ITokenService {

    // Interfaz consolidada que combina todos los métodos segregados
    // Esto es SOLO para mantener compatibilidad hacia atrás
    // Los nuevos clientes deben usar las interfaces segregadas específicas

}

