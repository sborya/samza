/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.samza.operators.impl;

import java.util.Collection;
import org.apache.samza.operators.KV;
import org.apache.samza.operators.spec.InputOperatorSpec;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.SystemStreamPartition;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.TaskCoordinator;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class TestInputOperatorImpl {
  @Test
  public void testWithKeyedInput() {
    InputOperatorImpl inputOperator =
        new InputOperatorImpl(new InputOperatorSpec("stream-id", null, null, null, true, "input-op-id"));

    IncomingMessageEnvelope ime =
        new IncomingMessageEnvelope(mock(SystemStreamPartition.class), "123", "key", "msg");

    Collection<Object> results =
        inputOperator.handleMessage(ime, mock(MessageCollector.class), mock(TaskCoordinator.class));

    Object result = results.iterator().next();
    assertEquals("key", ((KV) result).getKey());
    assertEquals("msg", ((KV) result).getValue());
  }

  @Test
  public void testWithUnkeyedInput() {
    InputOperatorImpl inputOperator =
        new InputOperatorImpl(new InputOperatorSpec("stream-id", null, null, null, false, "input-op-id"));

    IncomingMessageEnvelope ime =
        new IncomingMessageEnvelope(mock(SystemStreamPartition.class), "123", "key", "msg");

    Collection<Object> results =
        inputOperator.handleMessage(ime, mock(MessageCollector.class), mock(TaskCoordinator.class));

    Object result = results.iterator().next();
    assertEquals("msg", result);
  }

  @Test
  public void testWithInputTransformer() {
    InputOperatorSpec inputOpSpec =
        new InputOperatorSpec("stream-id", null, null, IncomingMessageEnvelope::getOffset, true, "input-op-id");
    InputOperatorImpl inputOperator = new InputOperatorImpl(inputOpSpec);

    IncomingMessageEnvelope ime =
        new IncomingMessageEnvelope(mock(SystemStreamPartition.class), "123", "key", "msg");

    Collection<Object> results =
        inputOperator.handleMessage(ime, mock(MessageCollector.class), mock(TaskCoordinator.class));

    Object result = results.iterator().next();
    assertEquals("123", result);
  }

  @Test
  public void testWithFilteringInputTransformer() {
    InputOperatorSpec inputOpSpec =
        new InputOperatorSpec("stream-id", null, null, (ime) -> null, true, "input-op-id");
    InputOperatorImpl inputOperator = new InputOperatorImpl(inputOpSpec);

    IncomingMessageEnvelope ime =
        new IncomingMessageEnvelope(mock(SystemStreamPartition.class), "123", "key", "msg");

    Collection<Object> results =
        inputOperator.handleMessage(ime, mock(MessageCollector.class), mock(TaskCoordinator.class));
    assertTrue("Transformer doesn't return any record. Expected an empty collection", results.isEmpty());
  }
}
