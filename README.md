# A "Volunteer-Matching" Platform for Health Events

> **Connect** with me through [Upwork](https://www.upwork.com/freelancers/~018d8a1d9dcab5ac61), [LinkedIn](https://linkedin.com/in/alifruliarso), [Email](mailto:alif.ruliarso@gmail.com), [Twitter](https://twitter.com/alifruliarso)

<div align="center">
<em>Built with :</em>

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

## Project Overview

### Features

**For Organizations:**

    * Post new volunteer opportunities.
    * Specify required skills for each opportunity (e.g., "Registered Nurse," "First Aid").
    * Review and manage the list of volunteers who have signed up for your events.
    * Approve or deny volunteer applications.

**For Volunteers:**

    * Create a personal user profile.
    * Add your professional skills and certifications (e.g., "CPR Certified," "Phlebotomist").
    * Sign up for events that match your skills.

**Core System:**

    * Smart matching logic only allows volunteers with the correct, verified skills to sign up for restricted roles.
    * Role-based access control for users (Volunteers vs. Organization Admins).

## Prerequisites

- **Technology Stack:** Spring Boot, Spring Security, Spring AI, Thymeleaf, Bootstrap 5, Maven
- **Database:** GridDB Cloud

## Development

Update your database connection in `application.properties`

    ```text
    griddbcloud.base-url=https://cloud5197.griddb.com:443/griddb/v2/gs_cluster
    griddbcloud.auth-token=XXX
    ```

Configure OpenAI API key. **Export** your OpenAI API keys as  environment variables:

    ```bash
    export OPENAI_API_KEY="your_api_key_here"
    ```

## Build

The application can be built using the following command:

    ```bash
    mvnw clean package
    ```

Start your application with the following command:

    ```bash
    mvnw spring-boot:run
    ```

After starting the application, it can be accessed at `localhost:8080`.

Format code:

    ```bash
    .\mvnw spotless:check

    .\mvnw spotless:apply
    ```

## Further readings

- [Maven docs](https://maven.apache.org/guides/index.html)  
- [Spring Boot reference](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/)  
- [Spring AI: an application framework for AI engineering.](https://spring.io/projects/spring-ai)
- [Thymeleaf docs](https://www.thymeleaf.org/documentation.html)  
- [Bootstrap docs](https://getbootstrap.com/docs/5.3/getting-started/introduction/)  
- [Htmx in a nutshell](https://htmx.org/docs/)  
- [Learn Spring Boot with Thymeleaf](https://www.wimdeblauwe.com/books/taming-thymeleaf/)  
- [Rapid prototyping for Spring Boot](https://bootify.io/next-steps/).
