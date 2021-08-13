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
package org.apache.commons.rng.sampling.distribution;

import org.apache.commons.rng.RestorableUniformRandomProvider;
import org.apache.commons.rng.simple.RandomSource;
import org.junit.Assert;
import org.junit.Test;

/**
 * This test checks the {@link PoissonSamplerCache} functions exactly like the
 * constructor of the {@link PoissonSampler}, irrespective of the range
 * covered by the cache.
 */
public class PoissonSamplerCacheTest {

    // Set a range so that the SmallMeanPoissonSampler is also required.

    /** The minimum of the range of the mean */
    private final int minRange = (int) Math.floor(PoissonSampler.PIVOT - 2);
    /** The maximum of the range of the mean */
    private final int maxRange = (int) Math.floor(PoissonSampler.PIVOT + 6);
    /** The mid-point of the range of the mean */
    private final int midRange = (minRange + maxRange) / 2;

    /**
     * Test the cache reports the minimum mean that uses an algorithm that supports caching.
     * This mean is the same level as the algorithm switch point in the PoissonSampler.
     */
    @Test
    public void testMinimumCachedMean() {
        Assert.assertEquals(PoissonSampler.PIVOT, PoissonSamplerCache.getMinimumCachedMean(), 0);
    }

    // Edge cases for construction

    /**
     * Test the cache can be created without a range that requires a cache.
     * In this case the cache will be a pass through to the constructor
     * of the SmallMeanPoissonSampler.
     */
    @Test
    public void testConstructorWithNoCache() {
        final double min = 0;
        final double max = PoissonSampler.PIVOT - 2;
        final PoissonSamplerCache cache = createPoissonSamplerCache(min, max);
        Assert.assertFalse(cache.isValidRange());
        Assert.assertEquals(0, cache.getMinMean(), 0);
        Assert.assertEquals(0, cache.getMaxMean(), 0);
    }

    /**
     * Test the cache can be created with a range of 1.
     * In this case the cache will be valid for all mean values
     * in the range {@code n <= mean < n+1}.
     */
    @Test
    public void testConstructorWhenMaxEqualsMin() {
        final double min = PoissonSampler.PIVOT + 2;
        final double max = min;
        final PoissonSamplerCache cache = createPoissonSamplerCache(min, max);
        Assert.assertTrue(cache.isValidRange());
        Assert.assertEquals(min, cache.getMinMean(), 0);
        Assert.assertEquals(Math.nextDown(Math.floor(max) + 1),
                            cache.getMaxMean(), 0);
    }

    /**
     * Test the cache can be created with a range of 1.
     * In this case the cache will be valid for all mean values
     * in the range {@code n <= mean < n+1}.
     */
    @Test
    public void testConstructorWhenMaxAboveMin() {
        final double min = PoissonSampler.PIVOT + 2;
        final double max = min + 10;
        final PoissonSamplerCache cache = createPoissonSamplerCache(min, max);
        Assert.assertTrue(cache.isValidRange());
        Assert.assertEquals(min, cache.getMinMean(), 0);
        Assert.assertEquals(Math.nextDown(Math.floor(max) + 1),
                            cache.getMaxMean(), 0);
    }

    /**
     * Test the cache requires a range with {@code max >= min}.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testConstructorThrowsWhenMaxIsLessThanMin() {
        final double min = PoissonSampler.PIVOT;
        final double max = Math.nextDown(min);
        createPoissonSamplerCache(min, max);
    }

    /**
     * Test the cache can be created with a min range below 0.
     * In this case the range is truncated to 0.
     */
    @Test
    public void testConstructorWhenMinBelow0() {
        final double min = -1;
        final double max = PoissonSampler.PIVOT + 2;
        final PoissonSamplerCache cache = createPoissonSamplerCache(min, max);
        Assert.assertTrue(cache.isValidRange());
        Assert.assertEquals(PoissonSampler.PIVOT, cache.getMinMean(), 0);
        Assert.assertEquals(Math.nextDown(Math.floor(max) + 1),
                            cache.getMaxMean(), 0);
    }

    /**
     * Test the cache can be created with a max range below 0.
     * In this case the range is truncated to 0, i.e. no cache.
     */
    @Test
    public void testConstructorWhenMaxBelow0() {
        final double min = -10;
        final double max = -1;
        final PoissonSamplerCache cache = createPoissonSamplerCache(min, max);
        Assert.assertFalse(cache.isValidRange());
        Assert.assertEquals(0, cache.getMinMean(), 0);
        Assert.assertEquals(0, cache.getMaxMean(), 0);
    }

