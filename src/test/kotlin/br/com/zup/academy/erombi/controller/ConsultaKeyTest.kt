package br.com.zup.academy.erombi.controller

import br.com.zup.academy.erombi.*
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import java.util.*

@MicronautTest
internal class ConsultaKeyTest {

    @Inject
    lateinit var grpcClient : KeyManagerGrpcServiceGrpc.KeyManagerGrpcServiceBlockingStub

    @Inject
    @field:Client("/")
    lateinit var httpClient: HttpClient

    @Test
    fun `deve consultar Key com sucesso`() {
        val idCliente = UUID.randomUUID()
        val idKey = UUID.randomUUID()

        val grpcRequest = ConsultaKeyRequest.newBuilder()
            .setPix(
                ConsultaKeyRequest.FiltroPorPix.newBuilder()
                        .setIdKey(idKey.toString())
                        .setIdCliente(idCliente.toString())
                    .build()
            )
            .build()

        Mockito.`when`(
            grpcClient.consultaKey(grpcRequest)
        ).thenReturn(
            ConsultaKeyResponse.newBuilder().build()
        )

        val httpRequest = HttpRequest.GET<Any>("keys?idKey=$idKey&idCliente=$idCliente")

        val httpResponse = httpClient.toBlocking().exchange(httpRequest, Any::class.java)

        Assertions.assertEquals(HttpStatus.OK, httpResponse.status)
    }

    @Test
    fun `deve retornar NOT_FOUND quando nao existir key ou cliente`() {
        val idCliente = UUID.randomUUID()
        val idKey = UUID.randomUUID()

        val grpcRequest = ConsultaKeyRequest.newBuilder()
            .setPix(
                ConsultaKeyRequest.FiltroPorPix.newBuilder()
                    .setIdKey(idKey.toString())
                    .setIdCliente(idCliente.toString())
                    .build()
            )
            .build()

        Mockito.`when`(
            grpcClient.consultaKey(grpcRequest)
        ).thenThrow(Status.NOT_FOUND.asRuntimeException())

        val httpRequest = HttpRequest.GET<Any>("keys?idKey=$idKey&idCliente=$idCliente")

        val error = assertThrows<HttpClientResponseException> {
            httpClient.toBlocking().exchange(httpRequest, Any::class.java)
        }

        with(error) {
            Assertions.assertEquals(HttpStatus.NOT_FOUND, error.status)
        }
    }

    @Test
    fun `deve retornar INVALID_ARGUMENT quando existir erro de formulario`() {
        val idCliente = UUID.randomUUID()
        val idKey = UUID.randomUUID()

        val grpcRequest = ConsultaKeyRequest.newBuilder()
            .setPix(
                ConsultaKeyRequest.FiltroPorPix.newBuilder()
                    .setIdKey(idKey.toString())
                    .setIdCliente(idCliente.toString())
                    .build()
            )
            .build()

        Mockito.`when`(
            grpcClient.consultaKey(grpcRequest)
        ).thenThrow(Status.INVALID_ARGUMENT.asRuntimeException())

        val httpRequest = HttpRequest.GET<Any>("keys?idKey=$idKey&idCliente=$idCliente")

        val error = assertThrows<HttpClientResponseException> {
            httpClient.toBlocking().exchange(httpRequest, Any::class.java)
        }

        with(error) {
            Assertions.assertEquals(HttpStatus.BAD_REQUEST, error.status)
        }
    }

    @Test
    fun `deve retornar INTERNAL_SERVER_ERROR quando erro inesperado`() {
        val idCliente = UUID.randomUUID()
        val idKey = UUID.randomUUID()

        val grpcRequest = ConsultaKeyRequest.newBuilder()
            .setPix(
                ConsultaKeyRequest.FiltroPorPix.newBuilder()
                    .setIdKey(idKey.toString())
                    .setIdCliente(idCliente.toString())
                    .build()
            )
            .build()

        Mockito.`when`(
            grpcClient.consultaKey(grpcRequest)
        ).thenThrow(Status.UNAVAILABLE.asRuntimeException())

        val httpRequest = HttpRequest.GET<Any>("keys?idKey=$idKey&idCliente=$idCliente")

        val error = assertThrows<HttpClientResponseException> {
            httpClient.toBlocking().exchange(httpRequest, Any::class.java)
        }

        with(error) {
            Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, error.status)
        }
    }
}