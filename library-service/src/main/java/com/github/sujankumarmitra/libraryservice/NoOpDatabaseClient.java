package com.github.sujankumarmitra.libraryservice;

import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author skmitra
 * @since Dec 01/12/21, 2021
 */
class NoOpDatabaseClient implements DatabaseClient {
    @Override
    public ConnectionFactory getConnectionFactory() {
        return null;
    }

    @Override
    public GenericExecuteSpec sql(String sql) {
        return null;
    }

    @Override
    public GenericExecuteSpec sql(Supplier<String> sqlSupplier) {
        return null;
    }

    @Override
    public <T> Mono<T> inConnection(Function<Connection, Mono<T>> action) throws DataAccessException {
        return null;
    }

    @Override
    public <T> Flux<T> inConnectionMany(Function<Connection, Flux<T>> action) throws DataAccessException {
        return null;
    }
}
