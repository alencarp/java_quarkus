package io.github.pfalencar.quarkussocial2.rest;

import io.github.pfalencar.quarkussocial2.domain.model.Post;
import io.github.pfalencar.quarkussocial2.domain.model.Usuario;
import io.github.pfalencar.quarkussocial2.domain.repository.FollowerRepository;
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
import java.util.List;
import java.util.stream.Collectors;

@Path("/usuarios/{usuarioId}/posts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PostResource {
    @Inject
    private UsuarioRepository usuarioRepository;
    private PostRepository postRepository;
    private FollowerRepository followerRepository;

    public PostResource(UsuarioRepository usuarioRepository, PostRepository postRepository, FollowerRepository followerRepository) {
        this.usuarioRepository = usuarioRepository;
        this.postRepository = postRepository;
        this.followerRepository = followerRepository;

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
    public Response listPosts(@PathParam("usuarioId") Long usuarioId, @HeaderParam("followerId") Long followerId){
        Usuario usuario = usuarioRepository.findById(usuarioId);
        if (usuario == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        if(followerId == null) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity("You forgot sending followerId in Header")
                    .build();
        }

        //um usuário só pode ver a lista de posts de um certo usuário, se aquele for seguidor deste:
        Usuario usuarioSeguidor = usuarioRepository.findById(followerId);

        if (usuarioSeguidor == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("followerId doesn't exist!").build();
        }

        boolean isFollower = followerRepository.follows(usuarioSeguidor, usuario);
        if (!isFollower) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("You can't see these posts").build(); //403
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
