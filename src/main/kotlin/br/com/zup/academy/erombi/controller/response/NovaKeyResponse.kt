package br.com.zup.academy.erombi.controller.response

import io.micronaut.core.annotation.Introspected

@Introspected
data class NovaKeyResponse(
    val pixId: String
)