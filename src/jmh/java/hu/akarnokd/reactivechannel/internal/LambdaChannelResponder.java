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

package hu.akarnokd.reactivechannel.internal;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import org.reactivestreams.Publisher;

import hu.akarnokd.reactivechannel.*;
import io.reactivex.Observable;
import io.reactivex.functions.Function3;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.subjects.*;

public final class LambdaChannelResponder<Request, Response, State> 
extends AtomicBoolean
implements ChannelConnection<Request, Response>, ChannelTerminalEvents {
    /** */
    private static final long serialVersionUID = 8965414283526152968L;

    final Function3<State, Request, ChannelTerminalEvents, ? extends Publisher<Response>> queryMapper;

    final Consumer<? super State> stateConsumer;

    final Subject<Object, Object> cancelled;

    final ChannelSubscriber<Request, Response> actual;

    final State state;

    public LambdaChannelResponder(
            ChannelSubscriber<Request, Response> actual,
            State initialState,
            Function3<State, Request, ChannelTerminalEvents, ? extends Publisher<Response>> queryMapper,
                    Consumer<? super State> stateConsumer) {
        this.state = initialState;
        this.actual = actual;
        this.queryMapper = queryMapper;
        this.stateConsumer = stateConsumer;
        this.cancelled = PublishSubject.create().toSerialized();
    }

    @Override
    public Publisher<Response> query(Request request) {
        Publisher<Response> p = queryMapper.apply(state, request, this);

        return Observable.fromPublisher(p).takeUntil(cancelled);
    }

    @Override
    public void close() {
        if (compareAndSet(false, true)) {
            cancelled.onComplete();
            stateConsumer.accept(state);
        }
    }

    @Override
    public void onError(Throwable error) {
        if (compareAndSet(false, true)) {
            cancelled.onError(error);
            stateConsumer.accept(state);
        } else {
            RxJavaPlugins.onError(error);
        }
    }

    @Override
    public void onComplete() {
        close();
    }
}