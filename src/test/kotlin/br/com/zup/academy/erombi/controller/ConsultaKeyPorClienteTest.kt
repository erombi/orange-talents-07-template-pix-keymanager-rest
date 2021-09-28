package br.com.zup.academy.erombi.controller

import br.com.zup.academy.erombi.*
import com.google.protobuf.Timestamp
import io.grpc.Status
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import java.util.*

@MicronautTest
internal class ConsultaKeyPorClienteTest {

    @Inject
    lateinit var grpcClient : KeyManagerGrpcServiceGrpc.KeyManagerGrpcServiceBlockingStub

    @Inject
    @field:Client("/")
    lateinit var httpClient: HttpClient

    @Test
    fun `deve consultar Key por Cliente com sucesso`() {
        val idCliente = UUID.randomUUID()

        val grpcRequest = ConsultaKeyPorClienteRequest.newBuilder()
            .setIdCliente(idCliente.toString())
            .build()

        val grpcKeys = mutableListOf(
            ConsultaKeyPorClienteResponse.KeyDescription.newBuilder()
                .setPixId(UUID.randomUUID().toString())
                .setClienteId(idCliente.toString())
                .setTipoKey(TipoKey.RANDOM)
                .setTipoConta(TipoConta.CACC)
                .setCriadoEm(Timestamp.newBuilder().build())
                .build()
        )

        Mockito.`when`(
            grpcClient.consultaKeysPorCliente(grpcRequest)
        ).thenReturn(
            ConsultaKeyPorClienteResponse.newBuilder()
                .addAllKeys(grpcKeys)
                .build()
        )

        val httpRequest = HttpRequest.GET<Any>("keys/porCliente?idCliente=$idCliente")

        val httpResponse = httpClient.toBlocking().exchange(httpRequest, Any::class.java)

        assertEquals(HttpStatus.OK, httpResponse.status)
    }

    @Test
    fun `deve retornar INVALID_ARGUMENT quando argumento invalido`() {
        val idCliente = UUID.randomUUID()

        val grpcRequest = ConsultaKeyPorClienteRequest.newBuilder()
            .setIdCliente(idCliente.toString())
            .build()

        Mockito.`when`(
            grpcClient.consultaKeysPorCliente(grpcRequest)
        ).thenThrow(Status.INVALID_ARGUMENT.asRuntimeException())

        val httpRequest = HttpRequest.GET<Any>("keys/porCliente?idCliente=$idCliente")

        val error = org.junit.jupiter.api.assertThrows<HttpClientResponseException> {
            httpClient.toBlocking().exchange(httpRequest, Any::class.java)
        }

        with(error) {
            assertEquals(HttpStatus.BAD_REQUEST, error.status)
        }
    }

    @Test
    fun `deve retornar SERVER_ERROR quando argumento invalido`() {
        val idCliente = UUID.randomUUID()

        val grpcRequest = ConsultaKeyPorClienteRequest.newBuilder()
            .setIdCliente(idCliente.toString())
            .build()

        Mockito.`when`(
            grpcClient.consultaKeysPorCliente(grpcRequest)
        ).thenThrow(Status.INTERNAL.asRuntimeException())

        val httpRequest = HttpRequest.GET<Any>("keys/porCliente?idCliente=$idCliente")

        val error = org.junit.jupiter.api.assertThrows<HttpClientResponseException> {
            httpClient.toBlocking().exchange(httpRequest, Any::class.java)
        }

        with(error) {
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, error.status)
        }
    }
}