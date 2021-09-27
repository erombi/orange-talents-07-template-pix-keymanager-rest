package br.com.zup.academy.erombi.controller.request.annotation

import br.com.zup.academy.erombi.controller.request.NovaKeyRequest
import br.com.zup.academy.erombi.model.TipoConta
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import io.micronaut.validation.validator.Validator
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.util.*

@MicronautTest
internal class ValidaKeyValidatorTest(
    val validator: Validator
) {

    @Test
    fun `deve retornar True quando key for nulo`() {
        val idCLiente = UUID.randomUUID()

        val validations = validator.validate(
            NovaKeyRequest(
                idCLiente,
                null,
                "",
                TipoConta.CONTA_CORRENTE
            )
        )

        assertTrue(validations.isEmpty())
    }
}