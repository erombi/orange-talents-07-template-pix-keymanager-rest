package br.com.zup.academy.erombi.controller

import br.com.zup.academy.erombi.*
import br.com.zup.academy.erombi.client.GrpcClientFactory
import io.grpc.Status
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Replaces
import io.micronaut.context.annotation.Value
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientException
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import jakarta.inject.Singleton
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import java.util.*
import br.com.zup.academy.erombi.controller.request.NovaKeyRequest as requestModel
import br.com.zup.academy.erombi.controller.response.NovaKeyResponse as responseModel
import br.com.zup.academy.erombi.model.TipoConta as tipoContaModel
import br.com.zup.academy.erombi.model.TipoKey as tipoKeyModel

@MicronautTest
internal class KeyControllerTest {

    @Inject
    lateinit var grpcClient : KeyManagerGrpcServiceGrpc.KeyManagerGrpcServiceBlockingStub

    @field:Inject
    @field:Client("/")
    lateinit var httpClient : HttpClient

    @Factory
    @Replaces(factory = GrpcClientFactory::class)
    internal class MockitoFactory {
        @Singleton
        fun stubMock() = Mockito.mock(KeyManagerGrpcServiceGrpc.KeyManagerGrpcServiceBlockingStub::class.java)
    }

    @Test
    fun `deve cadastrar Key com sucesso`() {
        val idCliente = UUID.randomUUID()
        val pixId = UUID.randomUUID()
        val key = "48243048812"

        val grpcRequest = NovaKeyRequest.newBuilder()
            .setUuidCliente(idCliente.toString())
            .setKey(key)
            .setTipoKey(TipoKey.CPF)
            .setTipoConta(TipoConta.CACC)
            .build()

        `when`(
            grpcClient.cadastrarKey(grpcRequest)
        ).thenReturn(
            NovaKeyResponse.newBuilder()
                    .setPixId(pixId.toString())
                .build()
        )

        val httpRequest = HttpRequest.POST<requestModel>("keys",
            requestModel(idCliente, tipoKeyModel.CPF, key, tipoContaModel.CONTA_CORRENTE)
        )

        val response = httpClient.toBlocking().exchange(httpRequest, responseModel::class.java)

        Assertions.assertEquals(HttpStatus.CREATED, response.status())
        assertTrue(response.headers.contains("Location"))
        assertTrue(response.header("Location")!!.contains(response.body()!!.pixId))
    }

    @Test
    fun `deve retornar BAD_REQUEST quando conta invalida`() {
        val idCliente = UUID.randomUUID()
        val pixId = UUID.randomUUID()
        val key = "48243048812"

        val grpcRequest = NovaKeyRequest.newBuilder()
            .setUuidCliente(idCliente.toString())
            .setKey(key)
            .setTipoKey(TipoKey.CPF)
            .setTipoConta(TipoConta.CACC)
            .build()

        `when`(
            grpcClient.cadastrarKey(grpcRequest)
        ).thenThrow(
            Status.INVALID_ARGUMENT.withDescription("INVALID_ARGUMENT: Conta inválida").asRuntimeException()
        )

        val httpRequest = HttpRequest.POST<requestModel>("keys",
            requestModel(idCliente, tipoKeyModel.CPF, key, tipoContaModel.CONTA_CORRENTE)
        )

        val error = assertThrows<HttpClientResponseException> {
            httpClient.toBlocking().exchange(httpRequest, responseModel::class.java)
        }

        with(error) {
            assertEquals(HttpStatus.BAD_REQUEST, status)
        }
    }

    @Test
    fun `deve retornar UNPROCESSABLE_ENTITY quando key ja existir`() {
        val idCliente = UUID.randomUUID()
        val pixId = UUID.randomUUID()
        val key = "48243048812"

        val grpcRequest = NovaKeyRequest.newBuilder()
            .setUuidCliente(idCliente.toString())
            .setKey(key)
            .setTipoKey(TipoKey.CPF)
            .setTipoConta(TipoConta.CACC)
            .build()

        `when`(
            grpcClient.cadastrarKey(grpcRequest)
        ).thenReturn(
            NovaKeyResponse.newBuilder()
                .setPixId(pixId.toString())
                .build()
        )

        val httpRequest = HttpRequest.POST<requestModel>("keys",
            requestModel(idCliente, tipoKeyModel.CPF, key, tipoContaModel.CONTA_CORRENTE)
        )

        val response = httpClient.toBlocking().exchange(httpRequest, responseModel::class.java)

        `when`(
            grpcClient.cadastrarKey(grpcRequest)
        ).thenThrow(
            Status.FAILED_PRECONDITION.withDescription("FAILED_PRECONDITION: key já existente").asRuntimeException()
        )

        val error = assertThrows<HttpClientResponseException> {
            httpClient.toBlocking().exchange(httpRequest, responseModel::class.java)
        }

        with(error) {
            assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, status)
        }
    }

    @Test
    fun `deve retornar SERVICE_UNAVAILABLE quando servico fora do ar`() {
        val idCliente = UUID.randomUUID()
        val pixId = UUID.randomUUID()
        val key = "48243048812"

        val grpcRequest = NovaKeyRequest.newBuilder()
            .setUuidCliente(idCliente.toString())
            .setKey(key)
            .setTipoKey(TipoKey.CPF)
            .setTipoConta(TipoConta.CACC)
            .build()

        `when`(
            grpcClient.cadastrarKey(grpcRequest)
        ).thenThrow(
            Status.UNAVAILABLE.withDescription("Connection refused").asRuntimeException()
        )

        val httpRequest = HttpRequest.POST<requestModel>("keys",
            requestModel(idCliente, tipoKeyModel.CPF, key, tipoContaModel.CONTA_CORRENTE)
        )

        val error = assertThrows<HttpClientResponseException> {
            httpClient.toBlocking().exchange(httpRequest, responseModel::class.java)
        }

        with(error) {
            assertEquals(HttpStatus.SERVICE_UNAVAILABLE, status)
        }
    }

    @Test
    fun `deve retornar INTERNAL_SERVER_ERROR quando servico fora do ar`() {
        val idCliente = UUID.randomUUID()
        val pixId = UUID.randomUUID()
        val key = "48243048812"

        val grpcRequest = NovaKeyRequest.newBuilder()
            .setUuidCliente(idCliente.toString())
            .setKey(key)
            .setTipoKey(TipoKey.CPF)
            .setTipoConta(TipoConta.CACC)
            .build()

        `when`(
            grpcClient.cadastrarKey(grpcRequest)
        ).thenThrow(
            Status.UNAUTHENTICATED.asRuntimeException()
        )

        val httpRequest = HttpRequest.POST<requestModel>("keys",
            requestModel(idCliente, tipoKeyModel.CPF, key, tipoContaModel.CONTA_CORRENTE)
        )

        val error = assertThrows<HttpClientResponseException> {
            httpClient.toBlocking().exchange(httpRequest, responseModel::class.java)
        }

        with(error) {
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, status)
        }
    }

}