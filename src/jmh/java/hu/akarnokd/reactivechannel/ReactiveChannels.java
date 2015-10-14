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

import java.util.function.*;

import org.reactivestreams.Publisher;

import hu.akarnokd.reactivechannel.internal.LambdaChannelResponder;
import io.reactivex.Observable;
import io.reactivex.functions.Function3;
import io.reactivex.subjects.PublishSubject;

/**
 * Static utility methods for creating and working with ChannelPublishers.
 */
public enum ReactiveChannels {
    ;
    
    // ------------------------------------------------------------
    // Methods
    // ------------------------------------------------------------
    
    public static <Request, Response> ChannelPublisher<Request, Response> create(
            BiFunction<Request, ChannelTerminalEvents, ? extends Publisher<Response>> queryMapper) {
        return create(() -> null, (s, r, t) -> queryMapper.apply(r, t), v -> { });
    }
    
    public static <Request, Response, State> ChannelPublisher<Request, Response> create(
            Supplier<State> stateSupplier,
            Function3<State, Request, ChannelTerminalEvents, ? extends Publisher<Response>> queryMapper,
            Consumer<? super State> stateConsumer) {
        return s -> {
            State initialState = stateSupplier.get();
            
            s.onConnect(new LambdaChannelResponder<>(s, initialState, queryMapper, stateConsumer));
        };
    }
    
    public static <Request, Response> Observable<Response> merge(
            ChannelPublisher<Request, Response> channel, 
            Publisher<Request> requests) {
        return Observable.create(s -> {
            channel.subscribe(new ChannelSubscriber<Request, Response>() {
                final PublishSubject<Response> terminate = PublishSubject.create();
                @Override
                public void onError(Throwable error) {
                    terminate.onError(error);
                }

                @Override
                public void onComplete() {
                    terminate.onComplete();
                }

                @Override
                public void onConnect(ChannelConnection<Request, Response> conn) {
                    Observable.fromPublisher(requests)
                    .flatMap(r -> conn.query(r), Integer.MAX_VALUE)
                    .mergeWith(terminate)
                    .doOnCancel(() -> conn.close())
                    .subscribe(s);
                }
                
            });
        });
    }
    
    public static <Request, Response, U> Observable<U> merge(
            ChannelPublisher<Request, Response> channel, 
            Publisher<Request> requests, 
            BiFunction<? super Request, ? super Response, U> resultMapper) {
        return Observable.create(s -> {
            channel.subscribe(new ChannelSubscriber<Request, Response>() {
                final PublishSubject<U> terminate = PublishSubject.create();
                @Override
                public void onError(Throwable error) {
                    terminate.onError(error);
                }

                @Override
                public void onComplete() {
                    terminate.onComplete();
                }

                @Override
                public void onConnect(ChannelConnection<Request, Response> conn) {
                    Observable.fromPublisher(requests)
                    .flatMap(r -> conn.query(r), resultMapper, Integer.MAX_VALUE)
                    .mergeWith(terminate)
                    .doOnCancel(() -> conn.close())
                    .subscribe(s);
                }
                
            });
        });
    }
    
    public static <Request, Response, U> Observable<U> concat(
            ChannelPublisher<Request, Response> channel, 
            Publisher<Request> requests, 
            BiFunction<? super Request, ? super Response, U> resultMapper) {
        return Observable.create(s -> {
            channel.subscribe(new ChannelSubscriber<Request, Response>() {
                final PublishSubject<U> terminate = PublishSubject.create();
                @Override
                public void onError(Throwable error) {
                    terminate.onError(error);
                }

                @Override
                public void onComplete() {
                    terminate.onComplete();
                }

                @Override
                public void onConnect(ChannelConnection<Request, Response> conn) {
                    Observable.fromPublisher(requests)
                    .concatMap(r -> Observable.fromPublisher(conn.query(r)).map(q -> resultMapper.apply(r, q)), 1)
                    .mergeWith(terminate)
                    .doOnCancel(() -> conn.close())
                    .subscribe(s);
                }
                
            });
        });
    }
    
    public static <Request, Response> Observable<Response> concat(
            ChannelPublisher<Request, Response> channel, 
            Publisher<Request> requests) {
        return Observable.create(s -> {
            channel.subscribe(new ChannelSubscriber<Request, Response>() {
                final PublishSubject<Response> terminate = PublishSubject.create();
                @Override
                public void onError(Throwable error) {
                    terminate.onError(error);
                }

                @Override
                public void onComplete() {
                    terminate.onComplete();
                }

                @Override
                public void onConnect(ChannelConnection<Request, Response> conn) {
                    Observable.fromPublisher(requests)
                    .concatMap(r -> conn.query(r), 1)
                    .mergeWith(terminate)
                    .doOnCancel(() -> conn.close())
                    .subscribe(s);
                }
                
            });
        });
    }
}
