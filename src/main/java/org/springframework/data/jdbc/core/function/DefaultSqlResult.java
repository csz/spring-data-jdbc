/*
 * Copyright 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.data.jdbc.core.function;

import io.r2dbc.spi.Connection;
import io.r2dbc.spi.Result;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Default {@link SqlResult} implementation.
 *
 * @author Mark Paluch
 */
class DefaultSqlResult<T> implements SqlResult<T> {

	private final ConnectionAccessor connectionAccessor;
	private final String sql;
	private final Function<Connection, Flux<Result>> resultFunction;
	private final Function<Connection, Mono<Integer>> updatedRowsFunction;
	private final FetchSpec<T> fetchSpec;

	DefaultSqlResult(ConnectionAccessor connectionAccessor, String sql, Function<Connection, Flux<Result>> resultFunction,
			Function<Connection, Mono<Integer>> updatedRowsFunction, BiFunction<Row, RowMetadata, T> mappingFunction) {

		this.sql = sql;
		this.connectionAccessor = connectionAccessor;
		this.resultFunction = resultFunction;
		this.updatedRowsFunction = updatedRowsFunction;

		this.fetchSpec = new DefaultFetchSpec<>(connectionAccessor, sql,
				it -> resultFunction.apply(it).flatMap(result -> result.map(mappingFunction)), updatedRowsFunction);
	}

	/* (non-Javadoc)
	 * @see org.springframework.data.jdbc.core.function.SqlResult#extract(java.util.function.BiFunction)
	 */
	@Override
	public <R> SqlResult<R> extract(BiFunction<Row, RowMetadata, R> mappingFunction) {
		return new DefaultSqlResult<>(connectionAccessor, sql, resultFunction, updatedRowsFunction, mappingFunction);
	}

	/* (non-Javadoc)
	 * @see org.springframework.data.jdbc.core.function.FetchSpec#one()
	 */
	@Override
	public Mono<T> one() {
		return fetchSpec.one();
	}

	/* (non-Javadoc)
	 * @see org.springframework.data.jdbc.core.function.FetchSpec#first()
	 */
	@Override
	public Mono<T> first() {
		return fetchSpec.first();
	}

	/* (non-Javadoc)
	 * @see org.springframework.data.jdbc.core.function.FetchSpec#all()
	 */
	@Override
	public Flux<T> all() {
		return fetchSpec.all();
	}

	/* (non-Javadoc)
	 * @see org.springframework.data.jdbc.core.function.FetchSpec#rowsUpdated()
	 */
	@Override
	public Mono<Integer> rowsUpdated() {
		return fetchSpec.rowsUpdated();
	}
}
