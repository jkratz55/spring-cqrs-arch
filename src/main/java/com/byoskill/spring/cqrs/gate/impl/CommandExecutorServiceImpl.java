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
package com.byoskill.spring.cqrs.gate.impl;

import com.byoskill.spring.cqrs.api.CommandServiceProvider;
import com.byoskill.spring.cqrs.api.CommandServiceSpec;
import com.byoskill.spring.cqrs.api.LoggingConfiguration;
import com.byoskill.spring.cqrs.executors.api.CommandRunnerChain;
import com.byoskill.spring.cqrs.executors.impl.BootstrapRunner;
import com.byoskill.spring.cqrs.executors.impl.DefaultCommandRunner;
import com.byoskill.spring.cqrs.utils.validation.ObjectValidation;
import com.byoskill.spring.cqrs.workflow.impl.CommandRunnerWorkflow;
import com.byoskill.spring.cqrs.workflow.impl.CommandRunnerWorkflowService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.annotation.PreDestroy;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

/**
 * This class prepares the command to be executed. It can override the default
 * command handler with a wrapper with enhanced functionalities. It Executes
 * SEQUENTIALLY the commands.
 *
 * @author Slawek
 */
public class CommandExecutorServiceImpl {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommandExecutorServiceImpl.class);

    private final LoggingConfiguration configuration;

    private final CommandServiceProvider handlersProvider;

    private final ObjectValidation objectValidation;

    private final ForkJoinPool threadPool;

    private final CommandRunnerWorkflowService commandWorkflowService;

    /**
     * Instantiates a new sequential command executor service.
     *
     * @param configuration          the logging configuration
     * @param handlersProvider       the handlers provider
     * @param objectValidation       the object validation
     * @param commandWorkflowService the command workflow service
     * @param threadPoolTaskExecutor the thread pool task executor
     */
    @Autowired
    public CommandExecutorServiceImpl(final LoggingConfiguration configuration,
                                      final CommandServiceProvider handlersProvider,
                                      final ObjectValidation objectValidation,
                                      final CommandRunnerWorkflowService commandWorkflowService,
                                      @Qualifier("cqrs-executor") final ForkJoinPool threadPoolTaskExecutor) {
        super();
        this.configuration = configuration;
        this.handlersProvider = handlersProvider;
        this.commandWorkflowService = commandWorkflowService;
        threadPool = threadPoolTaskExecutor;
        this.objectValidation = objectValidation;

    }

    @PreDestroy
    public void destroy() {
        LOGGER.warn("Closing CQRS Thread pool");
        try {
            LOGGER.warn("Waiting 1 second for threads to stop");
            threadPool.awaitTermination(1, TimeUnit.SECONDS);
        } catch (final InterruptedException e) {
            LOGGER.error("One or more threads didn't finish correctly : {}", e.getMessage(), e);
        }
        final List<Runnable> list = threadPool.shutdownNow();
        LOGGER.warn("{} threads were still running", list.size());
    }

    /**
     * Executes a command in synchronous way.
     *
     * @param <R>          the generic type
     * @param command      the command
     * @param expectedType the expected type
     * @return the result of the command
     */
    public <R> CompletableFuture<R> run(final Object command, final Class<R> expectedType) {
        final CommandServiceSpec<?, ?> handler = handlersProvider.getService(command);
        LOGGER.debug("Lauching the command {} with the expected type {}", command, expectedType);

        final DefaultCommandRunner defaultCommandRunner = new DefaultCommandRunner(handler);
        final CommandRunnerWorkflow runnerWorkflow = commandWorkflowService.getRunnerWorkflow();
        final CommandExecutionContextImpl commandExecutionContext = new CommandExecutionContextImpl(handler, command);
        final CommandRunnerChain commandRunnerChain = runnerWorkflow.buildChain(defaultCommandRunner);
        final BootstrapRunner bootstrap = new BootstrapRunner();
        return CompletableFuture.supplyAsync(
                () -> (R) bootstrap.execute(commandExecutionContext, commandRunnerChain),
                threadPool);
    }

}