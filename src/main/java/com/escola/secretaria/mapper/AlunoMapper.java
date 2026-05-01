package com.escola.secretaria.mapper;

import com.escola.secretaria.domain.Aluno;
import com.escola.secretaria.domain.Endereco;
import com.escola.secretaria.domain.RematriculaInfo;
import com.escola.secretaria.domain.Responsavel;
import com.escola.secretaria.dto.request.AlunoRequest;
import com.escola.secretaria.dto.request.EnderecoDto;
import com.escola.secretaria.dto.request.ResponsavelDto;
import com.escola.secretaria.dto.response.AlunoDetalheResponse;
import com.escola.secretaria.dto.response.AlunoResponse;
import com.escola.secretaria.dto.response.RematriculadoResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface AlunoMapper {

    @Mapping(source = "rematriculado", target = "rematriculado",
            qualifiedByName = "mapRematriculado")
    AlunoResponse toResponse(Aluno aluno);

    AlunoDetalheResponse toDetalheResponse(Aluno aluno);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "rematriculado", ignore = true)
    Aluno toEntity(AlunoRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "rematriculado", ignore = true)
    void updateEntity(AlunoRequest request, @MappingTarget Aluno aluno);

    Endereco toEndereco(EnderecoDto dto);

    com.escola.secretaria.domain.CertidaoNascimento toCertidao(com.escola.secretaria.dto.request.CertidaoNascimentoDto dto);

    Responsavel toResponsavel(ResponsavelDto dto);

    @Named("mapRematriculado")
    default RematriculadoResponse mapRematriculado(RematriculaInfo info) {
        if (info == null || info.getAnoLetivo() == null) {
            return null;
        }
        return new RematriculadoResponse(
                null, null, null,
                info.getAnoLetivo(),
                info.getSerie(),
                info.getTurma(),
                info.getTurno());
    }
}
