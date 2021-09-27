package br.com.zup.academy.erombi.controller.request

import br.com.zup.academy.erombi.model.TipoConta
import br.com.zup.academy.erombi.model.TipoKey
import br.com.zup.academy.erombi.controller.request.annotation.ValidaKey
import io.micronaut.core.annotation.Introspected
import java.util.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull


@Introspected
@ValidaKey
data class NovaKeyRequest(

    @field:NotBlank
    val uuidCliente: UUID?,

    @field:NotBlank
    val tipoKey: TipoKey?,


    val key: String?,

    @field:NotNull
    val tipoConta: TipoConta?,
)
