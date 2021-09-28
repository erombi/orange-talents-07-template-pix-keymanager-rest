package br.com.zup.academy.erombi.controller.response

import io.micronaut.core.annotation.Introspected
import java.time.LocalDateTime

@Introspected
data class ConsultaKeyRestResponse(
    val clienteId: String,
    val pixId: String,
    val tipoKey: String,
    val key: String,
    val conta: ContaRestResponse,
    val criadaEm: LocalDateTime
)