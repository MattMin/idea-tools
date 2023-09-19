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

# Java String Conversions

```java
// Converting String to int or Integer
@Test
public void whenConvertedToInt_thenCorrect() {
    String beforeConvStr = "1";
    int afterConvInt = 1;

    assertEquals(Integer.parseInt(beforeConvStr), afterConvInt);
}

@Test
public void whenConvertedToInteger_thenCorrect() {
    String beforeConvStr = "12";
    Integer afterConvInteger = 12;
    
    assertEquals(Integer.valueOf(beforeConvStr).equals(afterConvInteger), true);
}
        

// Converting String to long or Long
@Test
public void whenConvertedTolong_thenCorrect() {
    String beforeConvStr = "12345";
    long afterConvLongPrimitive = 12345;

    assertEquals(Long.parseLong(beforeConvStr), afterConvLongPrimitive);
}

@Test
public void whenConvertedToLong_thenCorrect() {
    String beforeConvStr = "14567";
    Long afterConvLong = 14567l;

    assertEquals(Long.valueOf(beforeConvStr).equals(afterConvLong), true);
}


// Converting String to double or Double
@Test
public void whenConvertedTodouble_thenCorrect() {
    String beforeConvStr = "1.4";
    double afterConvDoublePrimitive = 1.4;

    assertEquals(Double.parseDouble(beforeConvStr), afterConvDoublePrimitive, 0.0);
}

@Test
public void whenConvertedToDouble_thenCorrect() {
    String beforeConvStr = "145.67";
    double afterConvDouble = 145.67d;

    assertEquals(Double.valueOf(beforeConvStr).equals(afterConvDouble), true);
}


// Converting String to ByteArray
@Test
public void whenConvertedToByteArr_thenCorrect() {
    String beforeConvStr = "abc";
    byte[] afterConvByteArr = new byte[] { 'a', 'b', 'c' };

    assertEquals(Arrays.equals(beforeConvStr.getBytes(), afterConvByteArr), true);
}


// Converting String to CharArray
@Test
public void whenConvertedToCharArr_thenCorrect() {
    String beforeConvStr = "hello";
    char[] afterConvCharArr = { 'h', 'e', 'l', 'l', 'o' };

    assertEquals(Arrays.equals(beforeConvStr.toCharArray(), afterConvCharArr), true);
}


// Converting String to boolean or Boolean
@Test
public void whenConvertedToboolean_thenCorrect() {
    String beforeConvStr = "true";
    boolean afterConvBooleanPrimitive = true;

    assertEquals(Boolean.parseBoolean(beforeConvStr), afterConvBooleanPrimitive);
}

@Test
public void whenConvertedToBoolean_thenCorrect() {
    String beforeConvStr = "true";
    Boolean afterConvBoolean = true;

    assertEquals(Boolean.valueOf(beforeConvStr), afterConvBoolean);
}


// Converting String to Date or LocalDateTime
// Ream This: https://www.baeldung.com/java-8-date-time-intro


// Converting String to java.util.Date
@Test
public void whenConvertedToDate_thenCorrect() throws ParseException {
    String beforeConvStr = "15/10/2013";
    int afterConvCalendarDay = 15;
    int afterConvCalendarMonth = 9;
    int afterConvCalendarYear = 2013;
    SimpleDateFormat formatter = new SimpleDateFormat("dd/M/yyyy");
    Date afterConvDate = formatter.parse(beforeConvStr);
    Calendar calendar = new GregorianCalendar();
    calendar.setTime(afterConvDate);

    assertEquals(calendar.get(Calendar.DAY_OF_MONTH), afterConvCalendarDay);
    assertEquals(calendar.get(Calendar.MONTH), afterConvCalendarMonth);
    assertEquals(calendar.get(Calendar.YEAR), afterConvCalendarYear);
}


// Converting String to java.time.LocalDateTime
@Test
public void whenConvertedToLocalDateTime_thenCorrect() {
    String str = "2007-12-03T10:15:30";
    int afterConvCalendarDay = 03;
    Month afterConvCalendarMonth = Month.DECEMBER;
    int afterConvCalendarYear = 2007;
    LocalDateTime afterConvDate
    = new UseLocalDateTime().getLocalDateTimeUsingParseMethod(str);

    assertEquals(afterConvDate.getDayOfMonth(), afterConvCalendarDay);
    assertEquals(afterConvDate.getMonth(), afterConvCalendarMonth);
    assertEquals(afterConvDate.getYear(), afterConvCalendarYear);
}
```
