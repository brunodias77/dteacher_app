package com.dias.dteacher.contract;

import java.util.function.Supplier;

public interface TransactionManager {

    /**
     * Executa o bloco dentro de uma transação, retornando um resultado.
     */
    <T> T execute(Supplier<T> action);

    /**
     * Executa o bloco dentro de uma transação sem retorno.
     */
    void executeVoid(Runnable action);
}
