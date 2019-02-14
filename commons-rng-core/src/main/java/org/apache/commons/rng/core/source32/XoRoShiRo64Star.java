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

package org.apache.commons.rng.core.source32;

/**
 * A fast 32-bit generator suitable for {@code float} generation. This is slightly faster than the
 * all-purpose generator {@link XoRoShiRo64StarStar}.
 *
 * <p>This is a member of the Xor-Shift-Rotate family of generators. Memory footprint is 64 bits.
 *
 * @see <a href="http://xoshiro.di.unimi.it/xoroshiro64star.c">Original source code</a>
 * @see <a href="http://xoshiro.di.unimi.it/">xorshiro / xoroshiro generators</a>
 *
 * @since 1.3
 */
public class XoRoShiRo64Star extends AbstractXoRoShiRo64 {
    /**
     * Creates a new instance.
     *
     * @param seed Initial seed.
     * If the length is larger than 2, only the first 2 elements will
     * be used; if smaller, the remaining elements will be automatically
     * set.
     * A seed containing all zeros will create a non-functional generator.
     */
    public XoRoShiRo64Star(int[] seed) {
        super(seed);
    }

    /**
     * Creates a new instance using a 2 element seed.
     * A seed containing all zeros will create a non-functional generator.
     *
     * @param seed0 Initial seed element 0.
     * @param seed1 Initial seed element 1.
     */
    public XoRoShiRo64Star(int seed0, int seed1) {
        super(seed0, seed1);
    }

    /**
     * Creates a new instance using the upper and lower bits from the {@code long}
     * to create a 2 element {@code int} seed.
     * A seed containing all zeros will create a non-functional generator.
     *
     * @param seed Initial seed.
     */
    public XoRoShiRo64Star(long seed) {
        super(seed);
    }

    /** {@inheritDoc} */
    @Override
    public int next() {
        final int s0 = state0;
        int s1 = state1;
        final int result = s0 * 0x9E3779BB;

        s1 ^= s0;
        state0 = Integer.rotateLeft(s0, 26) ^ s1 ^ (s1 << 9); // a, b
        state1 = Integer.rotateLeft(s1, 13); // c

        return result;
    }
}
