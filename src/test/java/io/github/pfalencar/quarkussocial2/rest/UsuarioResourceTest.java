package io.github.pfalencar.quarkussocial2.rest;

import io.github.pfalencar.quarkussocial2.rest.dto.CreateUsuarioRequest;
import io.github.pfalencar.quarkussocial2.rest.dto.ResponseError;
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

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UsuarioResourceTest {
    @TestHTTPResource("/usuarios")
    URL apiURL;

    @Test
    @DisplayName("Deve criar um usuário com sucesso")
    @Order(1)
    public void createUsuarioTest(){
        var usuario = new CreateUsuarioRequest();
        usuario.setName("Fulano");
        usuario.setAge(30);

        //Esta é toda a especificação do request (da mesma forma que se faz no insomnia)
        var response =
                //dado este cenário
                given()
                    .contentType(ContentType.JSON)
                    .body(usuario)
                //execução
                .when()
                    .post(apiURL)
                //extraindo a resposta desta requisição
                .then()
                    .extract().response();

        //Assertivas:
        //Se o usuário é criado com sucesso, recebo o código de status 201 e o usuário persistido com id criado pelo bd:
        assertEquals(201, response.getStatusCode());
        assertNotNull(response.jsonPath().getString("id"));
    }

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

        assertEquals(ResponseError.UNPROCESSABLE_ENTITY_STATUS, response.getStatusCode());
        assertEquals("Validation Error", response.jsonPath().getString("message"));

        List<Map<String,String>> errors = response.jsonPath().getList("errors");
        assertNotNull(errors.get(0).get("message"));
        assertNotNull(errors.get(1).get("message"));
//        assertEquals("Age is required!",errors.get(0).get("message"));
//        assertEquals("Name is required!",errors.get(1).get("message"));
    }

    @Test
    @DisplayName("Deve listar todos os usuários")
    @Order(3)
    public void listAllUsuariosTest(){
            given()
                .contentType(ContentType.JSON)
            .when()
                .get(apiURL)
            .then()
                .statusCode(200)
                .body("size()", Matchers.is(1));
    }

}