    /**
     * Test the cache can be created without a range that requires a cache.
     * In this case the cache will be a pass through to the constructor
     * of the SmallMeanPoissonSampler.
     */
    @Test
    public void testWithRangeConstructorWithNoCache() {
        final double min = 0;
        final double max = PoissonSampler.PIVOT - 2;
        final PoissonSamplerCache cache = createPoissonSamplerCache().withRange(min, max);
        Assert.assertFalse(cache.isValidRange());
        Assert.assertEquals(0, cache.getMinMean(), 0);
        Assert.assertEquals(0, cache.getMaxMean(), 0);
    }

    /**
     * Test the cache can be created with a range of 1.
     * In this case the cache will be valid for all mean values
     * in the range {@code n <= mean < n+1}.
     */
    @Test
    public void testWithRangeConstructorWhenMaxEqualsMin() {
        final double min = PoissonSampler.PIVOT + 2;
        final double max = min;
        final PoissonSamplerCache cache = createPoissonSamplerCache().withRange(min, max);
        Assert.assertTrue(cache.isValidRange());
        Assert.assertEquals(min, cache.getMinMean(), 0);
        Assert.assertEquals(Math.nextDown(Math.floor(max) + 1),
                            cache.getMaxMean(), 0);
    }

    /**
     * Test the cache can be created with a range of 1.
     * In this case the cache will be valid for all mean values
     * in the range {@code n <= mean < n+1}.
     */
    @Test
    public void testWithRangeConstructorWhenMaxAboveMin() {
        final double min = PoissonSampler.PIVOT + 2;
        final double max = min + 10;
        final PoissonSamplerCache cache = createPoissonSamplerCache().withRange(min, max);
        Assert.assertTrue(cache.isValidRange());
        Assert.assertEquals(min, cache.getMinMean(), 0);
        Assert.assertEquals(Math.nextDown(Math.floor(max) + 1),
                            cache.getMaxMean(), 0);
    }

    /**
     * Test the cache requires a range with {@code max >= min}.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testWithRangeConstructorThrowsWhenMaxIsLessThanMin() {
        final double min = PoissonSampler.PIVOT;
        final double max = Math.nextDown(min);
        createPoissonSamplerCache().withRange(min, max);
    }

    /**
     * Test the cache can be created with a min range below 0.
     * In this case the range is truncated to 0.
     */
    @Test
    public void testWithRangeConstructorWhenMinBelow0() {
        final double min = -1;
        final double max = PoissonSampler.PIVOT + 2;
        final PoissonSamplerCache cache = createPoissonSamplerCache().withRange(min, max);
        Assert.assertTrue(cache.isValidRange());
        Assert.assertEquals(PoissonSampler.PIVOT, cache.getMinMean(), 0);
        Assert.assertEquals(Math.nextDown(Math.floor(max) + 1),
                            cache.getMaxMean(), 0);
    }

    /**
     * Test the cache can be created from a cache with no capacity.
     */
    @Test
    public void testWithRangeConstructorWhenCacheHasNoCapcity() {
        final double min = PoissonSampler.PIVOT + 2;
        final double max = min + 10;
        final PoissonSamplerCache cache = createPoissonSamplerCache(0, 0).withRange(min, max);
        Assert.assertTrue(cache.isValidRange());
        Assert.assertEquals(min, cache.getMinMean(), 0);
        Assert.assertEquals(Math.nextDown(Math.floor(max) + 1),
                            cache.getMaxMean(), 0);
    }

    /**
     * Test the withinRange function of the cache signals when construction
     * cost is minimal.
     */
    @Test
    public void testWithinRange() {
        final double min = PoissonSampler.PIVOT + 10;
        final double max = PoissonSampler.PIVOT + 20;
        final PoissonSamplerCache cache = createPoissonSamplerCache(min, max);
        // Under the pivot point is always within range
        Assert.assertTrue(cache.withinRange(PoissonSampler.PIVOT - 1));
        Assert.assertFalse(cache.withinRange(min - 1));
        Assert.assertTrue(cache.withinRange(min));
        Assert.assertTrue(cache.withinRange(max));
        Assert.assertFalse(cache.withinRange(max + 10));
    }

    // Edge cases for creating a Poisson sampler

