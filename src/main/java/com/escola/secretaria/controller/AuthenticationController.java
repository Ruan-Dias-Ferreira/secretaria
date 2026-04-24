package com.escola.secretaria.controller;

import com.escola.secretaria.domain.Usuario;
import com.escola.secretaria.domain.enums.Role;
import com.escola.secretaria.dto.request.LoginRequest;
import com.escola.secretaria.dto.request.UsuarioRequest;
import com.escola.secretaria.repository.UsuarioRepository;
import com.escola.secretaria.security.TokenService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody @Valid LoginRequest request) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(
                request.login(), request.senha());
        var auth = authenticationManager.authenticate(usernamePassword);
        var token = tokenService.generateToken((Usuario) auth.getPrincipal());
        return ResponseEntity.ok(token);
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody @Valid UsuarioRequest request) {
        if (request.role() == Role.SECRETARIA) {
            var authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null
                    || !(authentication.getPrincipal() instanceof Usuario usuarioLogado)
                    || usuarioLogado.getRole() != Role.SECRETARIA) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "Apenas SECRETARIA pode criar contas com role SECRETARIA");
            }
        }
        if (usuarioRepository.findByLogin(request.login()) != null) {
            return ResponseEntity.badRequest().build();
        }
        String senhaCriptografada = passwordEncoder.encode(request.senha());
        Usuario usuario = new Usuario();
        usuario.setLogin(request.login());
        usuario.setSenha(senhaCriptografada);
        usuario.setRole(request.role());
        usuarioRepository.save(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
