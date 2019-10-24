/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.test.testcontainers.spring.junit5;

import org.apache.camel.test.testcontainers.junit5.Wait;
import org.apache.camel.test.testcontainers.spring.junit5.ContainerAwareSpringTestSupport;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.testcontainers.containers.GenericContainer;

public class ContainerAwareSpringTestSupportIT extends ContainerAwareSpringTestSupport {
    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("org/apache/camel/test/testcontainers/spring/junit5/ContainerAwareSpringTestSupportTest.xml");
    }

    @Test
    public void testPropertyPlaceholders() throws Exception {
        final GenericContainer<?> container = getContainer("myconsul");

        final String host = context.resolvePropertyPlaceholders("{{container:host:myconsul}}");
        Assertions.assertThat(host).isEqualTo(container.getContainerIpAddress());

        final String port = context.resolvePropertyPlaceholders("{{container:port:8500@myconsul}}");
        Assertions.assertThat(port).isEqualTo("" + container.getMappedPort(8500));
    }

    @Override
    protected GenericContainer<?> createContainer() {
        return new GenericContainer("consul:1.5.3")
            .withNetworkAliases("myconsul")
            .withExposedPorts(8500)
            .waitingFor(Wait.forLogMessageContaining("Synced node info", 1))
            .withCommand(
                "agent",
                "-dev",
                "-server",
                "-bootstrap",
                "-client",
                "0.0.0.0",
                "-log-level",
                "trace"
            );
    }

}
