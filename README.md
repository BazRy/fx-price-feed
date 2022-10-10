# fx-price-feed

Prerequisites
-------------
Java 11 or above
Maven 3.5.0 or above
Both the above need to be present on the path.

How to build
------------
Navigate to the project's root using a command line.
From the project root, run `mvn clean install`
The following should appear:

BUILD SUCCESS

How to run tests
----------------
Navigate to the project's root using a command line.
From the project root, run `mvn test`
The following should appear:

Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 1.081 s - in com.santander.pricing.process.PriceProcessorTest
Running com.santander.pricing.messaging.PriceListenerTest
Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.001 s - in com.santander.pricing.messaging.PriceListenerTest

Results:
Tests run: 2, Failures: 0, Errors: 0, Skipped: 0

Results:
Tests run: 19, Failures: 0, Errors: 0, Skipped: 0

BUILD SUCCESS

How to run application
-----------------------
There is a Spring Boot Application class that can be run is required.  This can be run from within any IDE.  
By way of a test,  it can will send 5 lines of csv to the jms queue which will then be processsed and the latest of each instrument will be preserved in a Map.
To view these Price objects, go to the following URL in any browser - http://localhost:8080/prices,  this will output the Json representation of the Price objects.
Please note that the tests will fail if the Application is running.


Assumptions/Observations/Shortcomings
-------------------------------------
1. Assumed that the data should be pushed to an endpoint and also be accessible via a get request
2. Only the latest price per instrument should be available, it's assumed the Price with the highest ID per instrument is the latest for that instrument (possibly check the price time also )
2. More fine-grained tests are required.  For example the PriceTransform class should be tested in isolation
3. The output displays the instrument as USD_JPY, for example, USD/JPY.  This should be fixed.