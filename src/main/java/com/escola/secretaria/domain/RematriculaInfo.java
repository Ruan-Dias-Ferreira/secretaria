package com.escola.secretaria.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RematriculaInfo {

    @Column(name = "rematriculado_ano_letivo")
    private Integer anoLetivo;

    @Column(name = "rematriculado_serie")
    private String serie;

    @Column(name = "rematriculado_turma")
    private String turma;

    @Column(name = "rematriculado_turno")
    private String turno;

    public boolean isPreenchido() {
        return anoLetivo != null;
    }
}
