# Micronaut Nimbus


## Custom Annotations

### 1. `@JsonCleanPrefix`

- **Purpose**: Removes a specified prefix from the annotated field's value.

- **Parameters**:

- `prefix`: The prefix to be cleaned from the field value.

- **Example Usage**:

```java

@JsonCleanPrefix(prefix = "user_")

private String userId;

```

- Input: `{ "userId": "user_12345" }`

- Output: `{ "userId": "12345" }`



### 2. `@JsonNestedTransform`

- **Purpose**: Applies transformations to nested fields in a JSON structure.

- **Parameters**:

- `path`: The dot-separated path to the nested field.

- **Example Usage**:

```java

@JsonNestedTransform(path = "address.home")

private AddressDto homeAddress;

```

- Input: `{ "address": { "home": { "street": "Main St" } } }`

- Output: `homeAddress` is populated with the nested structure.



### 3. `@JsonToLower`

- **Purpose**: Converts the value of the annotated field to lowercase.

- **Example Usage**:

```java

@JsonToLower

private String email;

```

- Input: `{ "email": "USER@EXAMPLE.COM" }`

- Output: `{ "email": "user@example.com" }`

### INPUT
``` JSON
    {
    "userId": "user_12345",
    "name": "john doe",
    "email": "John.Doe@Example.COM",
    "homeAddress": {
        "street": "123 Main Street",
        "city": "Hometown",
        "zipCode": "12345"
    },
    "officeAddress": {
        "street": "456 Corporate Ave",
        "city": "Business City",
        "zipCode": "67890"
    },
    "orders":
        {
            "orderId": "order_001",
            "items": {
                "name": "Laptop",
                "quantity": 1,
                "price": 1200.50
            },
            "totalAmount": 1252.48
        }
}
```

### Output

```Json
{
    "userId": "user_12345",
    "NAME": "john doe",
    "email": "John.Doe@Example.COM",
    "address": {
        "home": {
            "street": "123 Main Street",
            "city": "Hometown",
            "zipCode": "12345"
        },
        "office": {
            "street": "456 Corporate Ave",
            "city": "Business City",
            "zipCode": "67890"
        }
    },
    "test": {
        "order": {
            "ID": "order_001"
        }
    },
    "items": {
        "name": "Laptop",
        "quantity": 1,
        "price": 1200.5
    },
    "orders": {
        "totalAmount": 1252.48
    }
}
```

## JSON Performance Analysis Service

The `JsonPerformanceAnalysisService` benchmarks Gson and Jackson for three key aspects:



### 1. Execution Time

Measures the average, minimum, and maximum time taken to transform a payload.



### 2. Memory Usage

Estimates the memory consumption during transformation for both libraries.



### 3. Concurrency Handling

Evaluates performance under multi-threaded scenarios to understand how well each library handles parallel transformations.



## Security Considerations

### Gson vs. Jackson

- **Gson**:

- Simpler and more lightweight.

- **Security Risk**: Does not handle certain polymorphic deserialization scenarios as securely as Jackson.

- Less prone to issues from external configurations.



- **Jackson**:

- More feature-rich but can be more vulnerable if not configured securely.

- **Security Features**:

- Handles polymorphic deserialization securely with annotations like `@JsonTypeInfo`.

- Can be configured to prevent certain types of attacks (e.g., deserialization of malicious objects).



### Recommendations:

- Use Jackson for complex APIs but configure it carefully to prevent deserialization attacks.

- Use Gson for simpler use cases where performance is critical and security risks are minimal.



## Creative Benchmark Presentation

### Example Results:

| Metric                  | Gson (ms)  | Jackson (ms) | Difference |
|-------------------------|------------|--------------|------------|
| **Execution Time (avg)** | 15.3       | 20.1         | -24%       |
| **Execution Time (min)** | 12.1       | 18.4         | -34%       |
| **Execution Time (max)** | 18.7       | 25.0         | -25%       |

| Memory Usage (KB)       | Gson       | Jackson      |
|-------------------------|------------|--------------|
| **Memory (avg)**        | 120        | 140          |
| **Memory (min)**        | 110        | 130          |
| **Memory (max)**        | 125        | 145          |


### Concurrency Results:

| Threads | Total Tasks | Gson Time (ms) | Jackson Time (ms) |
|---------|-------------|----------------|-------------------|
| 10      | 50          | 550            | 780               |
| 20      | 100         | 1120           | 1550              |

### Visualization:

#### Execution Time Chart

```

Gson:    |||||||||||||||||

Jackson: |||||||||||||||||||||||

```

#### Memory Usage Chart

```

Gson:    |||||||||||||||

Jackson: |||||||||||||||||

```



### Interpreting Results:

- **Execution Time**: Gson consistently outperforms Jackson in transformation speed.

- **Memory Usage**: Gson has a smaller memory footprint, making it ideal for resource-constrained environments.

- **Concurrency**: Gson scales better under multi-threaded conditions but lacks advanced features.



## Usage Instructions

1. Annotate fields in your DTOs with the desired custom annotations.

2. Use the `JsonPerformanceAnalysisService` to analyze the performance of your JSON payload transformations.

```java

@Inject

private JsonPerformanceAnalysisService analysisService;



AnalysisResult result = analysisService.performAnalysis(inputPayload);

```

3. Review and optimize based on the generated benchmark results.



## Conclusion

This project demonstrates how to:

- Simplify data transformations using custom annotations.

- Measure and compare the performance of Gson and Jackson.

- Make informed decisions about library usage based on benchmarks and security considerations.
