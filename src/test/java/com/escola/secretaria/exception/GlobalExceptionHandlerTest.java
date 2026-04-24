package com.escola.secretaria.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new DummyController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    // DTO Dummy para testar validação
    public record DummyRequest(
            @NotBlank(message = "não pode estar em branco") String campo1,
            @NotBlank(message = "não pode estar em branco") String campo2
    ) {}

    @RestController
    static class DummyController {
        @GetMapping("/test/recurso-nao-encontrado")
        public void recursoNaoEncontrado() {
            throw new RecursoNaoEncontradoException("Recurso X não encontrado");
        }

        @GetMapping("/test/regra-negocio")
        public void regraNegocio() {
            throw new RegraDeNegocioException("Violação de regra de negócio X");
        }

        @GetMapping("/test/acesso-negado-disciplina")
        public void acessoNegadoDisciplina() {
            throw new AcessoNegadoDisciplinaException("Acesso negado à disciplina X");
        }

        @GetMapping("/test/access-denied")
        public void accessDenied() {
            throw new AccessDeniedException("Access Denied Security");
        }

        @GetMapping("/test/bad-credentials")
        public void badCredentials() {
            throw new BadCredentialsException("Bad Credentials Security");
        }

        @PostMapping("/test/validation")
        public void validation(@Valid @RequestBody DummyRequest request) {
            // não faz nada, só valida
        }

        @GetMapping("/test/generic-exception")
        public void genericException() throws Exception {
            throw new Exception("Erro genérico qualquer");
        }
    }

    @Test
    @DisplayName("Deve retornar 404 e ErrorResponse para RecursoNaoEncontradoException")
    void testHandleRecursoNaoEncontrado_retorna404ComErrorResponse() throws Exception {
        mockMvc.perform(get("/test/recurso-nao-encontrado"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.message", is("Recurso X não encontrado")))
                .andExpect(jsonPath("$.path", is("/test/recurso-nao-encontrado")))
                .andExpect(jsonPath("$.timestamp", notNullValue()))
                .andExpect(jsonPath("$.errors", hasSize(0)));
    }

    @Test
    @DisplayName("Deve retornar 400 e ErrorResponse para RegraDeNegocioException")
    void testHandleRegraDeNegocio_retorna400ComErrorResponse() throws Exception {
        mockMvc.perform(get("/test/regra-negocio"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Bad Request")))
                .andExpect(jsonPath("$.message", is("Violação de regra de negócio X")))
                .andExpect(jsonPath("$.path", is("/test/regra-negocio")))
                .andExpect(jsonPath("$.timestamp", notNullValue()))
                .andExpect(jsonPath("$.errors", hasSize(0)));
    }

    @Test
    @DisplayName("Deve retornar 403 e ErrorResponse para AcessoNegadoDisciplinaException")
    void testHandleAcessoNegadoDisciplina_retorna403ComErrorResponse() throws Exception {
        mockMvc.perform(get("/test/acesso-negado-disciplina"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status", is(403)))
                .andExpect(jsonPath("$.error", is("Forbidden")))
                .andExpect(jsonPath("$.message", is("Acesso negado à disciplina X")))
                .andExpect(jsonPath("$.path", is("/test/acesso-negado-disciplina")))
                .andExpect(jsonPath("$.timestamp", notNullValue()))
                .andExpect(jsonPath("$.errors", hasSize(0)));
    }

    @Test
    @DisplayName("Deve retornar 403 e mensagem genérica para AccessDeniedException")
    void testHandleAccessDenied_retorna403ComMensagemGenerica() throws Exception {
        mockMvc.perform(get("/test/access-denied"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status", is(403)))
                .andExpect(jsonPath("$.error", is("Forbidden")))
                .andExpect(jsonPath("$.message", is("Você não tem permissão para acessar este recurso")))
                .andExpect(jsonPath("$.path", is("/test/access-denied")))
                .andExpect(jsonPath("$.timestamp", notNullValue()))
                .andExpect(jsonPath("$.errors", hasSize(0)));
    }

    @Test
    @DisplayName("Deve retornar 401 e mensagem genérica para BadCredentialsException")
    void testHandleBadCredentials_retorna401ComMensagemGenerica() throws Exception {
        mockMvc.perform(get("/test/bad-credentials"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status", is(401)))
                .andExpect(jsonPath("$.error", is("Unauthorized")))
                .andExpect(jsonPath("$.message", is("Credenciais inválidas")))
                .andExpect(jsonPath("$.path", is("/test/bad-credentials")))
                .andExpect(jsonPath("$.timestamp", notNullValue()))
                .andExpect(jsonPath("$.errors", hasSize(0)));
    }

    @Test
    @DisplayName("Deve retornar 400 e lista de erros para MethodArgumentNotValidException")
    void testHandleMethodArgumentNotValid_retorna400ComListaDeErrors() throws Exception {
        DummyRequest request = new DummyRequest("", null);

        mockMvc.perform(post("/test/validation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Bad Request")))
                .andExpect(jsonPath("$.message", is("Erro de validação")))
                .andExpect(jsonPath("$.path", is("/test/validation")))
                .andExpect(jsonPath("$.timestamp", notNullValue()))
                .andExpect(jsonPath("$.errors", hasSize(2)))
                .andExpect(jsonPath("$.errors", containsInAnyOrder("não pode estar em branco", "não pode estar em branco")));
    }

    @Test
    @DisplayName("Deve retornar 500 e mensagem genérica para Exception")
    void testHandleGenericException_retorna500ComMensagemGenerica() throws Exception {
        mockMvc.perform(get("/test/generic-exception"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status", is(500)))
                .andExpect(jsonPath("$.error", is("Internal Server Error")))
                .andExpect(jsonPath("$.message", is("Erro interno — contate o suporte")))
                .andExpect(jsonPath("$.path", is("/test/generic-exception")))
                .andExpect(jsonPath("$.timestamp", notNullValue()))
                .andExpect(jsonPath("$.errors", hasSize(0)));
    }
}
