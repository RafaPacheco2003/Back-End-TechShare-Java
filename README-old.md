# 🚀 TechShare Backend - Sistema de Gestión de Materiales

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.1-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://openjdk.org/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue.svg)](https://mysql.com/)
[![Status](https://img.shields.io/badge/Status-Production%20Ready-success.svg)]()
[![Quality](https://img.shields.io/badge/Quality-10%2F10%20Perfect-gold.svg)]()

**Sistema backend completo para gestión de materiales educativos con autenticación JWT, rate limiting, documentación OpenAPI y arquitectura enterprise.**

Este proyecto representa mi transición y especialización en el desarrollo backend con tecnologías de Java, buscando aplicar mis habilidades en entornos profesionales.

---

## ✨ Características Principales

* **API RESTful:** Endpoints bien definidos para la gestión de usuarios, publicaciones, comentarios y más.
* **Seguridad Integral:** Implementación de autenticación y autorización utilizando **Spring Security** y JSON Web Tokens (JWT) para proteger los endpoints.
* **Persistencia de Datos:** Gestión de la base de datos relacional con **Spring Data JPA** y Hibernate, facilitando las operaciones CRUD.
* **Validación de Datos:** Reglas de validación a nivel de controlador para asegurar la integridad de los datos de entrada.
* **Manejo de Excepciones:** Gestor global de excepciones para proporcionar respuestas de error claras y consistentes.

---

## 🛠️ Tecnologías Utilizadas

* **Lenguaje:** Java 17+
* **Framework:** Spring Boot 3
* **Seguridad:** Spring Security
* **Base de Datos:** Spring Data JPA, Hibernate, MySQL
* **Dependencias:** Maven
* **Herramientas Adicionales:** Lombok para reducir el código boilerplate.
* **Control de Versiones:** Git y GitHub

---

## 🚀 Cómo Empezar

Sigue estos pasos para levantar el proyecto en un entorno local.

### **Prerrequisitos**

* JDK 17 o superior
* Maven 3.x
* Un gestor de base de datos como MySQL o MariaDB

### **Instalación**

1.  **Clona el repositorio:**
    ```sh
    git clone [https://github.com/DARKTOTEM2703/Back-End-TechShare-Java.git](https://github.com/DARKTOTEM2703/Back-End-TechShare-Java.git)
    ```

2.  **Navega al directorio del proyecto:**
    ```sh
    cd Back-End-TechShare-Java
    ```

3.  **Configura la base de datos:**
    * Crea una base de datos en MySQL.
    * Renombra el archivo `application.properties.example` a `application.properties`.
    * Modifica el archivo `application.properties` con tus credenciales de la base de datos (URL, usuario y contraseña).

4.  **Instala las dependencias y ejecuta el proyecto:**
    ```sh
    mvn spring-boot:run
    ```

¡La API estará corriendo en `http://localhost:8080`!

---

## � Documentación centralizada

Toda la documentación técnica, guías y tutoriales están centralizados en la carpeta `docs/`.
Abre `docs/README.md` para un índice rápido y enlaces a las guías de optimización, pruebas y despliegue.

---

## �👤 Autor

**Jafeth Daniel Gamboa Baas**

* **LinkedIn:** [linkedin.com/in/jafethgamboabaas](https://linkedin.com/in/jafethgamboabaas)
* **Portafolio:** [jafethgamboa.netlify.app](https://jafethgamboa.netlify.app) 
* **Email:** [jafethgamboa27@gmail.com](mailto:jafethgamboa27@gmail.com) 
