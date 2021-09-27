package br.com.zup.academy.erombi.controller.request.annotation

import br.com.zup.academy.erombi.controller.request.NovaKeyRequest
import io.micronaut.core.annotation.AnnotationValue
import io.micronaut.validation.validator.constraints.ConstraintValidator
import io.micronaut.validation.validator.constraints.ConstraintValidatorContext
import jakarta.inject.Singleton
import javax.validation.Constraint
import kotlin.reflect.KClass

@MustBeDocumented
@Target(AnnotationTarget.TYPE, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [ValidaKeyValidator::class])
annotation class ValidaKey (
    val message: String = "Key inválida !",
    val groups: Array<KClass<Any>> = [],
    val payload: Array<KClass<Any>> = [])

@Singleton
class ValidaKeyValidator()
    : ConstraintValidator<ValidaKey, NovaKeyRequest> {

    override fun isValid(
        value: NovaKeyRequest?,
        annotationMetadata: AnnotationValue<ValidaKey>,
        context: ConstraintValidatorContext): Boolean {

        return value?.key?.let { key ->
            value.tipoKey?.validaKey(key)
        } ?: true

    }

}