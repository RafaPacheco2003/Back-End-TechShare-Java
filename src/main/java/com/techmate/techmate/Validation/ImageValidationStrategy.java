package com.techmate.techmate.Validation;

import org.springframework.web.multipart.MultipartFile;

public interface ImageValidationStrategy {
    /**
     * Valida un archivo de imagen. La implementación debe lanzar una
     * IllegalArgumentException u otra excepción en caso de invalidación.
     *
     * @param image el archivo multipart de la imagen
     */
    void validate(MultipartFile image);
}
