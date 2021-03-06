/*
 * Copyright (C) 2012-2019, TomTom (http://tomtom.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tomtom.speedtools.rest;

import akka.actor.ActorSystem;
import com.google.inject.Injector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * This class implements an event listener for the servlet context (defined in web.xml).
 */
@WebListener
public class Shutdown implements ServletContextListener {
    private static final Logger LOG = LoggerFactory.getLogger(Shutdown.class);

    private static final long WAIT_TIMEOUT_MS = 10000;

    @Override
    public void contextInitialized(@Nullable final ServletContextEvent sce) {
        LOG.info("contextInitialized: context initialized");
    }

    @SuppressWarnings("WaitNotInLoop")
    @Override
    public void contextDestroyed(@Nullable final ServletContextEvent sce) {
        LOG.info("contextDestroyed: context destroyed");

        // Find injector
        final Injector injector = InjectorRegistry.getInjector();
        if (injector != null) {

            // Stop Akka.
            LOG.info("contextDestroyed: shutting down Akka");
            try {
                final ActorSystem actorSystem = injector.getInstance(ActorSystem.class);
                actorSystem.terminate();
            } catch (final RuntimeException e) {
                LOG.warn("contextDestroyed: shut down of Akka failed: ", e);
                throw e;
            }

            // Clear injector.
            InjectorRegistry.clear();
        }
    }
}
