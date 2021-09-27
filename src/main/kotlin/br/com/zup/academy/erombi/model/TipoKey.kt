package br.com.zup.academy.erombi.model

import br.com.zup.academy.erombi.TipoKey
import java.util.regex.Pattern

enum class TipoKey {
    RANDOM {
        override fun validaKey(key: String): Boolean {
            return key.isEmpty()
        }

        override fun toStub(): TipoKey {
            return TipoKey.RANDOM
        }
    }, CPF {
        override fun validaKey(key: String): Boolean {
            return Pattern.compile("^[0-9]{11}\$").matcher(key).matches()
        }

        override fun toStub(): TipoKey {
            return TipoKey.CPF
        }
    }, CNPJ {
        override fun validaKey(key: String): Boolean {
            return Pattern.compile("^\\d{2}\\.\\d{3}\\.\\d{3}\\/\\d{4}\\-\\d{2}\$").matcher(key).matches()
        }

        override fun toStub(): TipoKey {
            return TipoKey.CNPJ
        }
    }, PHONE {
        override fun validaKey(key: String): Boolean {
            return Pattern.compile("^\\+[1-9][0-9]\\d{1,14}\$").matcher(key).matches()
        }

        override fun toStub(): TipoKey {
            return TipoKey.PHONE
        }
    }, EMAIL {
        override fun validaKey(key: String): Boolean {
            return Pattern.compile("[a-z0-9!#\$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#\$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?").matcher(key).matches()
        }

        override fun toStub(): TipoKey {
            return TipoKey.EMAIL
        }
    };

    abstract fun validaKey(key: String): Boolean
    abstract fun toStub(): TipoKey
}
