package com.github.sujankumarmitra.libraryservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.tools.agent.ReactorDebugAgent;

//@SpringBootApplication(exclude = {R2dbcAutoConfiguration.class, R2dbcDataAutoConfiguration.class})
@SpringBootApplication
public class LibraryServiceApplication {

//	@Bean
//	public DatabaseClient client() {
//		return new DatabaseClient() {
//			@Override
//			public ConnectionFactory getConnectionFactory() {
//				return null;
//			}
//
//			@Override
//			public GenericExecuteSpec sql(String sql) {
//				return null;
//			}
//
//			@Override
//			public GenericExecuteSpec sql(Supplier<String> sqlSupplier) {
//				return null;
//			}
//
//			@Override
//			public <T> Mono<T> inConnection(Function<Connection, Mono<T>> action) throws DataAccessException {
//				return null;
//			}
//
//			@Override
//			public <T> Flux<T> inConnectionMany(Function<Connection, Flux<T>> action) throws DataAccessException {
//				return null;
//			}
//		};
//	}

	public static void main(String[] args) {
		ReactorDebugAgent.init();
		SpringApplication.run(LibraryServiceApplication.class, args);
	}

}
