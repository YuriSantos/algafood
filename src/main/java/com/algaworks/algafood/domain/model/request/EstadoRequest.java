package com.algaworks.algafood.domain.model.request;

import com.algaworks.algafood.core.validation.Groups;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EstadoRequest {

	@NotNull(groups = Groups.EstadoId.class)
	private Long id;
	
	@NotBlank
	private String nome;
	
}