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
// Read more: https://www.baeldung.com/java-9-http-client

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
