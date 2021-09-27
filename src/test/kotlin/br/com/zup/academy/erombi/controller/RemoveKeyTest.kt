package br.com.zup.academy.erombi.controller

import br.com.zup.academy.erombi.KeyManagerGrpcServiceGrpc
import br.com.zup.academy.erombi.RemoveKeyResponse
import br.com.zup.academy.erombi.controller.request.RemoveKeyRestRequest
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import java.util.*

@MicronautTest
internal class RemoveKeyTest {
    @Inject
    lateinit var grpcClient : KeyManagerGrpcServiceGrpc.KeyManagerGrpcServiceBlockingStub

    @field:Inject
    @field:Client("/")
    lateinit var httpClient : HttpClient

    @Test
    fun  `deve remover key com sucesso`() {
        val idCliente = UUID.randomUUID()
        val pixId = UUID.randomUUID()
        val key = "48243048812"

        val grpcRequest = br.com.zup.academy.erombi.RemoveKeyRequest.newBuilder()
                .setIdKey(pixId.toString())
                .setIdCliente(idCliente.toString())
            .build()

        Mockito.`when`(
            grpcClient.removeKey(grpcRequest)
        ).thenReturn(
            RemoveKeyResponse.getDefaultInstance()
        )

        val httpRequest = HttpRequest.DELETE("keys",
            RemoveKeyRestRequest(
                pixId.toString(),
                idCliente.toString()
            )
        )

        val response = httpClient.toBlocking().exchange(httpRequest, RemoveKeyRestRequest::class.java)

        assertEquals(HttpStatus.OK, response.status)
    }

    @Test
    fun  `deve lancar NOT_FOUND quando nao existir key ou cliente`() {
        val idCliente = UUID.randomUUID()
        val pixId = UUID.randomUUID()
        val key = "48243048812"

        val grpcRequest = br.com.zup.academy.erombi.RemoveKeyRequest.newBuilder()
            .setIdKey(pixId.toString())
            .setIdCliente(idCliente.toString())
            .build()

        Mockito.`when`(
            grpcClient.removeKey(grpcRequest)
        ).thenThrow(
            Status.NOT_FOUND.asRuntimeException()
        )

        val httpRequest = HttpRequest.DELETE("keys",
            RemoveKeyRestRequest(
                pixId.toString(),
                idCliente.toString()
            )
        )

        val error = assertThrows<HttpClientResponseException> {
            httpClient.toBlocking().exchange(httpRequest, RemoveKeyRestRequest::class.java)
        }

        assertEquals(HttpStatus.NOT_FOUND, error.status)
    }

    @Test
    fun  `deve lancar SERVICE_UNAVAILABLE quando servico fora do ar`() {
        val idCliente = UUID.randomUUID()
        val pixId = UUID.randomUUID()
        val key = "48243048812"

        val grpcRequest = br.com.zup.academy.erombi.RemoveKeyRequest.newBuilder()
            .setIdKey(pixId.toString())
            .setIdCliente(idCliente.toString())
            .build()

        Mockito.`when`(
            grpcClient.removeKey(grpcRequest)
        ).thenThrow(
            Status.UNAVAILABLE.asRuntimeException()
        )

        val httpRequest = HttpRequest.DELETE("keys",
            RemoveKeyRestRequest(
                pixId.toString(),
                idCliente.toString()
            )
        )

        val error = assertThrows<HttpClientResponseException> {
            httpClient.toBlocking().exchange(httpRequest, RemoveKeyRestRequest::class.java)
        }

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, error.status)
    }

    @Test
    fun  `deve lancar INTERNAL_SERVER_ERROR quando erro desconhecido`() {
        val idCliente = UUID.randomUUID()
        val pixId = UUID.randomUUID()
        val key = "48243048812"

        val grpcRequest = br.com.zup.academy.erombi.RemoveKeyRequest.newBuilder()
            .setIdKey(pixId.toString())
            .setIdCliente(idCliente.toString())
            .build()

        Mockito.`when`(
            grpcClient.removeKey(grpcRequest)
        ).thenThrow(
            Status.INTERNAL.asRuntimeException()
        )

        val httpRequest = HttpRequest.DELETE("keys",
            RemoveKeyRestRequest(
                pixId.toString(),
                idCliente.toString()
            )
        )

        val error = assertThrows<HttpClientResponseException> {
            httpClient.toBlocking().exchange(httpRequest, RemoveKeyRestRequest::class.java)
        }

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, error.status)
    }
}