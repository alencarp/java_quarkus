package io.github.pfalencar.quarkussocial2.rest.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class CreateUsuarioRequest {
    @NotBlank(message = "Name is required!")
    private String name;
    @NotNull(message = "Age is required!")
    private Integer age;

}
