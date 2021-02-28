/*
 * Copyright 2013-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.openfeign;

import feign.AsyncClient;
import feign.Client;
import feign.hc5.ApacheHttp5Client;
import feign.hc5.AsyncApacheHttp5Client;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.nio.PoolingAsyncClientConnectionManager;
import org.apache.hc.client5.http.nio.AsyncClientConnectionManager;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.commons.httpclient.HttpClientConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Nguyen Ky Thanh
 */
@ExtendWith({ SpringExtension.class })
public class FeignAsyncHttpClient5ConfigurationTests {

	@Test
	public void verifyAsyncHttpClient5AutoConfig() {
		ConfigurableApplicationContext context = new SpringApplicationBuilder()
				.properties("feign.httpclient.hc5.async.enabled=true",
						"feign.httpclient.enabled=false")
				.web(WebApplicationType.NONE)
				.sources(HttpClientConfiguration.class, FeignAutoConfiguration.class)
				.run();

		CloseableHttpAsyncClient asyncHttpClient5 = context
				.getBean(CloseableHttpAsyncClient.class);
		assertThat(asyncHttpClient5).isNotNull();
		AsyncClientConnectionManager asyncConnectionManager = context
				.getBean(AsyncClientConnectionManager.class);
		assertThat(asyncConnectionManager)
				.isInstanceOf(PoolingAsyncClientConnectionManager.class);
		AsyncClient<HttpClientContext> asyncClient = context.getBean(AsyncClient.class);
		assertThat(asyncClient).isInstanceOf(AsyncApacheHttp5Client.class);

		if (context != null) {
			context.close();
		}
	}

	@Test
	public void bothVersionsAvailable() {
		ConfigurableApplicationContext context = new SpringApplicationBuilder()
				.properties("feign.httpclient.hc5.async.enabled=true",
						"feign.httpclient.hc5.enabled=true")
				.web(WebApplicationType.NONE)
				.sources(HttpClientConfiguration.class, FeignAutoConfiguration.class)
				.run();

		Client client = context.getBean(Client.class);
		assertThat(client).isInstanceOf(ApacheHttp5Client.class);

		AsyncClient<HttpClientContext> asyncClient = context.getBean(AsyncClient.class);
		assertThat(asyncClient).isInstanceOf(AsyncApacheHttp5Client.class);

		if (context != null) {
			context.close();
		}
	}

}
