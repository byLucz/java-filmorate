MERGE INTO RATING AS target
USING (VALUES ('G', 'Фильм демонстрируется без ограничений'),
              ('PG', 'Детям рекомендуется смотреть фильм с родителями'),
              ('PG-13', 'Просмотр не желателен детям до 13 лет'),
              ('R', 'Лица, не достигшие 17-летнего возраста, допускаются на фильм только в сопровождении одного из родителей, либо законного представителя'),
              ('NC-17', 'Лица 17-летнего возраста и младше на фильм не допускаются')) AS source(RATING_CODE, DESCRIPTION)
ON target.RATING_CODE = source.RATING_CODE
WHEN NOT MATCHED THEN
INSERT (RATING_CODE, DESCRIPTION) VALUES (source.RATING_CODE, source.DESCRIPTION);

MERGE INTO GENRE AS target
USING (VALUES ('Комедия'),
              ('Драма'),
              ('Мультфильм'),
              ('Триллер'),
              ('Документальный'),
              ('Боевик')) AS source(NAME)
ON target.NAME = source.NAME
WHEN NOT MATCHED THEN
INSERT (NAME) VALUES (source.NAME);