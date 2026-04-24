package com.escola.secretaria.service;

import com.escola.secretaria.domain.Usuario;
import com.escola.secretaria.dto.request.UsuarioRequest;
import com.escola.secretaria.dto.response.UsuarioResponse;
import com.escola.secretaria.exception.RecursoNaoEncontradoException;
import com.escola.secretaria.mapper.UsuarioMapper;
import com.escola.secretaria.repository.UsuarioRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<UsuarioResponse> findAll() {
        return usuarioRepository.findAll()
                .stream()
                .map(u -> usuarioMapper.toResponse(u))
                .toList();
    }

    @Transactional(readOnly = true)
    public UsuarioResponse findById(Long id) {
        return usuarioRepository.findById(id)
                .map(u -> usuarioMapper.toResponse(u))
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado. Id: " + id));
    }

    @Transactional
    public UsuarioResponse save(UsuarioRequest request) {
        Usuario usuario = usuarioMapper.toEntity(request);
        usuario.setSenha(passwordEncoder.encode(request.senha()));
        return usuarioMapper.toResponse(usuarioRepository.save(usuario));
    }

    @Transactional
    public UsuarioResponse update(Long id, UsuarioRequest request) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado. Id: " + id));
        usuarioMapper.updateEntity(request, usuario);
        if (request.senha() != null && !request.senha().isBlank()) {
            usuario.setSenha(passwordEncoder.encode(request.senha()));
        }
        return usuarioMapper.toResponse(usuarioRepository.save(usuario));
    }

    @Transactional
    public void delete(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado. Id: " + id));
        usuarioRepository.delete(usuario);
    }
}