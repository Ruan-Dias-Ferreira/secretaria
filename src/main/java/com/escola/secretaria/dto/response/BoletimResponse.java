package com.escola.secretaria.dto.response;

import com.escola.secretaria.domain.enums.SituacaoNota;

import java.util.List;

public record BoletimResponse (
     String nomeDisciplina,
     List<NotaResponse> notaDisciplinas,
     double mediaNotaDisciplina,
     SituacaoNota situacaoNota
){
}
