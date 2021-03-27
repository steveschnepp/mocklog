package org.pwkf.httplog.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lombok.extern.java.Log;

@Component
@Log
public class DummyWebMvcConfigurerAdapter implements WebMvcConfigurer {

	@Override
	public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
		// Do nothing. No Converters !
		log.info("configureMessageConverters - before: " + converters);

		List<HttpMessageConverter<?>> newConverters = new ArrayList<HttpMessageConverter<?>>();

		for (HttpMessageConverter<?> httpMessageConverter : converters) {
			if (httpMessageConverter instanceof ByteArrayHttpMessageConverter) {
				newConverters.add(httpMessageConverter);
			}
		}

		converters.clear();
		converters.addAll(newConverters);
		log.info("configureMessageConverters - after: " + converters);

	}
}
