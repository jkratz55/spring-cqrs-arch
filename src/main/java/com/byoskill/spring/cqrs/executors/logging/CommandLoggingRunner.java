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
package com.byoskill.spring.cqrs.executors.logging;

import com.byoskill.spring.cqrs.api.LoggingConfiguration;
import com.byoskill.spring.cqrs.executors.api.CommandExecutionContext;
import com.byoskill.spring.cqrs.executors.api.CommandRunner;
import com.byoskill.spring.cqrs.executors.api.CommandRunnerChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;

public class CommandLoggingRunner implements CommandRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommandLoggingRunner.class);

    private final LoggingConfiguration configuration;

    /**
     * Instantiates a new command logging service.
     *
     * @param configuration the configuration
     */
    @Autowired
    public CommandLoggingRunner(final LoggingConfiguration configuration) {
        super();
        this.configuration = configuration;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.byoskill.spring.cqrs.executors.api.CommandRunner#execute(com.byoskill.
     * spring.cqrs.executors.api.CommandExecutionContext,
     * com.byoskill.spring.cqrs.executors.api.CommandRunnerChain)
     */
    @Override
    public Object execute(final CommandExecutionContext context, final CommandRunnerChain chain)
            throws RuntimeException {
        Object res = null;
        try {
            MDC.put("command", context.getRawCommand().getClass().getName());
            if (configuration.isLoggingEnabled()) {
                LOGGER.info("Command to be executed : {}", context.getRawCommand());
            }
            res = chain.execute(context);
            if (configuration.isLoggingEnabled()) {
                LOGGER.info("Command has been executed with success {} with the result {}",
                        context.getRawCommand(), res);
            }
        } catch (final Exception t) {
            if (configuration.isLoggingEnabled()) {
                final Object command = context.getRawCommand();

                if (t != null) {
                    LOGGER.error("Command {} has failed with informations {} for the reason {}",
                            command.getClass().getName(),
                            command,
                            t);
                } else {
                    LOGGER.error("Command {} with informations {} has failed.", command.getClass().getName(), command);
                }
            }
            throw t;
        } finally {
            MDC.remove("command");
        }
        return res;
    }

}
