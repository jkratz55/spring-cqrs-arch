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
package com.byoskill.spring.cqrs.events.guava;

import com.byoskill.spring.cqrs.annotations.EventHandler;
import com.byoskill.spring.cqrs.gate.api.EventBusService;
import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;

import javax.annotation.PreDestroy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GuavaEventBusService implements BeanPostProcessor, EventBusService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GuavaEventBusService.class);
    private ApplicationContext applicationContext;
    private EventBus eventBus;

    private ExecutorService threadPoolTaskExecutor;

    /**
     * Instantiates a new guava event bus service.
     *
     * @param asyncExecution the async execution
     */
    public GuavaEventBusService(final boolean asyncExecution) {
        if (asyncExecution) {
            // The event bus should handle async event processing.
            threadPoolTaskExecutor = Executors.newCachedThreadPool();
            eventBus = new AsyncEventBus(threadPoolTaskExecutor);
        } else {
            eventBus = new EventBus();
        }

    }

    @PreDestroy
    public void destroy() {
        eventBus = null;
        if (threadPoolTaskExecutor != null) {
            threadPoolTaskExecutor.shutdown();
        }
    }

    @Override
    public Object postProcessAfterInitialization(final Object bean, final String beanName) throws BeansException {
        if (AnnotationUtils.findAnnotation(bean.getClass(), EventHandler.class) != null) {
            registerEventSubscriber(bean);
        }
        return bean;
    }

    @Override
    public Object postProcessBeforeInitialization(final Object bean, final String beanName) throws BeansException {
        return bean;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.springframework.context.ApplicationEventPublisher#publishEvent(java.lang.
     * Object)
     */
    @Override
    public void publishEvent(final Object event) {
        eventBus.post(event);

    }

    /**
     * Register event suscriber.
     *
     * @param bean the bean
     */
    public void registerEventSubscriber(final Object bean) {
        eventBus.register(bean);
    }

}