    /**
     * Test createSharedStateSampler() with a bad mean.
     *
     * <p>Note this test actually tests the SmallMeanPoissonSampler throws.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCreateSharedStateSamplerThrowsWithZeroMean() {
        final RestorableUniformRandomProvider rng =
                RandomSource.SPLIT_MIX_64.create(0L);
        final PoissonSamplerCache cache = createPoissonSamplerCache();
        cache.createSharedStateSampler(rng, 0);
    }

    /**
     * Test createSharedStateSampler() with a mean that is too large.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCreateSharedStateSamplerThrowsWithNonIntegerMean() {
        final RestorableUniformRandomProvider rng =
                RandomSource.SPLIT_MIX_64.create(0L);
        final PoissonSamplerCache cache = createPoissonSamplerCache();
        final double mean = Integer.MAX_VALUE + 1.0;
        cache.createSharedStateSampler(rng, mean);
    }

    // Sampling tests

    /**
     * Test the cache returns the same samples as the PoissonSampler when it
     * covers the entire range.
     */
    @Test
    public void testCanComputeSameSamplesAsPoissonSamplerWithFullRangeCache() {
        checkComputeSameSamplesAsPoissonSampler(minRange,
                                                maxRange);
    }

    /**
     * Test the cache returns the same samples as the PoissonSampler
     * with no cache.
     */
    @Test
    public void testCanComputeSameSamplesAsPoissonSamplerWithNoCache() {
        checkComputeSameSamplesAsPoissonSampler(0,
                                                minRange - 2);
    }

    /**
     * Test the cache returns the same samples as the PoissonSampler with
     * partial cache covering the lower range.
     */
    @Test
    public void testCanComputeSameSamplesAsPoissonSamplerWithPartialCacheCoveringLowerRange() {
        checkComputeSameSamplesAsPoissonSampler(minRange,
                                                midRange);
    }

    /**
     * Test the cache returns the same samples as the PoissonSampler with
     * partial cache covering the upper range.
     */
    @Test
    public void testCanComputeSameSamplesAsPoissonSamplerWithPartialCacheCoveringUpperRange() {
        checkComputeSameSamplesAsPoissonSampler(midRange,
                                                maxRange);
    }

    /**
     * Test the cache returns the same samples as the PoissonSampler with
     * cache above the upper range.
     */
    @Test
    public void testCanComputeSameSamplesAsPoissonSamplerWithCacheAboveTheUpperRange() {
        checkComputeSameSamplesAsPoissonSampler(maxRange + 10,
                                                maxRange + 20);
    }

    /**
     * Check poisson samples are the same from the {@link PoissonSampler}
     * and {@link PoissonSamplerCache}.
     *
     * @param minMean the min mean of the cache
     * @param maxMean the max mean of the cache
     */
    private void checkComputeSameSamplesAsPoissonSampler(int minMean,
                                                         int maxMean) {
        // Two identical RNGs
        final RandomSource source = RandomSource.SPLIT_MIX_64;
        final long seed = RandomSource.createLong();
        final RestorableUniformRandomProvider rng1 = source.create(seed);
        final RestorableUniformRandomProvider rng2 = source.create(seed);

        // Create the cache with the given range
        final PoissonSamplerCache cache =
                createPoissonSamplerCache(minMean, maxMean);
        // Test all means in the test range (which may be different
        // from the cache range).
        for (int i = minRange; i <= maxRange; i++) {
            // Test integer mean (no SmallMeanPoissonSampler required)
            testPoissonSamples(rng1, rng2, cache, i);
            // Test non-integer mean (SmallMeanPoissonSampler required)
            testPoissonSamples(rng1, rng2, cache, i + 0.5);
        }
    }

    /**
     * Creates the poisson sampler cache with the given range.
     *
     * @param minMean the min mean
     * @param maxMean the max mean
     * @return the poisson sampler cache
     */
    private static PoissonSamplerCache createPoissonSamplerCache(double minMean,
                                                          double maxMean) {
        return new PoissonSamplerCache(minMean, maxMean);
    }

    /**
     * Creates a poisson sampler cache that will have a valid range for the cache.
     *
     * @return the poisson sampler cache
     */
    private static PoissonSamplerCache createPoissonSamplerCache() {
        return new PoissonSamplerCache(PoissonSampler.PIVOT,
                                       PoissonSampler.PIVOT + 10);
    }

