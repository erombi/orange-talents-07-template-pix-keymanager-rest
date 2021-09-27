package br.com.zup.academy.erombi.controller.request

import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.NotBlank

@Introspected
data class RemoveKeyRestRequest(
    @NotBlank
    val idKey: String?,

    @NotBlank
    val idCliente: String?
)
