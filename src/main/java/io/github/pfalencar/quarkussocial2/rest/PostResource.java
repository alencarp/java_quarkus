package io.github.pfalencar.quarkussocial2.rest;

import io.github.pfalencar.quarkussocial2.domain.model.Post;
import io.github.pfalencar.quarkussocial2.domain.model.Usuario;
import io.github.pfalencar.quarkussocial2.domain.repository.PostRepository;
import io.github.pfalencar.quarkussocial2.domain.repository.UsuarioRepository;
import io.github.pfalencar.quarkussocial2.rest.dto.CreatePostRequest;
import io.github.pfalencar.quarkussocial2.rest.dto.PostResponse;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Sort;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Path("/usuarios/{usuarioId}/posts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PostResource {
    @Inject
    private UsuarioRepository usuarioRepository;
    private PostRepository postRepository;
    public PostResource(UsuarioRepository usuarioRepository, PostRepository postRepository) {
        this.usuarioRepository = usuarioRepository;
        this.postRepository = postRepository;
    }
    @POST
    @Transactional
    public Response savePost(@PathParam("usuarioId") Long usuarioId, CreatePostRequest createPostRequest){
        /* procuro o usuário com o id informado na url
        E "ligado" ao usuario gravo uma postagem.
        Posso criar um objeto Post a partir desse objeto createPostRequest
         */
        Usuario usuario = usuarioRepository.findById(usuarioId);
        if (usuario == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        Post post = new Post();
        post.setText(createPostRequest.getText());
        post.setUsuario(usuario);

        //usar somente se não tiver o método prePersist() na classe Post
       // post.setDataTime(LocalDateTime.now());

        postRepository.persist(post);

        return Response.status(Response.Status.CREATED).build();
    }
    @GET
    public Response listPosts(@PathParam("usuarioId") Long usuarioId){
        Usuario usuario = usuarioRepository.findById(usuarioId);
        if (usuario == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        PanacheQuery<Post> postPanacheQuery = postRepository.find(
                "usuario", Sort.by("dataTime", Sort.Direction.Descending),usuario);
        List<Post> listaDePosts = postPanacheQuery.list();

        var postResponseList = listaDePosts.stream()
                .map(post -> PostResponse.fromEntity(post))  //usa esta linha ou a linha .map(PostResponse::fromEntity)
               // .map(PostResponse::fromEntity)
                .collect(Collectors.toList());

        return Response.ok(postResponseList).build();
    }

}
