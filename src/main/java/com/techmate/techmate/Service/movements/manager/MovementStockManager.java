package com.techmate.techmate.Service.movements.manager;

import org.springframework.stereotype.Component;

import com.techmate.techmate.entity.Materials;
import com.techmate.techmate.entity.Movements;

@Component
public class MovementStockManager {

    public void adjustMaterialStock(Materials materials, Movements movements) {
        switch (movements.getMoveType()) {
            case IN:
                materials.setBorrowable_stock(materials.getBorrowable_stock() + movements.getQuantity());
                materials.setStock(materials.getStock() + movements.getQuantity());
                break;
            case OUT:
                if (materials.getStock() < movements.getQuantity()) {
                    throw new IllegalArgumentException("Stock insuficiente para el material");
                }
                materials.setBorrowable_stock(materials.getBorrowable_stock() - movements.getQuantity());
                materials.setStock(materials.getStock() - movements.getQuantity());
                break;
            case ADJUST:
                int difference = movements.getQuantity() - materials.getStock();
                materials.setBorrowable_stock(materials.getBorrowable_stock() + difference);
                materials.setStock(movements.getQuantity());
                break;
            default:
                throw new IllegalArgumentException("Tipo de movimiento inválido");
        }
    }
}
