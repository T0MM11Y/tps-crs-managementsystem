package com.twm.mgmt.config;

import java.nio.charset.StandardCharsets;

import javax.net.ssl.SSLContext;

import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

	@Bean(name = "ntSsoRestTemplate")
	public RestTemplate ntSsoRestTemplate(ClientHttpRequestFactory factory) {
		RestTemplate template = new RestTemplate(factory);

		template.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));

		return template;
	}

	@Bean
	public ClientHttpRequestFactory clientHttpRequestFactory() throws Exception {
		SSLContextBuilder sslContextBuilder = new SSLContextBuilder();
		sslContextBuilder.setProtocol("TLS");
		sslContextBuilder.loadTrustMaterial(new TrustAllStrategy());
		SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslContextBuilder.build(), NoopHostnameVerifier.INSTANCE);
		CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(sslConnectionSocketFactory).build();
		HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);
		factory.setConnectTimeout(30000);
		factory.setReadTimeout(30000);
		return factory;
	}
	
	
    @Bean
    public RestTemplate restTemplate() throws Exception {
        // 信任所有憑證
        TrustStrategy acceptingTrustStrategy = (cert, authType) -> true;
        SSLContext sslContext = org.apache.http.ssl.SSLContexts
            .custom()
            .loadTrustMaterial(null, acceptingTrustStrategy)
            .build();

        SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(
            sslContext,
            (hostname, session) -> true  // 跳過 Hostname 驗證
        );

        CloseableHttpClient httpClient = HttpClients
            .custom()
            .setSSLSocketFactory(csf)
            .build();

        HttpComponentsClientHttpRequestFactory requestFactory =
            new HttpComponentsClientHttpRequestFactory();
        requestFactory.setHttpClient(httpClient);

        return new RestTemplate(requestFactory);
    }

}
