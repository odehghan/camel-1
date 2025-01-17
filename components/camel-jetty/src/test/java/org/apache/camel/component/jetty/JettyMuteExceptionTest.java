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
package org.apache.camel.component.jetty;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.junit.Assert;
import org.junit.Test;

public class JettyMuteExceptionTest extends BaseJettyTest {

    @Test
    public void testMuteException() throws Exception {

        HttpClient client = new HttpClient();
        GetMethod get = new GetMethod("http://localhost:" + getPort() + "/foo");
        get.setRequestHeader("Accept", "application/text");
        client.executeMethod(get);

        String body = get.getResponseBodyAsString();
        Assert.assertNotNull(body);
        Assert.assertEquals("Exception", body);
        Assert.assertEquals(500, get.getStatusCode());
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("jetty:http://localhost:{{port}}/foo?muteException=true").to("mock:destination").throwException(new IllegalArgumentException("Camel cannot do this"));
            }
        };
    }

}
