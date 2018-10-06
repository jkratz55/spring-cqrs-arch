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
import com.byoskill.spring.cqrs.api.LoggingConfiguration;
import com.google.common.eventbus.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@EventHandler
public class EventLoggerListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventLoggerListener.class);
    private final LoggingConfiguration configuration;

    /**
     * Instantiates a new event logger listener.
     *
     * @param configuration the configuration
     */
    @Autowired
    public EventLoggerListener(final LoggingConfiguration configuration) {
        super();
        this.configuration = configuration;
    }

    /**
     * Triggers when a message is received.
     *
     * @param eventMessage the event message
     */
    @Subscribe
    public void subscribe(final Object eventMessage) {
        if (configuration.isLoggingEnabled()) {
            LOGGER.info("Event sent : {} -> {}", eventMessage.getClass(), eventMessage);
        } else {
            LOGGER.trace("Event sent : {} -> {}", eventMessage.getClass(), eventMessage);
        }
    }

}
