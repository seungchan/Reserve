# Reservation API
1. System Requirements:
	Gradle 2.10+
	JDK 8
2. Running application
   Go to project root directory, and run the following.
   gradle bootRun
3. Running unit tests
   Go to project root directory, and run the following.
   gradle test
4. DataStore:
   In-memory H2 database
   Every restart of application, there will be new data and previous record will not persist.
5. Rate Limit for state update
   Use some of implementations from the followings.
   https://github.com/vladimir-bukhtoyarov/bucket4j
   https://gist.github.com/calo81/2071634
6. Any questions and comments to slee7411@yahoo.com
