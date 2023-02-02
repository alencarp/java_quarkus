package io.github.pfalencar.quarkussocial2.rest.dto;

import io.github.pfalencar.quarkussocial2.domain.model.Post;
import lombok.Data;

import java.time.LocalDateTime;
@Data
public class PostResponse {

    private String text;
    private LocalDateTime dataTime;

    public static PostResponse fromEntity(Post post) {
        PostResponse postResponse = new PostResponse();
        postResponse.setText(post.getText());
        postResponse.setDataTime(post.getDataTime());
        return postResponse;
    }

}
