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
 * Base interface for receiving onError or onComplete events.
 */
public interface ChannelTerminalEvents {
    /**
     * Receives an error event at most once.
     * @param error the Trowable, not null
     */
    void onError(Throwable error);

    /**
     * Receives a completion event at most once.
     */
    void onComplete();
}