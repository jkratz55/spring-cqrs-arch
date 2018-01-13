package com.byoskill.spring.cqrs.gate.impl;

import org.junit.Assert;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.byoskill.spring.cqrs.gate.api.Gate;
import com.byoskill.spring.cqrs.gate.api.ICommandExceptionHandler;
import com.byoskill.spring.cqrs.gate.conf.CqrsConfiguration;

@Configuration
@ComponentScan("com.byoskill")

public class TestConfiguration {




    @Bean
    public CqrsConfiguration configuration() {
	return new CqrsConfiguration();
    }

    @Bean
    public DummyObjectCommandHandler dummyValidationHandler(final Gate gate) {
	return new DummyObjectCommandHandler();
    }

    @Bean
    public StringCommandHandler exampleHandler(final Gate gate) {
	return new StringCommandHandler(gate);
    }

    @Bean
    public ICommandExceptionHandler exceptionHandler() {
	return context -> {
	    Assert.fail(context.getException().getMessage());

	};
    }

}