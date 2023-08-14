package com.algaworks.algafood.core.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({ ElementType.TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = { ValorZeroIncluiDescricaoValidator.class })
public @interface ValorZeroIncluiDescricao {

	String message() default "descrição obrigatória inválida";

	Class<?>[] groups() default { };

	Class<? extends Payload>[] payload() default { };
	
	String valorField();
	
	String descricaoField();
	
	String descricaoObrigatoria();
	
}
