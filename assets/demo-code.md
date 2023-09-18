# Unicode 转汉字

```java
public static String unicodeToString(String str) {
    Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");
    Matcher matcher = pattern.matcher(str);
    char ch;
    while (matcher.find()) {
        // group 6728
        String group = matcher.group(2);
        // ch:'木' 26408
        ch = (char) Integer.parseInt(group, 16);
        // group1 \u6728
        String group1 = matcher.group(1);
        str = str.replace(group1, ch + "");
    }
    return str;
}
```

# New HttpClient in Java

```java
// Read More: https://www.baeldung.com/java-9-http-client

// Creating a request
HttpRequest get = HttpRequest
    .newBuilder(new URI("https://.."))
    .version(HttpClient.Version.HTTP_2)
    .header("key1", "value1")
    .header("key2", "value2")
    .GET()  // POST(), PUT()...
    .build();

// Creating a HttpClient
HttpClient httpClient = HttpClient.newHttpClient();

// Sending the request
HttpResponse<String> response = httpClient.send(get, HttpResponse.BodyHandlers.ofString());
String body = response.body();

// Setting a proxy
HttpResponse<String> response = HttpClient
  .newBuilder()
  .proxy(ProxySelector.getDefault())
  .build()
  .send(request, BodyHandlers.ofString());
```

# Convert Between Java LocalDate and Epoch

```java
// Epoch to Date/Time
long milliSecondsSinceEpoch = 2131242L;
ZoneId zoneId = ZoneId.of("Europe/Amsterdam");
LocalDate date = Instant.ofEpochMilli(milliSecondsSinceEpoch).atZone(zoneId).toLocalDate();

LocalDateTime time = Instant.ofEpochMilli(milliSecondsSinceEpoch).atZone(zoneId).toLocalDateTime();


// Date/Time to Epoch
ZoneId zoneId = ZoneId.of("Europe/Tallinn");
LocalDate date = LocalDate.now();
long EpochMilliSecondsAtDate = date.atStartOfDay(zoneId).toInstant().toEpochMilli();
        
LocalDateTime localDateTime = LocalDateTime.parse("2019-11-15T13:15:30");
long epochMilliSecondsAtTime = time.atZone(zoneId).toInstant().toEpochMilli();
```
# Jackson ObjectMapper

```xml
<!-- dependencies -->
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-dataformat-xml</artifactId>
    <version>2.14.2</version>
</dependency>
```

```java
// Read More: https://www.baeldung.com/jackson-object-mapper-tutorial#Overview
public class Car {

    private String color;
    private String type;

    // standard getters setters
}


// Java Object to JSON
ObjectMapper objectMapper = new ObjectMapper();
Car car = new Car("yellow", "renault");
String carAsString = objectMapper.writeValueAsString(car);


// JSON to Java Object
String json = "{ \"color\" : \"Black\", \"type\" : \"BMW\" }";
Car car = objectMapper.readValue(json, Car.class);


// JSON to Jackson JsonNode
String json = "{ \"color\" : \"Black\", \"type\" : \"FIAT\" }";
JsonNode jsonNode = objectMapper.readTree(json);
String color = jsonNode.get("color").asText();
// Output: color -> Black


// Creating a Java List From a JSON Array String
String jsonCarArray = 
        "[{ \"color\" : \"Black\", \"type\" : \"BMW\" }, { \"color\" : \"Red\", \"type\" : \"FIAT\" }]";
List<Car> listCar = objectMapper.readValue(jsonCarArray, new TypeReference<List<Car>>(){});


// Creating Java Map From JSON String
String json = "{ \"color\" : \"Black\", \"type\" : \"BMW\" }";
Map<String, Object> map
        = objectMapper.readValue(json, new TypeReference<Map<String,Object>>(){});

```
