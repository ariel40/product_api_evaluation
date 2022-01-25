= README!!

This is and evaluation test that consist in a simple REST service that provides a CRUD RESTful endpoint protected by OAuth 2. 

== Spring Projects

The following Spring projects are used in this evaluation project:

* http://projects.spring.io/spring-boot/[Spring Boot]
* http://docs.spring.io/spring/docs/current/spring-framework-reference/html/mvc.html[Spring MVC]
* http://projects.spring.io/spring-security/[Spring Security]
* http://projects.spring.io/spring-security-oauth/[Spring Security OAuth]
* http://projects.spring.io/spring-data-jpa/[Spring Data JPA]
* http://projects.spring.io/spring-batch/[Spring Batch]

## Requirements
This demo is build with with Maven 3.4.x and Java 8.

== Build and Run

Maven:

```sh
mvn clean package spring-boot:run
```

== Usage

You can use the H2-Console for exploring the database under http://localhost:8080/h2-console:

The credentials for the database connection. please see. application.yml

## Security Level
There are three user accounts present to demonstrate the different levels of access to the endpoints in
the API and the different authorization exceptions:
```
Roque - roque:roque  -> ROLE: PRODUCT_PRICING
George - george:george -> ROLE: PRODUCT_MANAGERS
Anibal - anibal:anibal -> ROLE: PRODUCT_CREATORS

```sh
curl http://localhost:8081/api/v1/product/1
```

You receive the following JSON response, which indicates you are not authorized to access the resource:

```json
{
    "error": "access_denied",
    "error_description": "Access is denied"
}
```

In order to access the protected resource, you must first request an access token via the OAuth handshake. Request OAuth authorization:

```sh
curl -X POST -u bravo_client:bravo_secret http://localhost:8081/oauth/token -H "Accept: application/json" -d "password=anibal&username=anibal&grant_type=password&scope=read%20write"
```

A successful authorization results in the following JSON response:

```json
{
    "access_token": "bf1a21c7-79c6-487d-a714-9d52c3268682",
    "token_type": "bearer",
    "refresh_token": "8c99db0b-0d2a-49cb-b61b-daee3370ed14",
    "expires_in": 119,
    "scope": "read write trust"
}
```

Use the `access_token` returned in the previous request to make the authorized request to the protected endpoint:

```sh
curl http://localhost:8081/api/v1/product -H "Authorization: Bearer bf1a21c7-79c6-487d-a714-9d52c3268682"
```

If the request is successful, you will see the following JSON response:

```json
[
{
"id":1,
"description":"Malta India 12 oz",
"creationDate":"2022-01-25T11:13:47.380Z",
"modificationDate":null,
"status":"A",
"prices":[{
        "id": 1,
        "creationDate":"2022-01-25T11:13:47.380Z",
        "modificationDate": null, 
        "price": 32.55,
        "status": "A"
    }]
},
{
"id":2,
"description":"Malta India 10 oz",
"creationDate":"2022-01-25T11:13:49.380Z",
"modificationDate":"2022-01-25T11:13:55.380Z",
"status":"A",
"prices":[{
        "id": 2,
        "creationDate":"2022-01-25T11:13:47.380Z",
        "modificationDate": null, 
        "price": 32.55,
        "status": "A"
    }]
},
]
```

After the specified time period, the `access_token` will expire. Use the `refresh_token` that was returned in the original OAuth authorization to retrieve a new `access_token`:

```sh
curl -X POST -u bravo_client:bravo_secret http://localhost:8081/oauth/token -H "Accept: application/json" -d "grant_type=refresh_token&refresh_token=f554d386-0b0a-461b-bdb2-292831cecd57"
```
Request to create a new product

```sh
curl -X POST http://localhost:8081/api/v1/product -H "Authorization: Bearer 2b0aa3fb-d9a7-4f5f-a706-a45a3e0ce578" \
  --header 'Content-Type: application/json' \
  --data "{
    "description": "Malta India 12 oz", \
    "status": "A", \
    "prices":[{ \
        "price": 32.55, \
        "status": "A" \
    }] \
}" \
  -w "\n"
```

response:

```json
{
    "id": 1,
    "description": "Malta India 12 oz",
    "creationDate": "2022-01-25T11:13:47.380Z",
    "modificationDate": null,
    "status": "A",
    "prices":[{
        "id": 2,
        "creationDate":"2022-01-25T11:13:47.380Z",
        "modificationDate": null, 
        "price": 32.55,
        "status": "A"
    }]
}

