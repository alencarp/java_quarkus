package io.github.pfalencar.quarkussocial2.rest.dto;
/*
Este objeto representa o JSON de retorno.
    Field é o campo que deu erro.
    Message é a mensagem associada ao campo que deu erro.
 */
public class FieldError {
    private String field;
    private String message;

    public FieldError(String field, String message) {
        this.field = field;
        this.message = message;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
