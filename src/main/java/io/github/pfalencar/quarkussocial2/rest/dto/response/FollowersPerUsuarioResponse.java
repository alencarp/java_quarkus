package io.github.pfalencar.quarkussocial2.rest.dto.response;

import lombok.Data;

import java.util.List;
@Data
public class FollowersPerUsuarioResponse {

    private Integer followersCount;
    private List<FollowerResponse> content;



}
