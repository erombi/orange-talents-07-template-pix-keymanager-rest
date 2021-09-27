package br.com.zup.academy.erombi.model

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.*

internal class TipoKeyTest{

    @Test
    fun `deve validar Key CPF com sucesso`() {
        val valido = TipoKey.CPF.validaKey("48243048812")

        assertTrue(valido)
    }

    @Test
    fun `deve invalidar Key CPF`() {
        val valido = TipoKey.CPF.validaKey("4824805412")

        assertFalse(valido)
    }

    @Test
    fun `deve validar Key CNPJ`() {
        val valido = TipoKey.CNPJ.validaKey("01.000.111/0001-00")

        assertTrue(valido)
    }

    @Test
    fun `deve invalidar Key CNPJ`() {
        val valido = TipoKey.CNPJ.validaKey("01.000.111/0001")

        assertFalse(valido)
    }

    @Test
    fun `deve validar Key PHONE`() {
        val valido = TipoKey.PHONE.validaKey("+5519999741000")

        assertTrue(valido)
    }

    @Test
    fun `deve invalidar Key PHONE`() {
        val valido = TipoKey.PHONE.validaKey("5519999741000")

        assertFalse(valido)
    }

    @Test
    fun `deve validar Key EMAIL`() {
        val valido = TipoKey.EMAIL.validaKey("eduardo@zup.com.br")

        assertTrue(valido)
    }

    @Test
    fun `deve invalidar Key EMAIL sem @`() {
        val valido = TipoKey.EMAIL.validaKey("eduardozup.com.br")

        assertFalse(valido)
    }

    @Test
    fun `deve invalidar Key EMAIL sem sufixo`() {
        val valido = TipoKey.EMAIL.validaKey("eduardo@")

        assertFalse(valido)
    }

    @Test
    fun `deve invalidar Key EMAIL sem prefixo`() {
        val valido = TipoKey.EMAIL.validaKey("@zup.com.br")

        assertFalse(valido)
    }

    @Test
    fun `deve validar Key RANDOM`() {
        val valido = TipoKey.RANDOM.validaKey("")

        assertTrue(valido)
    }

    @Test
    fun `deve invalidar Key RANDOM`() {
        val valido = TipoKey.RANDOM.validaKey(UUID.randomUUID().toString())

        assertFalse(valido)
    }

    @Test
    fun `deve retornar RANDOM do stub`() {
        val stub = TipoKey.RANDOM.toStub()

        assertTrue(stub == br.com.zup.academy.erombi.TipoKey.RANDOM)
    }

    @Test
    fun `deve retornar CPF do stub`() {
        val stub = TipoKey.CPF.toStub()

        assertTrue(stub == br.com.zup.academy.erombi.TipoKey.CPF)
    }

    @Test
    fun `deve retornar CNPJ do stub`() {
        val stub = TipoKey.CNPJ.toStub()

        assertTrue(stub == br.com.zup.academy.erombi.TipoKey.CNPJ)
    }

    @Test
    fun `deve retornar PHONE do stub`() {
        val stub = TipoKey.PHONE.toStub()

        assertTrue(stub == br.com.zup.academy.erombi.TipoKey.PHONE)
    }

    @Test
    fun `deve retornar EMAIL do stub`() {
        val stub = TipoKey.EMAIL.toStub()

        assertTrue(stub == br.com.zup.academy.erombi.TipoKey.EMAIL)
    }

}