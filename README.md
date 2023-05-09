# studentsApp
 Unfortunately the integration test I wrote does not work properly
# API Endpoints

The following endpoints can be reached through Postman

## `/list`

### `GET`

Retrieve a list of all Students.

## `/add`

### `POST`

Create a new user.

#### Request Parameters

```json
{
  "name": "string",
  "email": "string with a valid e-mail format"
}
```

## `/update`

### `PUT`

Modify an existing user

#### Request Parameters

```json
{
  "id":"UUID",
  "name": "string",
  "email": "string with a valid e-mail format"
}
```

## `/delete/{id}`

Delete the student with the provided UUID



