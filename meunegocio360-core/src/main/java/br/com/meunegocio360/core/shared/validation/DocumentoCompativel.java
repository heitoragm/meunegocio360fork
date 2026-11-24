package br.com.meunegocio360.core.shared.validation;

import br.com.meunegocio360.core.shared.domain.TipoPessoa;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * O documento deve ser um CPF válido para pessoa física e um CNPJ válido para pessoa jurídica
 * (RNF-BE-27). Anotação de classe para corpos de requisição; o erro é registrado no campo {@code
 * documento}.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DocumentoCompativel.Validador.class)
public @interface DocumentoCompativel {

  String message() default "documento incompatível com o tipo de pessoa";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  /** Corpos de requisição com tipo de pessoa e documento (já sem máscara). */
  interface ComDocumento {
    String tipoPessoa();

    String documento();
  }

  class Validador implements ConstraintValidator<DocumentoCompativel, ComDocumento> {

    @Override
    public boolean isValid(ComDocumento corpo, ConstraintValidatorContext contexto) {
      TipoPessoa tipo = TipoPessoa.deTexto(corpo.tipoPessoa());
      // Tipo ausente/inválido e documento em branco já têm as próprias mensagens.
      if (tipo == null || corpo.documento() == null || corpo.documento().isEmpty()) {
        return true;
      }
      if (tipo.aceita(corpo.documento())) {
        return true;
      }
      contexto.disableDefaultConstraintViolation();
      contexto
          .buildConstraintViolationWithTemplate(
              tipo == TipoPessoa.FISICA
                  ? "deve ser um CPF válido (11 dígitos) para pessoa física"
                  : "deve ser um CNPJ válido (14 dígitos) para pessoa jurídica")
          .addPropertyNode("documento")
          .addConstraintViolation();
      return false;
    }
  }
}
