package com.algaworks.algafood.api.mapper;

import com.algaworks.algafood.domain.model.Restaurante;
import com.algaworks.algafood.domain.model.request.RestauranteRequest;
import com.algaworks.algafood.domain.model.response.RestauranteResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RestauranteConverter {

    Restaurante convert(RestauranteRequest restauranteRequest);

    RestauranteResponse convert(Restaurante restaurante);

    List<RestauranteResponse> convert(List<Restaurante> restaurantes);
}
