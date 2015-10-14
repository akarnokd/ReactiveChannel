reactive-streams-impl
==============

Non-fluent operators to work with org.reactivestreams interfaces.

Compilation and use (currently) requires Java 8 due to heavy lambda usage.

Implementation notes
--------------

The implementation uses non-blocking concurrency primitives such as [queue-drain serialization](http://akarnokd.blogspot.hu/2015/05/operator-concurrency-primitives_11.html), non-blocking queues and
copy-on-write data structures.

Releases
--------

<a href='https://travis-ci.org/akarnokd/reactive-streams-impl/builds'><img src='https://travis-ci.org/akarnokd/reactive-streams-impl.svg?branch=master'></a>