package io.github.pfalencar.quarkussocial2.rest;

import io.github.pfalencar.quarkussocial2.domain.model.Follower;
import io.github.pfalencar.quarkussocial2.domain.model.Post;
import io.github.pfalencar.quarkussocial2.domain.model.Usuario;
import io.github.pfalencar.quarkussocial2.domain.repository.FollowerRepository;
import io.github.pfalencar.quarkussocial2.domain.repository.PostRepository;
import io.github.pfalencar.quarkussocial2.domain.repository.UsuarioRepository;
import io.github.pfalencar.quarkussocial2.rest.dto.request.CreatePostRequest;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.transaction.Transactional;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestHTTPEndpoint(PostResource.class) //com isso o quarkus já vai identificar a URL que quero fazer as requisições.
    //porque a classe PostResource já tem o Path em cima da classe. Então, todas as requisições que eu fizer serão para
    //essa URL que foi definida aqui nessa annotation
class PostResourceTest {

    @Inject
    UsuarioRepository usuarioRepository;
    @Inject
    FollowerRepository followerRepository;

    @Inject
    PostRepository postRepository;

    Long usuarioID;
    Long usuarioNaoSeguidorID;
    Long usuarioSeguidorID;

    @BeforeEach  //este método será executado antes de cada um dos métodos que eu colocar nesta classe
    @Transactional
    public void setUP(){
        //usuario padrão dos testes
        Usuario usuario = new Usuario();
        usuario.setAge(30);
        usuario.setName("Fulano");
        usuarioRepository.persist(usuario);
        usuarioID = usuario.getId();

        //usuário que não segue ninguém
        Usuario usuarioNaoSeguidor = new Usuario();
        usuarioNaoSeguidor.setAge(33);
        usuarioNaoSeguidor.setName("Cicrano");
        usuarioRepository.persist(usuarioNaoSeguidor);
        usuarioNaoSeguidorID = usuarioNaoSeguidor.getId();

        //usuario seguidor
        Usuario usuarioSeguidor = new Usuario();
        usuarioSeguidor.setAge(40);
        usuarioSeguidor.setName("Beltrano");
        usuarioRepository.persist(usuarioSeguidor);
        usuarioSeguidorID = usuarioSeguidor.getId();

        Follower follower = new Follower();
        follower.setUsuario(usuario); //"usuario" é o usuário que vai ser seguido
        follower.setFollower(usuarioSeguidor); //"usuarioSeguidor" é o usuário que vai seguir
        followerRepository.persist(follower);

        //Criando uma postagem para o usuario
        Post post = new Post();
        post.setText("Hello");
        post.setUsuario(usuario);
        postRepository.persist(post);
    }


    //Cenário de Teste 1 - POST - Cria uma postagem para um usuário
    //ao rodar este teste pela primeira vez, ele vai dar erro, pq espera o status 201, mas recebe o 404, isto porque
    //não existe o usuarioId criado, portanto precisa inserir este usuário antes de tentar fazer um post pra ele.
    //Para isso, criei o método setUP()
    @Test
    @DisplayName("Should create a post for an user")
    public void createPostTest() {
        CreatePostRequest createPostRequest = new CreatePostRequest();
        createPostRequest.setText("Some text");

        //preciso passar o ID do usuário que fica na URL da requisição.


        //dado que vou fazer uma requisição com um JSON, vou enviar o "Some text" no corpo da requisição
        given()
                .contentType(ContentType.JSON)
                .body(createPostRequest)  //dto de request para o post
                .pathParam("usuarioId", usuarioID) //nome do parâmetro na classe PostResource, id que criei neste método.
        .when()
                .post() //Este é o método REST. Não preciso passar a URL, pq já está no @TestHTTPEndpoint
        .then()
                .statusCode(201); //então, se estou passando o objeto corretamente, recebo "Created"
    }

    //Cenário de Teste 2 - POST - Tentando fazer uma postagem para um usuário inexistente
    //Quando ele não encontra o usuário do qual ele vai fazer a postagem
    //Vamos precisar passar um Id de um usuário que não existe na base
    @Test
    @DisplayName("should return 404 when trying to make a post for an inexistent user")
    public void postagemParaUmUsuarioInexistenteTeste(){
        CreatePostRequest createPostRequest = new CreatePostRequest();
        createPostRequest.setText("Some text");

        Long inexistentUsuarioId = 999L;

        given()
                .contentType(ContentType.JSON)
                .body(createPostRequest)
                .pathParam("usuarioId", inexistentUsuarioId)
        .when()
                .post() //qdo chamar esta url, que está no @TestHTTPEndpoint
        .then()
                .statusCode(404); //Se estou passando um usuário inexistente, recebo 404
    }

    //Cenário de Teste 1 - GET - Quando o id de usuario não é encontrado
    @Test
    @DisplayName("Should return 404 when user doesn't exist")
    public void listPostUsuarioIsNotFoundTest() {
        Long InexistentUserID = 999L;

        given()
                .pathParam("usuarioId",InexistentUserID)
        .when()
                .get() //Método GET. Não preciso passar a URL, por causa da annotation @TestHTTPEndpoint
        .then()
                .statusCode(404);


    }

    //Cenário de Teste 2 - GET - Quando o id do seguidor não foi enviado no header
    @Test
    @DisplayName("Should return 400 when followerId Header wasn't sent")
    public void listPostFollowerHeaderIsNotSentTest() {
        given()
            .pathParam("usuarioId",usuarioID) //tenho que passar um usuário existente aqui
               //não vou passar o header que eu deveria passar, para dar o erro que eu preciso aqui.
        .when()
            .get()
        .then()
            .statusCode(400) //como retorno recebo 400 e a msg abaixo.
            .body(Matchers.is("You forgot sending followerId in Header")); //verifico se está igual a msg da classe PostResource.

    }

    //Cenário de Teste 3 - GET - Quando manda o seguidor no header, mas não existe esse seguidor
    @Test
    @DisplayName("Should return 400 when followerId doesn't exist")
    public void listPostFollowerIdIsNotFoundTest() {
        Long inexistentFollowerID = 999L;
        given()
            .pathParam("usuarioId",usuarioID) //tenho que passar um usuário existente aqui
            .header("followerId", inexistentFollowerID)
       .when()
            .get()
       .then()
            .statusCode(400)
            .body(Matchers.is("followerId doesn't exist!"));
    }

    //Cenário de Teste 4 - GET - Quando é proibido, um não segue o outro.
    @Test
    @DisplayName("Should return 403 when follower doesn't follow the user")
    public void listPostIsNotAFollowerTest() {
        given()
           .pathParam("usuarioId", usuarioID)
           .header("followerId", usuarioNaoSeguidorID)
        .when()
           .get()
        .then()
            .statusCode(403)
            .body(Matchers.is("You can't see these posts"));
    }

    //Cenário de Teste 5 - GET - Quando deu sucesso. Ele retorna as postagens do usuário.
    @Test
    @DisplayName("Should return posts")
    public void listPostTest() {
        given()
                .pathParam("usuarioId", usuarioID)
                .header("followerId", usuarioSeguidorID)
        .when()
                .get()
        .then()
                .statusCode(200)
                .body("size()", Matchers.is(1)); //um array contendo as postagens do usuarioID, recebo 1 postagem (pq criei só uma)

    }
}