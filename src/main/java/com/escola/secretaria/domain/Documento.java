package com.escola.secretaria.domain;

import com.escola.secretaria.domain.enums.TipoDocumento;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table (name="documento")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Documento {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private TipoDocumento tipo;
    private LocalDate dataEmissao;
    @ManyToOne @JoinColumn(name = "aluno_id")
    private Aluno aluno;
}
