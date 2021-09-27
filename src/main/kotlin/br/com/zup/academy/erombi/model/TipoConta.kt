package br.com.zup.academy.erombi.model

import br.com.zup.academy.erombi.TipoConta

enum class TipoConta {
    CONTA_CORRENTE {
        override fun toStub(): TipoConta {
            return TipoConta.CACC
        }
    }, CONTA_POUPANCA {
        override fun toStub(): TipoConta {
            return TipoConta.SVGS
        }
    };

    abstract fun toStub(): TipoConta

}
