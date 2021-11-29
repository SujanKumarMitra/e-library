package com.github.sujankumarmitra.libraryservice;

import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.r2dbc.R2dbcDataAutoConfiguration;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.DataAccessException;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.tools.agent.ReactorDebugAgent;

import java.util.function.Function;
import java.util.function.Supplier;

@SpringBootApplication(exclude = {R2dbcAutoConfiguration.class, R2dbcDataAutoConfiguration.class})
public class LibraryServiceApplication {

	@Bean
	public DatabaseClient client() {
		return new DatabaseClient() {
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
		};
	}

	public static void main(String[] args) {
		ReactorDebugAgent.init();
		SpringApplication.run(LibraryServiceApplication.class, args);
	}

}
