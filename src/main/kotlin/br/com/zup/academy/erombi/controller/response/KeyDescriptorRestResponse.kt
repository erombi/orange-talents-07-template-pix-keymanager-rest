package br.com.zup.academy.erombi.controller.response

import io.micronaut.core.annotation.Introspected
import java.time.LocalDateTime

@Introspected
data class KeyDescriptorRestResponse(
    val pixId: String,
    val clienteId: String,
    val tipoKey: String,
    val tipoConta:String,
    val criadoEm: LocalDateTime
)