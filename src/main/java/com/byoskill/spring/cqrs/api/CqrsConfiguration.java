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
package com.byoskill.spring.cqrs.api;

import java.util.concurrent.ForkJoinPool;

/**
 * The Interface CqrsConfiguration defines the configuration required to boot
 * the module.
 */
public interface CqrsConfiguration {

    /**
     * Provides a fork join pool to allow async executions for the commands.
     *
     * @return the fork join pool for async execution
     */
    ForkJoinPool getForkJoinPool();

    /**
     * Gets the logging configuration.
     *
     * @return the logging configuration
     */
    LoggingConfiguration getLoggingConfiguration();

    /**
     * Returns a throttling interface. The default behaviour is simply ignoring the
     * throttling.
     *
     * @return the i throttling interface
     */
    ThrottlingInterface getThrottlingInterface();

    /**
     * Gets the trace configuration.
     *
     * @return the trace configuration
     */
    TraceConfiguration getTraceConfiguration();
}