```

Request to get one product by id

```sh
curl http://localhost:8081/api/v1/product/1 -H "Authorization: Bearer 2b0aa3fb-d9a7-4f5f-a706-a45a3e0ce578"
```

Response:

```json
{
    "id": 1,
    "description": "Malta India 12 oz",
    "creationDate": "2022-01-25T11:13:47.380Z",
    "modificationDate": null,
    "status": "A",
    "prices":[{
        "id": 2,
        "creationDate":"2022-01-25T11:13:47.380Z",
        "modificationDate": null, 
        "price": 32.55,
        "status": "A"
    }]
}
```

Request to update one existing product.

```sh
curl -X POST http://localhost:8081/api/v1/product/1 -H "Authorization: Bearer 2b0aa3fb-d9a7-4f5f-a706-a45a3e0ce578" \
  --header 'Content-Type: application/json' \
  --data "{
    "description": "Malta India 12 oz", \
    "status": "A", \
    "prices":[{ \
        "price": 25.55, \
        "status": "A" \
    }] \
}" \
  -w "\n"
```

Response:

```json
{
    "id": 1,
    "description": "Malta India 12 oz",
    "creationDate": "2022-01-25T11:13:47.380Z",
    "modificationDate": null,
    "status": "A",
    "prices":[{
        "id": 2,
        "creationDate":"2022-01-25T11:13:47.380Z",
        "modificationDate": "2022-01-25T11:14:47.396Z", 
        "price": 32.55,
        "status": "A"
    }]
}
```

Request to delete one existing product.

```sh
curl -X DELETE http://localhost:8081/api/v1/product/1 -H "Authorization: Bearer 2b0aa3fb-d9a7-4f5f-a706-a45a3e0ce578"
```

Response:

HttpStatus: 200

Request to create a new price

```sh
curl -X POST http://localhost:8081/api/v1/price -H "Authorization: Bearer 2b0aa3fb-d9a7-4f5f-a706-a45a3e0ce578" \
  --header 'Content-Type: application/json' \
  --data "{
       "price": 32.55, \
        "status": "A" \
        "productId": 1
}" \
  -w "\n"
```

response:

```json
{
    "id": 2,
    "product": 1,
    "creationDate": "2022-01-25T12:02:42.743Z",
    "modificationDate": null,
    "price": 24.33,
    "status": "A"
}

```

Request to get the list of prices.

```sh
curl http://localhost:8081/api/v1/price/ -H "Authorization: Bearer 2b0aa3fb-d9a7-4f5f-a706-a45a3e0ce578"
```

Response:

```json
[
    {
        "id": 1,
        "product": 1,
        "creationDate": null,
        "modificationDate": null,
        "price": 32.55,
        "status": "A"
    },
    {
        "id": 2,
        "product": 2,
        "creationDate": "2022-01-25T12:02:42.743Z",
        "modificationDate": null,
        "price": 24.33,
        "status": "A"
    }
]
```

Request to get one price by id

```sh
curl http://localhost:8081/api/v1/price/1 -H "Authorization: Bearer 2b0aa3fb-d9a7-4f5f-a706-a45a3e0ce578"
```

Response:

```json
{
    "id": 2,
    "product": 2,
    "creationDate": "2022-01-25T12:02:42.743Z",
    "modificationDate": null,
    "price": 24.33,
    "status": "A"
}
```

Request to update one existing price.

```sh
curl -X POST http://localhost:8081/api/v1/price/1 -H "Authorization: Bearer 2b0aa3fb-d9a7-4f5f-a706-a45a3e0ce578" \
  --header 'Content-Type: application/json' \
  --data "{
   "prices":{ \
        "id": 2
        "price": 33.33, \
        "status": "A" \
    } \
  -w "\n"
```

Response:

```json
{
    "id": 2,
    "creationDate":"2022-01-25T11:13:47.380Z",
    "modificationDate": "2022-01-25T11:14:47.396Z", 
    "price": 33.33,
    "status": "A"
}
```

Request to delete one existing price.

```sh
curl -X DELETE http://localhost:8081/api/v1/price/1 -H "Authorization: Bearer 2b0aa3fb-d9a7-4f5f-a706-a45a3e0ce578"
```

Response:

HttpStatus: 200

