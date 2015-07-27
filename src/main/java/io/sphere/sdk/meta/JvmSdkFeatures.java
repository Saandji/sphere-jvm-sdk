package io.sphere.sdk.meta;

import io.sphere.sdk.models.Base;

/**

 <h3>Why is it not called Java SDK?</h3>
 <p>Android also uses Java programming language but we use Java 8 language features and JRE 8 features which are not available on Android. The SDK should also be great for other languages on the JVM like Scala, Groovy, Clojure, Kotlin, etc.</p>



<!--

 ## Parallel execution

 * Java 8 {@link java.util.concurrent.CompletionStage}
 * show example for two requests and then collect their results
 * exception handling: show recover with completionstage
 * ecosystem friendly, Scala 2.10, 2.11, 2.13; Play 2.2, Play 2.3, Play 2.4, Spring (planned), Rx (planned), Reactive Streams (WIP)

 ## Libs

 * Java money, show automatic formatting for country
 * Java 8 datetime, show rendering for timezones
 * Country code, display country (requires language)
 * SLF4J, show cool fine granulated logging per endpoint and direction

## SPHERE.IO Embedded Domain Specific Language

 * Search
 * Query
 * Sort
 * reference expansion + Expansion paths
 * Typed models and predicates/sort etc. harder to make copy/paste errors
 * fallbacks using Strings like in HTTP API
 * immutable data, good for caching, functional programming within Java and Scala
 * Scala addons, show example for predicate and sort without parenthesis

 ## Modularity

 * just use SPHERE.IO client for authentication
 * use SDK with given models or it is possible to create own model representations and use custom JSON mappers
 * testable client with fake return objects and JSON dumps
 * custom objects with pojo or your preferred JSON mapper
 * replaceable http client, tweakable concerning timeouts and parallelity

 ## Great documentation

 * Javadoc
 * tutorials
 * great error reports, show example exception, so details and link to javadoc with suggestions to recover
 * code examples in Javadoc, compiled and integration tested
 * UML, show exeption example (partial diagram)
 * atNullable annotation, show example with yellow IDE markings, that a value could be null
 * big release notes
 * hosted on maven central

 -->


<!--
<h3>Embracing Java 8</h3>
 <p>The SDK API uses:</p>
 <ul>
    <li></li>
    <li>{@link java.util.Optional}</li>
    <li>Java Date API: {@link java.time.ZonedDateTime}, {@link java.time.LocalDate} and {@link java.time.LocalTime}</li>
    <li>{@link java.util.function.Function}</li>
 </ul>

<h3>Good defaults for toString(), equals() and hashCode()</h3>
<p>The SDK's implementation classes extend {@link Base} which provides default implementations for the methods by using
 reflection following the suggestions of
 <a href="http://www.oracle.com/technetwork/java/effectivejava-136174.html">Effective Java</a>.</p>

<h3>Domain models are immutable</h3>
<p>Domain models are no plain old Java objects since the client does not pose control over them, but needs to send
 update commands to SPHERE.IO. Thus setters, as provided by <em>other</em> cloud services are not applicable in SPHERE.IO.</p> 
 <p>The approach to synchronize the model in the background blocks the caller thread and makes it hard to impossible to tune error handling, timeouts and recover strategies.
 Our approach makes it explicit, that an operation can be performed in the background, that the operation takes time and that the operation might fail.</p>

 <h3>Domain models are interfaces</h3>
<p>Since domain models are interfaces you can use them in design patterns of your choice or to add convenience methods.</p>

 <h3>Testability</h3>
 <p>Since the clients and the models are interfaces they can be replaced with test doubles.
 In addition the SDK provides builders and JSON converters to create models for unit tests.</p>

 <h3>Domain specific languages to create requests</h3>
 <p>For example, {@link io.sphere.sdk.queries.QueryDsl} assists in formulating valid queries and to find out which attributes can be used in which way for querying and sorting.</p>
-->
 */
public final class JvmSdkFeatures extends Base {
    private JvmSdkFeatures() {
    }
}
