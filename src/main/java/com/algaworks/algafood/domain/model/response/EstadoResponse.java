package com.algaworks.algafood.domain.model.response;

import com.algaworks.algafood.core.validation.Groups;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EstadoResponse {

	@NotNull(groups = Groups.EstadoId.class)
	private Long id;
	
	@NotBlank
	private String nome;
	
}