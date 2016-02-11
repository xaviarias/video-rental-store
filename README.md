Video Rental Store
==================

Exercise implemented as a JAX-RS API and in-memory storage.

Exposed API
-----------

* Dates and money are serialized in JSON as their corresponding ISO strings.

* Error codes are simplified as `202 (OK)`, `400 (Bad request)` and `404 (Not found)`,
  and reported as an array of JSON objects containing a message 
  and, for parameter validation, the path and the value of the offending parameter.

Decision rules
--------------

Prices and bonus decision rules are implemented using Camunda DMN in a per-currency basis.
To change the rules, go to http://demo.bpmn.io/dmn/new and load the file 
corresponding to the currency you want to apply the rules for, following the pattern:
`/video-rental-store/domain/src/main/resources/price-rules_<currency code>.dmn`.

Where `<currency code>` is a ISO three-letter currency code (e.g. 'SEK'). 
You can then replace the existing XML to see the policy changes.

In the DMN editor rules are visualized something like:

![Price decision rules](https://raw.githubusercontent.com/xaviarias/video-rental-store/master/screenshot.png =500x)

Other considerations
--------------------
* Model entities are not attached to any representation in form of JPA or JSON annotations.

* Date & Time handling is done by the class `java.time.Clock`, allowing alternate times during testing.
                
* Logging is handled by the SLF4J simple logger. JUL is also redirected to SLF4J.

* Bulk storage retrievals are optimized using Java 8 parallel streams.