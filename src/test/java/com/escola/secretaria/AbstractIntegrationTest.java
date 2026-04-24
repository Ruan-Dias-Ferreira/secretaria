package com.escola.secretaria;

import com.escola.secretaria.repository.AlunoRepository;
import com.escola.secretaria.repository.DisciplinaRepository;
import com.escola.secretaria.repository.FrequenciaRepository;
import com.escola.secretaria.repository.MatriculaRepository;
import com.escola.secretaria.repository.NotaRepository;
import com.escola.secretaria.repository.TurmaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@ActiveProfiles("integration")
@Testcontainers
public abstract class AbstractIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired protected FrequenciaRepository frequenciaRepository;
    @Autowired protected NotaRepository notaRepository;
    @Autowired protected MatriculaRepository matriculaRepository;
    @Autowired protected DisciplinaRepository disciplinaRepository;
    @Autowired protected AlunoRepository alunoRepository;
    @Autowired protected TurmaRepository turmaRepository;

    // Ordem respeita FK: dependentes primeiro, referenciados por último
    @BeforeEach
    void limparBanco() {
        frequenciaRepository.deleteAll();
        notaRepository.deleteAll();
        matriculaRepository.deleteAll();
        disciplinaRepository.deleteAll();
        alunoRepository.deleteAll();
        turmaRepository.deleteAll();
    }
}
