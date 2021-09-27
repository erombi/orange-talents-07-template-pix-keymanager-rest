package br.com.zup.academy.erombi.controller

import br.com.zup.academy.erombi.KeyManagerGrpcServiceGrpc
import br.com.zup.academy.erombi.RemoveKeyRequest
import br.com.zup.academy.erombi.controller.request.NovaKeyRequest
import br.com.zup.academy.erombi.controller.request.RemoveKeyRestRequest
import br.com.zup.academy.erombi.controller.response.NovaKeyResponse
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Delete
import io.micronaut.http.annotation.Post
import io.micronaut.validation.Validated
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
            val response = grpcClient.removeKey(
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

    private fun capturaDescription(e: StatusRuntimeException) =
        e.status.description?.split(":")?.get(1) ?: "INVALID_ARGUMENT"
}