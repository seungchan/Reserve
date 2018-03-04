# Reservation API
1. System Requirements:
	Gradle 2.10+,
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
   
6. Sample Requests
	
	6.1. To create a reservation data, run the following in command line.
		   
	$ curl -i -X POST -H "Content-Type:application/json" -d "{  \"guestName\" : \"Frodo\",  \"dateFrom\" : \"2018-03-09\",\"dateTo\" : \"2018-03-10\",\"price\" : \"110\",\"state\" : \"F\"}" http://localhost:8080/reserve
		
	The response will be:

	HTTP/1.1 201 Created
	Server: Apache-Coyote/1.1
	Location: http://localhost:8080/people/1
	Content-Length: 0
	Date: Wed, 26 Feb 2014 20:26:55 GMT

	6.2. To get all reservation data, run the following.
	
	$ curl localhost:8080/reserve
	
	6.3. To get specific reservation data by id, run the following.
	
	$ curl localhost:8080/reserve
	
	6.4. To find all the custom queries, run the following.
	
	$ curl localhost:8080/reserve/search
	
	6.5. To use findByGuestName query, run the following.
	
	$ curl localhost:8080/reserve/search/findByGuestName?name=Frodo
	
	6.6. PUT, PATCH, and DELETE REST calls to either replace, update, or delete existing records.
	
	$ curl -i -X PUT -H "Content-Type:application/json" -d "{  \"guestName\" : \"Frodo\",  \"dateFrom\" : \"2018-03-09\",\"dateTo\" : \"2018-03-10\",\"price\" : \"110\",\"state\" : \"I\"}" localhost:8080/reserve/3
	
	$ curl -i -X PATCH -H "Content-Type:application/json" -d "{\"state\" : \"O\"}" localhost:8080/reserve/3
	
	$ curl -X DELETE localhost:8080/reserve/3
   
7. Any questions and comments to slee7411@yahoo.com
