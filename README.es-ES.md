

# Una Plataforma de "Conexión de Voluntarios" para Eventos de Salud

> **Conéctate** conmigo a través de [Upwork](https://www.upwork.com/freelancers/~018d8a1d9dcab5ac61), [LinkedIn](https://linkedin.com/in/alifruliarso), [Email](mailto:alif.ruliarso@gmail.com), [Twitter](https://twitter.com/alifruliarso)

<div align="center">
<em>Construido con :</em>

<img src="https://img.shields.io/badge/Spring-000000.svg?style=flat&logo=Spring&logoColor=white" alt="Spring">
<img src="https://img.shields.io/badge/Spring_Boot-6DB33F?style=flat&logo=spring-boot&logoColor=white" >
<img src="https://img.shields.io/badge/Spring%20AI-green">
<img src="https://img.shields.io/badge/Thymeleaf-%23005C0F.svg?style=flat&logo=Thymeleaf&logoColor=white" >
<img src="https://img.shields.io/badge/Bootstrap-563D7C?style=flat&logo=bootstrap&logoColor=white" >
<img src="https://img.shields.io/badge/JavaScript-F7DF1E.svg?style=flat&logo=JavaScript&logoColor=black" alt="JavaScript">
<img src="https://img.shields.io/badge/Apache%20Maven-C71A36.svg?style=flat&logo=Apache-Maven&logoColor=white">
<img src="https://img.shields.io/badge/GridDB%20Cloud-8A2BE2">
<img src="https://img.shields.io/badge/OpenAI-000000.svg?style=flat&logo=OpenAI&logoColor=white" alt="OpenAI">
</div>
<br>

## Descripción del Proyecto

### Características

**Para Organizaciones:**

    * Publicar nuevas oportunidades de voluntariado.
    * Especificar las habilidades requeridas para cada oportunidad (por ejemplo, "Enfermera Registrada", "Primeros Auxilios").
    * Revisar y gestionar la lista de voluntarios que se han registrado para tus eventos.
    * Aprobar o denegar solicitudes de voluntarios.

**Para Voluntarios:**

    * Crear un perfil de usuario personal.
    * Agregar tus habilidades profesionales y certificaciones (por ejemplo, "Certificado en RCP", "Flebotomista").
    * Registrarse para eventos que coincidan con tus habilidades.

**Sistema Central:**

    * La lógica de emparejamiento inteligente solo permite que los voluntarios con las habilidades correctas y verificadas se registren para roles restringidos.
    * Control de acceso basado en roles para usuarios (Voluntarios vs. Administradores de Organizaciones).

## Requisitos Previos

- **Stack Tecnológico:** Spring Boot, Spring Security, Spring AI, Thymeleaf, Bootstrap 5, Maven
- **Base de Datos:** GridDB Cloud

## Desarrollo

Actualiza tu conexión a la base de datos en `application.properties`

    ```text
    griddbcloud.base-url=https://cloud5197.griddb.com:443/griddb/v2/gs_cluster
    griddbcloud.auth-token=XXX
    ```

Configura la clave API de OpenAI. **Exporta** tus claves API de OpenAI como variables de entorno:

    ```bash
    export OPENAI_API_KEY="your_api_key_here"
    ```

## Compilación

La aplicación puede compilarse utilizando el siguiente comando:

    ```bash
    mvnw clean package
    ```

Inicia tu aplicación con el siguiente comando:

    ```bash
    mvnw spring-boot:run
    ```

Después de iniciar la aplicación, podrá accederse a ella en `localhost:8080`.

Formatear código:

    ```bash
    .\mvnw spotless:check

    .\mvnw spotless:apply
    ```

## Lecturas Adicionales

- [Documentación de Maven](https://maven.apache.org/guides/index.html)  
- [Referencia de Spring Boot](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/)  
- [Spring AI: un framework de aplicaciones para ingeniería de IA.](https://spring.io/projects/spring-ai)
- [Documentación de Thymeleaf](https://www.thymeleaf.org/documentation.html)  
- [Documentación de Bootstrap](https://getbootstrap.com/docs/5.3/getting-started/introduction/)  
- [Htmx en resumen](https://htmx.org/docs/)  
- [Aprende Spring Boot con Thymeleaf](https://www.wimdeblauwe.com/books/taming-thymeleaf/)  
- [Prototipado rápido para Spring Boot](https://bootify.io/next-steps/).
