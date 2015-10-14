/*
 * Copyright 2015 David Karnok
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

package hu.akarnokd.reactivechannel;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.*;

/**
 * Example performance class.
 * <p>
 * gradlew jmh "-Pjmh=ExamplePerf"
 */
@BenchmarkMode(Mode.Throughput)
@Warmup(iterations = 5)
@Measurement(iterations = 5, time = 5, timeUnit = TimeUnit.SECONDS)
@OutputTimeUnit(TimeUnit.SECONDS)
@Fork(value = 1)
@State(Scope.Thread)
public class ExamplePerf {
    @Param({ "1", "1000", "1000000" })
    public int times;
    
    volatile Object reference;
    volatile int value;
    
    static final Object REFERENCE = new Object();
    static final int VALUE = 123;
    
    @Benchmark
    public void referenceStore() {
        int s = times;
        for (int i = 0; i < s; i++) {
            reference = REFERENCE;
        }
    }
    @Benchmark
    public void valueStore() {
        int s = times;
        for (int i = 0; i < s; i++) {
            value = VALUE;
        }
    }
}