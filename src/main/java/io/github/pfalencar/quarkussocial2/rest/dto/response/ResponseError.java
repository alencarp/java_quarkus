package io.github.pfalencar.quarkussocial2.rest.dto.response;

import io.github.pfalencar.quarkussocial2.rest.dto.FieldError;
import lombok.Data;

import javax.validation.ConstraintViolation;
import javax.ws.rs.core.Response;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/*
Esta classe vai representar o objeto de retorno quando acontece o erro.
    vai retornar um JSON que tem a propriedade messagem => message
    e vai ter um array dentro do JSON com todos os erros, contendo o campo e a msg de erro => errors
 */

@Data
public class ResponseError {

    public static final int UNPROCESSABLE_ENTITY_STATUS = 422;
    private String message;
    private Collection<FieldError> errors;

    //esse <T> genérico vai criar um erro de resposta baseado nas violações, erros de constraint de qlqr objeto, por
    //colocamos esse <T>
    public static <T> ResponseError createFromValidation(Set<ConstraintViolation<T>> violations){
        //cv: vai representar um constraint violation
        //getPropertyPath(): qual foi a propriedade que ele tentou validar
        //com o map() estou mapeando um constraint violation para um FieldError
        List<FieldError> errors = violations
                .stream()
                .map(cv -> new FieldError(cv.getPropertyPath().toString(), cv.getMessage()))
                .collect(Collectors.toList());
        //meu objeto ResponseError está vindo de um erro de validação
        String message = "Validation Error";
        ResponseError responseError = new ResponseError(message, errors);

        return responseError;
    }

    public ResponseError(String message, Collection<FieldError> errors) {
        this.message = message;
        this.errors = errors;
    }

    public Response withStatusCode(int code) {
        return Response.status(code).entity(this).build();
    }
}
