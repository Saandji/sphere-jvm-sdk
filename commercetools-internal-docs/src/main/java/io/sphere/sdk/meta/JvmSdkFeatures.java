package io.sphere.sdk.meta;

import io.sphere.sdk.models.Base;

/**

 <h3>Why is it not called Java SDK?</h3>
 <p>Android also uses Java programming language but we use Java 8 language features and JRE 8 features which are not available on Android. The SDK should also be great for other languages on the JVM like Scala, Groovy, Clojure, Kotlin, etc.</p>



 <h3>Parallelity Features</h3>


 <div class="feature">
 {@include.example io.sphere.sdk.meta.JvmSdkFeaturesTest#asyncExecutionDemo()}

 <div>
 <h4>Non-blocking</h4>
 <p>API calls can take time and to save CPU cycles threads are not blocked.</p>

 </div>
 </div>


 <div class="feature">
 {@include.example io.sphere.sdk.meta.JvmSdkFeaturesTest#parallelExecutionDemo()}


 <div>
 <h4>Parallel execution</h4>
 For high performance you should parallelize as much requests as possible.
 By using {@link java.util.concurrent.CompletionStage} from Java it is easy to start parallel asynchronous
 calls and combine them into a single {@link java.util.concurrent.CompletionStage}.

 </div>
 </div>


 <div class="feature">
 {@include.example io.sphere.sdk.meta.JvmSdkFeaturesTest#recoverDemo()}

 <div>
 <h4>Recover from Exceptions</h4>
 <p>API requests can fail due to network errors and other sources. With the JVM SDK it is easy to code a plan B instead of crashing.</p>

 </div>
 </div>



 <div class="feature">
 <img src="{@docRoot}/resources/images/features/scala-futures.png" alt="">


 <div>
 <h4>Other future implementations (Scala!)</h4>
 <p>There add-ons to support other Future implementations such as Scalas Future (2.10, 2.11, 2.12) and Play Frameworks F.Promise (2.2, 2.3, 2.4).
 We also plan to support Spring, Rx and Reactive Streams.</p>

 </div>
 </div>

 <h3>Library Features</h3>

 <div class="feature">
 {@include.example io.sphere.sdk.meta.JvmSdkFeaturesTest#moneyDemo()}


 <div>
 <h4>Java Money</h4>
 <p>The SDK uses the Java Money library which makes it easy to retrieve
 currencies and format monetary amounts for specific locales.</p>

 </div>
 </div>

 <div class="feature">
 {@include.example io.sphere.sdk.meta.JvmSdkFeaturesTest#dateTimeDemo()}


 <div>
 <h4>Java 8 time classes</h4>
 <p>In the SPHERE.IO models are the Java time classes used and they can conveniently formatted for specific time zones.</p>

 </div>
 </div>


 <div class="feature">
 {@include.example io.sphere.sdk.meta.JvmSdkFeaturesTest#countryCodeDemo()}


 <div>
 <h4>Country codes</h4>
 <p>In the API country codes are represented as Strings, in the JVM SDK models it is {@link com.neovisionaries.i18n.CountryCode} from the nv-i18n library.
 With the library you can format the country name according to a locale.</p>

 </div>
 </div>

 <h3>Logging</h3>
 <p>For logging is SLF4J used.</p>

 <div class="feature">
 <img src="{@docRoot}/resources/images/features/fine-granular-log-levels.png" alt="">


 <div>
 <h4>Granular logging</h4>
 <p>For each commercetools platform resource you can specify a custom log level.
 More over it is possible to set the level for request or response objects and fine-tune them for read and write access to the API.
 (Image shows example config with logback.)</p>

 </div>
 </div>

 <div class="feature">
 <img src="{@docRoot}/resources/images/features/object-logging.png" alt="">


 <div>
 <h4>Debug level logs the Java objects</h4>
 <p>With the Java objects you can analyse the data.</p>

 </div>
 </div>

 <div class="feature">
 <img src="{@docRoot}/resources/images/features/trace-pretty-printed-json.png" alt="">


 <div>
 <h4>Trace level logs the the JSON objects in a pretty printed way</h4>
 <p>This way you can directly analyze what is sent or get from the commercetools platform HTTP API.</p>

 </div>
 </div>

 <h3>Domain Specific Languages for creating requests</h3>

 <div class="feature"> {@include.example io.sphere.sdk.meta.JvmSdkFeaturesTest#productSearchDemo()}
     <div>
         <h4>Product Search</h4>
         <p>Product Search is one of the most important parts of a web shop and with the JVM SDK you will be supported to create
         powerfull requests.</p>
     </div>
 </div>
 <div class="feature"> {@include.example io.sphere.sdk.meta.JvmSdkFeaturesTest#queryDemo()}
     <div>
         <h4>Query</h4>
         <p>Creating queries for deep nested objects works like a charm.</p>
     </div>
 </div>
 <div class="feature"> {@include.example io.sphere.sdk.meta.JvmSdkFeaturesTest#referenceExpansion()}
     <div>
         <h4>Reference Expansion</h4>
         <p>Fetching multiple objects in in request can be done by reference expansion.</p>
     </div>
 </div>
 <div class="feature"> {@include.example io.sphere.sdk.meta.JvmSdkFeaturesTest#typeSafe()}
     <div>
         <h4>Type-safety</h4>
         <p>In a useful way objects are typed and so the compiler checks for some copy paste errors.</p>
     </div>
 </div>
 <div class="feature"> {@include.example io.sphere.sdk.meta.JvmSdkFeaturesTest#stringFallback()}
     <div>
         <h4>Flexibility above the JVM SDK core</h4>
         <p>Even if a predicate, an expansion path or a sort expression is not yet supported in the SDK String expressions can be used as fallback.</p>
     </div>
 </div>
 <div class="feature"> {@include.example io.sphere.sdk.meta.JvmSdkFeaturesTest#immutability()}
     <div>
         <h4>Immutability</h4>
         <p>Immutable objects can be freely shared in a multi-threaded environment.
 The JVM SDK resource types are immutable by default. Also typical requests are immutable and
 provide an API to create adjusted copies which fit well in a functional programming style.</p>
     </div>
 </div>

 <h3>Modularity</h3>

 <div class="feature"> {@include.example io.sphere.sdk.meta.JvmSdkFeaturesTest#justAuth()}
 <div>
 <h4>It is possible to just use the SDK to get an access token</h4>
 <p>The JVM SDK enables you to fetch an access token for a commercetools platform project.</p>
 </div>
 </div>

 <div class="feature"> {@include.example io.sphere.sdk.meta.JvmSdkFeaturesTest#jsonModels()}
 <div>
 <h4>Java models not required but provided</h4>
 <p>If the Java models don't fit you use cases you can create your own model classes or just use directly JSON in Java.</p>
 </div>
 </div>


 <div class="feature"> {@include.example io.sphere.sdk.meta.JvmSdkFeaturesTest#testDoubleForRequest()}
 <div>
 <h4>Testable request and response handling</h4>
 <p>If the Java models don't fit you use cases you can create your own model classes or just use directly JSON in Java.</p>
 </div>
 </div>

 <div class="feature"> {@include.example io.sphere.sdk.meta.JvmSdkFeaturesTest#replaceableHttpClient()}
 <div>
 <h4>JVM SDK supports multiple HTTP client implementation for compatibility and speed</h4>
 <p>Async HTTP client is a very fast and verbose HTTP client but has problems in some projects concerning compatibility of this library and to the netty library.
 This can be solved by picking a specific version or fall back to the Apache HTTP client. Also it is possible to configure the underlying client for example to deal with proxies.</p>
 </div>
 </div>

 <!--

 ## Modularity

 * custom objects with pojo or your preferred JSON mapper

 ## Great documentation

 * Javadoc
 * tutorials
 * great error reports, show example exception, so details and link to javadoc with suggestions to recover
 * code examples in Javadoc, compiled and integration tested
 * UML, show exception example (partial diagram)
 * atNullable annotation, show example with yellow IDE markings, that a value could be null
 * big release notes
 * hosted on maven central\
 * toString, similar hashCode and equals

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
