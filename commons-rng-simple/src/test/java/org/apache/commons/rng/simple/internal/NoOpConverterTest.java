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
package org.apache.commons.rng.simple.internal;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests for the {@link NoOpConverter}.
 */
class NoOpConverterTest {
    @Test
    void testNoOpIntegerCoversion() {
        final NoOpConverter<Integer> converter = new NoOpConverter<>();
        final Integer in = 123;
        Assertions.assertSame(in, converter.convert(in));
    }

    @Test
    void testNoOpLongArrayCoversion() {
        final NoOpConverter<long[]> converter = new NoOpConverter<>();
        final long[] in = {123L, 456L, Long.MAX_VALUE};
        Assertions.assertSame(in, converter.convert(in));
    }
}
