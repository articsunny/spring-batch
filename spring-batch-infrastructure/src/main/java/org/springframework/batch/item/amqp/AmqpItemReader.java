/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.batch.item.amqp;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.batch.item.ItemReader;
import org.springframework.util.Assert;

/**
 * <p>
 * AMQP {@link ItemReader} implementation using an {@link AmqpTemplate} to
 * receive and/or convert messages.
 * </p>
 *
 * @author Chris Schaefer
 */
public class AmqpItemReader<T> implements ItemReader<T> {
    private final AmqpTemplate amqpTemplate;
    private Class<? extends T> itemType;

    public AmqpItemReader(final AmqpTemplate amqpTemplate) {
        Assert.notNull(amqpTemplate, "AmpqTemplate must not be null");

        this.amqpTemplate = amqpTemplate;
    }

    @SuppressWarnings("unchecked")
    public T read() {
        if (itemType != null && itemType.isAssignableFrom(Message.class)) {
            return (T) amqpTemplate.receive();
        }

        Object result = amqpTemplate.receiveAndConvert();

        if (itemType != null && result != null) {
            Assert.state(itemType.isAssignableFrom(result.getClass()),
                    "Received message payload of wrong type: expected [" + itemType + "]");
        }

        return (T) result;
    }

    public void setItemType(Class<? extends T> itemType) {
        Assert.notNull(itemType, "Item type cannot be null");
        this.itemType = itemType;
    }
}
