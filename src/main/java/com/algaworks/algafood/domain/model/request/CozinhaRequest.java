package com.algaworks.algafood.domain.model.request;

import com.algaworks.algafood.core.validation.Groups;
import com.algaworks.algafood.domain.model.Restaurante;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CozinhaRequest {

	@NotNull(groups = Groups.CozinhaId.class)
	private Long id;
	
	@NotBlank
	private String nome;

	private List<Restaurante> restaurantes = new ArrayList<>();
	
}
