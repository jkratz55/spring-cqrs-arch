/*
 * Copyright (C) 2017 Sylvain Leroy - BYOSkill Company All Rights Reserved
 * You may use, distribute and modify this code under the
 * terms of the MIT license, which unfortunately won't be
 * written for another century.
 *
 * You should have received a copy of the MIT license with
 * this file. If not, please write to: sleroy at byoskill.com, or visit : www.byoskill.com
 *
 */
package com.byoskill.spring.cqrs.gate.conf;

import com.byoskill.spring.cqrs.api.EventBusConfiguration;
import com.byoskill.spring.cqrs.api.LoggingConfiguration;
import com.byoskill.spring.cqrs.events.guava.EventLoggerListener;
import com.byoskill.spring.cqrs.events.guava.GuavaEventBusPostProcessor;
import com.byoskill.spring.cqrs.events.guava.GuavaEventBusService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ImportGuavaAsyncEventBusConfiguration implements EventBusConfiguration {

    @Bean
    @Override
    public GuavaEventBusService eventBus() {
        return new GuavaEventBusService(true);
    }

    @Bean
    public GuavaEventBusPostProcessor eventHandlerScanner(final GuavaEventBusService eventBusService) {
        return new GuavaEventBusPostProcessor(eventBusService);
    }

    @Bean
    public EventLoggerListener eventLoggerListener(final LoggingConfiguration loggingConfiguration) {
        return new EventLoggerListener(loggingConfiguration);
    }
}
