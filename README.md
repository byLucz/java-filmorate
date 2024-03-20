# java-filmorate

Amazing Filmorate v0.3

Scheme powered by dbdiagram.io
![FILMORATE](https://github.com/byLucz/java-filmorate/assets/72922298/7a45424f-a0d8-47d5-9f96-e143cacff40b)


## Usage/Examples

Get all friends of user with a specific ID:
```sql
SELECT U.name
FROM User AS U
JOIN Friendship AS F ON U.id = F.friend_id
WHERE F.user_id = <user_id>;
```
Find all films liked by a specific user:

```sql
SELECT F.name, F.description
FROM Film AS F
JOIN Like AS L ON F.id = L.film_id
WHERE L.user_id = <user_id>;
```
Retrieve the names of users who have not liked any films released in the current year:
```sql
SELECT U.name
FROM User AS U
WHERE NOT EXISTS (
    SELECT 1
    FROM Like AS L
    JOIN Film AS F ON L.film_id = F.id
    WHERE L.user_id = U.id AND EXTRACT(YEAR FROM F.release_date) = EXTRACT(YEAR FROM CURRENT_DATE)
);
```
