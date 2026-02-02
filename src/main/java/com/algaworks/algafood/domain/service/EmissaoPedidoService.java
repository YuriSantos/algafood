package com.algaworks.algafood.domain.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.algaworks.algafood.domain.exception.NegocioException;
import com.algaworks.algafood.domain.exception.PedidoNaoEncontradoException;
import com.algaworks.algafood.domain.model.Cidade;
import com.algaworks.algafood.domain.model.FormaPagamento;
import com.algaworks.algafood.domain.model.Pedido;
import com.algaworks.algafood.domain.model.Produto;
import com.algaworks.algafood.domain.model.Restaurante;
import com.algaworks.algafood.domain.model.Usuario;
import com.algaworks.algafood.domain.repository.PedidoRepository;

@Slf4j
@Service
public class EmissaoPedidoService {

	@Autowired
	private PedidoRepository pedidoRepository;

	@Autowired
	private CadastroRestauranteService cadastroRestaurante;

	@Autowired
	private CadastroCidadeService cadastroCidade;

	@Autowired
	private CadastroUsuarioService cadastroUsuario;

	@Autowired
	private CadastroProdutoService cadastroProduto;

	@Autowired
	private CadastroFormaPagamentoService cadastroFormaPagamento;

	@Transactional
	public Pedido emitir(Pedido pedido) {
		log.info("Iniciando emissão de novo pedido para o cliente: {}", pedido.getCliente().getId());
		validarPedido(pedido);
		validarItens(pedido);

		pedido.setTaxaFrete(pedido.getRestaurante().getTaxaFrete());
		pedido.calcularValorTotal();

		log.info("Pedido emitido com sucesso. Valor total: {}", pedido.getValorTotal());
		return pedidoRepository.save(pedido);
	}

	private void validarPedido(Pedido pedido) {
		log.debug("Validando dados do pedido...");
		Cidade cidade = cadastroCidade.buscarOuFalhar(pedido.getEnderecoEntrega().getCidade().getId());
		Usuario cliente = cadastroUsuario.buscarOuFalhar(pedido.getCliente().getId());
		Restaurante restaurante = cadastroRestaurante.buscarOuFalhar(pedido.getRestaurante().getId());
		FormaPagamento formaPagamento = cadastroFormaPagamento.buscarOuFalhar(pedido.getFormaPagamento().getId());

		pedido.getEnderecoEntrega().setCidade(cidade);
		pedido.setCliente(cliente);
		pedido.setRestaurante(restaurante);
		pedido.setFormaPagamento(formaPagamento);
		
		if (restaurante.naoAceitaFormaPagamento(formaPagamento)) {
			log.warn("Forma de pagamento '{}' não é aceita pelo restaurante '{}'", formaPagamento.getDescricao(), restaurante.getNome());
			throw new NegocioException(String.format("Forma de pagamento '%s' não é aceita por esse restaurante.",
					formaPagamento.getDescricao()));
		}
		log.debug("Dados do pedido validados com sucesso.");
	}

	private void validarItens(Pedido pedido) {
		log.debug("Validando itens do pedido...");
		pedido.getItens().forEach(item -> {
			Produto produto = cadastroProduto.buscarOuFalhar(
					pedido.getRestaurante().getId(), item.getProduto().getId());
			
			item.setPedido(pedido);
			item.setProduto(produto);
			item.setPrecoUnitario(produto.getPreco());
		});
		log.debug("Itens do pedido validados com sucesso.");
	}
	
	public Pedido buscarOuFalhar(String codigoPedido) {
		log.debug("Buscando pedido com código: {}", codigoPedido);
		return pedidoRepository.findByCodigo(codigoPedido)
			.orElseThrow(() -> {
				log.warn("Pedido não encontrado com código: {}", codigoPedido);
				return new PedidoNaoEncontradoException(codigoPedido);
			});
	}

}
