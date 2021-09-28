package br.com.zup.academy.erombi.controller.response

import br.com.zup.academy.erombi.model.TipoConta
import io.micronaut.core.annotation.Introspected

@Introspected
data class ContaRestResponse(
    val tipoConta: String,
    val instituicao: String,
    val nomeTitular: String,
    val cpfTitular: String,
    val agencia: String,
    val numeroConta: String
)
