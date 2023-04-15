package io.github.pfalencar.quarkussocial2.rest;

import io.github.pfalencar.quarkussocial2.rest.dto.request.CreateUsuarioRequest;
import io.github.pfalencar.quarkussocial2.rest.dto.response.ResponseError;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;

import java.net.URL;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest //sobe o contexto da aplicação do quarkus
@TestMethodOrder(MethodOrderer.OrderAnnotation.class) //para rodar os testes na ordem que eu quero. Uso para poder colocar o @Order()
class UsuarioResourceTest {
    @TestHTTPResource("/usuarios")
    URL apiURL;

    //caso de sucesso, quando faço uma requisição e o retorno é sucesso:
    @Test
    @DisplayName("Deve criar um usuário com sucesso")
    @Order(1)
    public void createUsuarioTest(){
        var usuario = new CreateUsuarioRequest();
        usuario.setName("Fulano");
        usuario.setAge(30);

        //Esta é toda a especificação do request (da mesma forma que se faz no postman/insomnia)
        //Dado que eu tenho esse conteúdo com esse corpo, quando eu fizer este post, então
        //vou extrair a resposta desta requisição
        var response =
                //given() = dado este cenário. Quando executo isso, então, isso aqui deve acontecer.
                given() //dado o conteúdo JSON, o corpo da requisição é a variável usuario
                    .contentType(ContentType.JSON)//transforma o usuario em JSON
                    .body(usuario)
                //when() = É a parte da execução
                .when() //Estou enviando o JSON para a apiURL
                 // .post("/usuarios") //post() recebe a URL para a qual eu vou mandar o POST
                    .post(apiURL)
                //extraindo a resposta desta requisição
                .then()
                    .extract().response();

        //Assertivas:
        //Se o usuário é criado com sucesso, recebo o código de status 201 e o usuário persistido com id criado pelo bd:
        assertEquals(201, response.getStatusCode()); //conforme a Response do método POST de usuarios (UsuarioResource)
        assertNotNull(response.jsonPath().getString("id")); //quero que não esteja nuloo id da resposta que vejo no postman/insomnia
    }

    //Caso de teste onde não passo os dados para criar o usuario e ele não passa na validação do método POST createUser (UsuarioResource)
    @Test
    @DisplayName("Deve retornar erro quando o JSON não é válido")
    @Order(2)
    public void createUsuarioValidationErrorTest(){
        var usuario = new CreateUsuarioRequest();
        usuario.setAge(null);
        usuario.setName(null);

        var response =
                given()
                    .contentType(ContentType.JSON)
                    .body(usuario)
                .when()
                    .post(apiURL)
                .then()
                    .extract().response();

        assertEquals(ResponseError.UNPROCESSABLE_ENTITY_STATUS, response.getStatusCode()); //espero que a resposta seja UNPROCESSABLE_ENTITY_STATUS = status 422
        assertEquals("Validation Error", response.jsonPath().getString("message")); //espero que a mensagem seja esta


        //Dentro do meu jsonPath() eu tenho a lista de errors na resposta do POST usuarios, quando não envio a idade e o nome pelo Postman
        //Por isso faço getList("errors").
        //Posso transformar esta lista num mapa de String com chave (field) e valor (message)

        List<Map<String,String>> errors = response.jsonPath().getList("errors");
        assertNotNull(errors.get(0).get("message"));  //estou vendo se não é nula a mensagem do primeiro objeto da lista de errors da resposta
        assertNotNull(errors.get(1).get("message"));

        //Pode ser que o retorno troque as posições, por isso, não é recomendável chumbar as mensagens de retorno.

//       assertEquals("Age is required!",errors.get(0).get("message")); //vendo se a mensagem específica do primeiro objeto está correta.
//       assertEquals("Name is required!",errors.get(1).get("message")); //vendo se a mensagem do segundo objeto está correta.
    }



    //NÃO posso rodar este teste isolado, pq eu não cadastrei nenhum usuário no banco para testar aqui. Vai dar erro!
    @Test
    @DisplayName("Deve listar todos os usuários")
    @Order(3)
    public void listAllUsuariosTest(){
            given()
                .contentType(ContentType.JSON) //veja que neste caso não tem corpo, porque é um GET
            .when()
                .get(apiURL)
            .then()//podemos verificar sem assertivas, desta forma mais simples, direto no then()
                .statusCode(200) //verificou se retornou o código de status 200
                .body("size()", Matchers.is(1)); //size() fico sabendo o tamanho do array.
        // Matchers.is(1) Quero que tenha pelo menos 1 usuario cadastrado. Se eu colocar 0 funciona, quando não tenho nenhum usuario.
    }

}