    /**
     * Test poisson samples are the same from the {@link PoissonSampler}
     * and {@link PoissonSamplerCache}. The random providers must be
     * identical (including state).
     *
     * @param rng1  the first random provider
     * @param rng2  the second random provider
     * @param cache the cache
     * @param mean  the mean
     */
    private static void testPoissonSamples(
            final RestorableUniformRandomProvider rng1,
            final RestorableUniformRandomProvider rng2,
            PoissonSamplerCache cache,
            double mean) {
        final DiscreteSampler s1 = PoissonSampler.of(rng1, mean);
        final DiscreteSampler s2 = cache.createSharedStateSampler(rng2, mean);
        for (int j = 0; j < 10; j++) {
            Assert.assertEquals(s1.sample(), s2.sample());
        }
    }

    /**
     * Test the cache returns the same samples as the PoissonSampler with
     * a new cache reusing the entire range.
     */
    @Test
    public void testCanComputeSameSamplesAsPoissonSamplerReusingCacheEntireRange() {
        checkComputeSameSamplesAsPoissonSamplerReusingCache(midRange,
                                                            maxRange,
                                                            midRange,
                                                            maxRange);
    }

    /**
     * Test the cache returns the same samples as the PoissonSampler with
     * a new cache reusing none of the range.
     */
    @Test
    public void testCanComputeSameSamplesAsPoissonSamplerReusingCacheNoRange() {
        checkComputeSameSamplesAsPoissonSamplerReusingCache(midRange,
                                                            maxRange,
                                                            maxRange + 10,
                                                            maxRange + 20);
    }

    /**
     * Test the cache returns the same samples as the PoissonSampler with
     * a new cache reusing some of the lower range.
     */
    @Test
    public void testCanComputeSameSamplesAsPoissonSamplerReusingCacheLowerRange() {
        checkComputeSameSamplesAsPoissonSamplerReusingCache(midRange,
                                                            maxRange,
                                                            minRange,
                                                            midRange + 1);
    }

    /**
     * Test the cache returns the same samples as the PoissonSampler with
     * a new cache reusing some of the upper range.
     */
    @Test
    public void testCanComputeSameSamplesAsPoissonSamplerReusingCacheUpperRange() {
        checkComputeSameSamplesAsPoissonSamplerReusingCache(midRange,
                                                            maxRange,
                                                            maxRange - 1,
                                                            maxRange + 5);
    }

    /**
     * Check poisson samples are the same from the {@link PoissonSampler}
     * and a {@link PoissonSamplerCache} created reusing values.
     *
     * <p>Note: This cannot check the cache values were reused but ensures
     * that a new cache created with a range functions correctly.
     *
     * @param minMean  the min mean of the cache
     * @param maxMean  the max mean of the cache
     * @param minMean2 the min mean of the second cache
     * @param maxMean2 the max mean of the second cache
     */
    private void checkComputeSameSamplesAsPoissonSamplerReusingCache(int minMean,
                                                                     int maxMean,
                                                                     int minMean2,
                                                                     int maxMean2) {
        // Two identical RNGs
        final RandomSource source = RandomSource.SPLIT_MIX_64;
        final long seed = RandomSource.createLong();
        final RestorableUniformRandomProvider rng1 = source.create(seed);
        final RestorableUniformRandomProvider rng2 = source.create(seed);

        // Create the cache with the given range and fill it
        final PoissonSamplerCache cache =
                createPoissonSamplerCache(minMean, maxMean);
        // Test all means in the test range (which may be different
        // from the cache range).
        for (int i = minMean; i <= maxMean; i++) {
            cache.createSharedStateSampler(rng1, i);
        }

        final PoissonSamplerCache cache2 = cache.withRange(minMean2, maxMean2);
        Assert.assertNotSame("WithRange cache is the same object", cache, cache2);

        // Test all means in the test range (which may be different
        // from the cache range).
        for (int i = minRange; i <= maxRange; i++) {
            // Test integer mean (no SmallMeanPoissonSampler required)
            testPoissonSamples(rng1, rng2, cache2, i);
            // Test non-integer mean (SmallMeanPoissonSampler required)
            testPoissonSamples(rng1, rng2, cache2, i + 0.5);
        }
    }

    /**
     * Explicit test for the deprecated method createPoissonSampler().
     * All other uses of this method have been removed.
     */
    @SuppressWarnings("deprecation")
    @Test
    public void testCreatePoissonSampler() {
        final PoissonSamplerCache cache = createPoissonSamplerCache(0, 100);
        final DiscreteSampler s2 = cache.createPoissonSampler(null, 42);
        Assert.assertTrue(s2 instanceof LargeMeanPoissonSampler);
    }
}
