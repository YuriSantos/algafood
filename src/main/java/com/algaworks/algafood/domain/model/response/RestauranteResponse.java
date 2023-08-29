package com.algaworks.algafood.domain.model.response;

import com.algaworks.algafood.domain.model.Cozinha;
import com.algaworks.algafood.domain.model.Endereco;
import com.algaworks.algafood.domain.model.FormaPagamento;
import com.algaworks.algafood.domain.model.Produto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class RestauranteResponse {

	private Long id;
	
	@NotBlank
	private String nome;
	
	@NotNull
	@PositiveOrZero
	private BigDecimal taxaFrete;
	
	@Valid
	@NotNull
	private Cozinha cozinha;

	private Endereco endereco;

	private OffsetDateTime dataCadastro;

	private OffsetDateTime dataAtualizacao;

	private List<FormaPagamento> formasPagamento = new ArrayList<>();

	private List<Produto> produtos = new ArrayList<>();
	
}
