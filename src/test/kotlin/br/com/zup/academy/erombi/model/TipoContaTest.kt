package br.com.zup.academy.erombi.model

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class TipoContaTest {

    @Test
    fun `deve retornar CACC do stub`() {
        val stub = TipoConta.CONTA_CORRENTE.toStub()

        assertTrue(stub == br.com.zup.academy.erombi.TipoConta.CACC)
    }

    @Test
    fun `deve retornar SVGS do stub`() {
        val stub = TipoConta.CONTA_POUPANCA.toStub()

        assertTrue(stub == br.com.zup.academy.erombi.TipoConta.SVGS)
    }

}