package io.github.pfalencar.quarkussocial2.rest.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CreatePostRequest {
    @NotBlank(message = "Text is required")
    private String text;
}
