package com.techmate.techmate.Validation.Impl;

import org.springframework.stereotype.Component;

import com.techmate.techmate.Validation.ImageValidationStrategy;

@Component // O @Service, si lo prefieres
public class ExtensionValidationStrategy implements ImageValidationStrategy {

    @Override
    public void validate(String imagePath) {


        //Is verified if the imagePath is empty
        if(imagePath == null || imagePath.trim().isEmpty()){
            throw new IllegalArgumentException("El campo imagePath no puede estar vacio");
        }

        String[] validExtensions = { ".jpg", ".jpeg", ".png", ".gif" };
        boolean isValid = false;
        
        for (String extension : validExtensions) {
            if (imagePath.toLowerCase().endsWith(extension)) {
                isValid = true;
                break;
            }
        }

        if (!isValid) {
            throw new IllegalArgumentException(
                    "El campo imagePath debe tener una extensión válida (jpg, jpeg, png, gif).");
        }
    }
    
}
