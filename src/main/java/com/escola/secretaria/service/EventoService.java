package com.escola.secretaria.service;

import com.escola.secretaria.domain.Evento;
import com.escola.secretaria.dto.request.EventoRequest;
import com.escola.secretaria.dto.response.EventoResponse;
import com.escola.secretaria.exception.RecursoNaoEncontradoException;
import com.escola.secretaria.mapper.EventoMapper;
import com.escola.secretaria.repository.EventoRepository;
import com.escola.secretaria.repository.FrequenciaRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public class EventoService {
    private final EventoRepository eventoRepository;
    private final EventoMapper eventoMapper;
    private final FrequenciaRepository frequenciaRepository;

    @Transactional(readOnly = true)
    public List<EventoResponse> findAll() {
        return eventoRepository.findAll().stream().map(eventoMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<EventoResponse> findByPeriodo(LocalDate inicio, LocalDate fim) {
        return eventoRepository.findByDataBetween(inicio, fim).stream().map(eventoMapper::toResponse).toList();
    }

    public record EventoSaveResult(EventoResponse evento, long frequenciasRemovidas) {}

    @Transactional
    public EventoSaveResult save(EventoRequest request) {
        if (eventoRepository.existsByData(request.data())) {
            throw new IllegalArgumentException("Já existe evento cadastrado para a data: " + request.data());
        }
        long removidas = frequenciaRepository.deleteByData(request.data());
        Evento evento = eventoMapper.toEntity(request);
        return new EventoSaveResult(eventoMapper.toResponse(eventoRepository.save(evento)), removidas);
    }

    @Transactional
    public EventoSaveResult update(Long id, EventoRequest request) {
        Evento evento = eventoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Evento não encontrado. Id: " + id));
        if (!evento.getData().equals(request.data()) && eventoRepository.existsByData(request.data())) {
            throw new IllegalArgumentException("Já existe evento cadastrado para a data: " + request.data());
        }
        long removidas = 0;
        if (!evento.getData().equals(request.data())) {
            removidas = frequenciaRepository.deleteByData(request.data());
        }
        eventoMapper.updateEntity(request, evento);
        return new EventoSaveResult(eventoMapper.toResponse(eventoRepository.save(evento)), removidas);
    }

    @Transactional
    public void delete(Long id) {
        Evento evento = eventoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Evento não encontrado. Id: " + id));
        eventoRepository.delete(evento);
    }
}
