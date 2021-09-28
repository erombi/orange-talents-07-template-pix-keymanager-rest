package br.com.zup.academy.erombi.controller

import br.com.zup.academy.erombi.ConsultaKeyPorClienteRequest
import br.com.zup.academy.erombi.ConsultaKeyRequest
import br.com.zup.academy.erombi.KeyManagerGrpcServiceGrpc
import br.com.zup.academy.erombi.RemoveKeyRequest
import br.com.zup.academy.erombi.controller.request.NovaKeyRequest
import br.com.zup.academy.erombi.controller.request.RemoveKeyRestRequest
import br.com.zup.academy.erombi.controller.response.ConsultaKeyRestResponse
import br.com.zup.academy.erombi.controller.response.ContaRestResponse
import br.com.zup.academy.erombi.controller.response.KeyDescriptorRestResponse
import br.com.zup.academy.erombi.controller.response.NovaKeyResponse
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.*
import io.micronaut.validation.Validated
import java.time.LocalDateTime
import java.time.ZoneOffset
import javax.validation.Valid

@Validated
@Controller("/keys")
class KeyController(
    val grpcClient: KeyManagerGrpcServiceGrpc.KeyManagerGrpcServiceBlockingStub
) {

    @Post
    fun cadastrarKey(@Body @Valid request : NovaKeyRequest): HttpResponse<Any> {
        return try {
            val response = grpcClient.cadastrarKey(
                br.com.zup.academy.erombi.NovaKeyRequest.newBuilder()
                    .setUuidCliente(request.uuidCliente.toString())
                    .setTipoKey(request.tipoKey?.toStub())
                    .setKey(request.key)
                    .setTipoConta(request.tipoConta?.toStub())
                    .build()
            )

            HttpResponse.created(
                NovaKeyResponse(response.pixId),
                HttpResponse.uri("/keys/${response.pixId}")
            )
        } catch (e: StatusRuntimeException) {
            when(e.status.code) {
                Status.Code.INVALID_ARGUMENT -> {
                    HttpResponse.badRequest(hashMapOf(Pair("error", capturaDescription(e))))
                }
                Status.Code.FAILED_PRECONDITION -> {
                    HttpResponse.status<Any?>(HttpStatus.UNPROCESSABLE_ENTITY).body(hashMapOf(Pair("error", capturaDescription(e))))
                }
                Status.Code.UNAVAILABLE -> {
                    HttpResponse.status(HttpStatus.SERVICE_UNAVAILABLE)
                }

                else -> HttpResponse.serverError(hashMapOf(Pair("error", capturaDescription(e))))
            }
        }
    }

    @Delete
    fun removeKey(@Body @Valid request: RemoveKeyRestRequest): HttpResponse<Any> {
        return try {
            grpcClient.removeKey(
                RemoveKeyRequest.newBuilder()
                    .setIdKey(request.idKey)
                    .setIdCliente(request.idCliente)
                    .build()
            )

            HttpResponse.ok()
        } catch (e: StatusRuntimeException) {
            when(e.status.code) {
                Status.Code.NOT_FOUND -> {
                    HttpResponse.notFound()
                }
                Status.Code.UNAVAILABLE -> {
                    HttpResponse.status(HttpStatus.SERVICE_UNAVAILABLE)
                }


                else -> HttpResponse.serverError()
            }
        }
    }

    @Get
    fun consultaKey(@QueryValue idKey:String, @QueryValue idCliente: String): HttpResponse<Any> {
        return try {
            val response = grpcClient.consultaKey(
                ConsultaKeyRequest.newBuilder()
                        .setPix(
                            ConsultaKeyRequest.FiltroPorPix.newBuilder()
                                    .setIdKey(idKey)
                                    .setIdCliente(idCliente)
                                .build()
                        )
                    .build()
            )

            HttpResponse.ok(
                ConsultaKeyRestResponse(
                    response.clienteId,
                    response.pixId,
                    response.chave.tipoKey.name,
                    response.chave.key,
                    ContaRestResponse(
                        response.chave.conta.tipo.name,
                        response.chave.conta.instituicao,
                        response.chave.conta.nomeTitular,
                        response.chave.conta.cpfTitular,
                        response.chave.conta.agencia,
                        response.chave.conta.numeroConta
                    ),
                    LocalDateTime.ofEpochSecond(response.chave.criadaEm.seconds, response.chave.criadaEm.nanos, ZoneOffset.UTC)
                )
            )
        } catch (e: StatusRuntimeException) {
            when(e.status.code) {
                Status.Code.NOT_FOUND -> HttpResponse.notFound()
                Status.Code.INVALID_ARGUMENT -> HttpResponse.badRequest()

                else -> HttpResponse.serverError()
            }
        }
    }

    @Get("/porCliente")
    fun consultaKeyPorCliente(@QueryValue idCliente: String): HttpResponse<Any> {
        return try {
            val response = grpcClient.consultaKeysPorCliente(
                ConsultaKeyPorClienteRequest.newBuilder()
                    .setIdCliente(idCliente)
                    .build()
            )

            val keys = response.keysList.map {
                KeyDescriptorRestResponse(
                    it.pixId,
                    it.clienteId,
                    it.tipoKey.name,
                    it.tipoConta.name,
                    LocalDateTime.ofEpochSecond(it.criadoEm.seconds, it.criadoEm.nanos, ZoneOffset.UTC)
                )
            }

            return HttpResponse.ok(hashMapOf("keys" to keys))
        } catch (e: StatusRuntimeException) {
            when(e.status.code) {
                Status.Code.INVALID_ARGUMENT -> HttpResponse.badRequest()

                else -> HttpResponse.serverError()
            }
        }
    }

    private fun capturaDescription(e: StatusRuntimeException) =
        e.status.description?.split(":")?.get(1) ?: "INVALID_ARGUMENT"
}