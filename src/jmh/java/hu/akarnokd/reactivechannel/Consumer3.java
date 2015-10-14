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
/**
 * Represents a consumer with three arguments.
 *
 * @param <T1> the first argument type
 * @param <T2> the second argument type
 * @param <T3> the thrird argument type
 */
@FunctionalInterface
public interface Consumer3<T1, T2, T3> {
    /**
     * Applies a computation to the given arguments.
     * @param t1
     * @param t2
     * @param t3
     */
    void accept(T1 t1, T2 t2, T3 t3);
}