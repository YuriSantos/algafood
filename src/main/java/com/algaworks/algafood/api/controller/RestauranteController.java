package com.algaworks.algafood.api.controller;

import java.util.List;

import com.algaworks.algafood.api.mapper.RestauranteConverter;
import com.algaworks.algafood.domain.model.request.RestauranteRequest;
import com.algaworks.algafood.domain.model.response.RestauranteResponse;
import jakarta.validation.Valid;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.algaworks.algafood.domain.exception.CozinhaNaoEncontradaException;
import com.algaworks.algafood.domain.exception.NegocioException;
import com.algaworks.algafood.domain.model.Restaurante;
import com.algaworks.algafood.domain.repository.RestauranteRepository;
import com.algaworks.algafood.domain.service.CadastroRestauranteService;

@Slf4j
@RestController
@RequestMapping(value = "/restaurantes")
public class RestauranteController {

	@Autowired
	private RestauranteRepository restauranteRepository;
	
	@Autowired
	private CadastroRestauranteService cadastroRestaurante;

	@Autowired
	private RestauranteConverter converter;

	private static final Logger LOGGER = LoggerFactory.getLogger(RestauranteController.class);

	@GetMapping
	public List<Restaurante> listar() {
		LOGGER.info("Recebida a requisição de getRestaurantes");
		return restauranteRepository.findAll();
	}
	
	@GetMapping("/{restauranteId}")
	public RestauranteResponse buscar(@PathVariable Long restauranteId) {
		LOGGER.info("Recebida a requisição de getRestaurante pelo restauranteId {}", restauranteId);
		return converter.convert(cadastroRestaurante.buscarOuFalhar(restauranteId));
	}
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public RestauranteResponse adicionar(@RequestBody @Valid RestauranteRequest restauranteRequest) {
		LOGGER.info("Recebida a requisição para adicionar um novo restaurante {}", restauranteRequest.toString());
		try {
			return converter.convert(
					cadastroRestaurante.salvar(converter.convert(restauranteRequest)));
		} catch (CozinhaNaoEncontradaException e) {
			throw new NegocioException(e.getMessage());
		}
	}
	
	@PutMapping("/{restauranteId}")
	public RestauranteResponse atualizar(@PathVariable Long restauranteId,
			@RequestBody @Valid RestauranteRequest restauranteRequest) {
		LOGGER.info("Recebida a requisição para atualizar o novo restaurante {}", restauranteRequest.getNome());
		try {
			Restaurante restauranteAtual = cadastroRestaurante.buscarOuFalhar(restauranteId);
			
			BeanUtils.copyProperties(restauranteRequest, restauranteAtual,
					"id", "formasPagamento", "endereco", "dataCadastro", "produtos");

			return converter.convert(cadastroRestaurante.salvar(restauranteAtual));
		} catch (CozinhaNaoEncontradaException e) {
			throw new NegocioException(e.getMessage());
		}
	}
	
}
