package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.util.Map;

@RestControllerAdvice("ru.yandex.practicum.filmorate.controller")
@Slf4j

public class ExHandler {
    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationException(final RuntimeException e) {
        log.debug("Получен статус 400 Validation Exception {}", e.getMessage(), e);
        return Map.of("ERROR", "Ошибка валидации данных", "ErrorMessage", e.getMessage()
        );
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFoundException(final RuntimeException e) {
        log.debug("Получен статус 404 Not Found {}", e.getMessage(), e);
        return Map.of(
                "ERROR", "Искомый объект не найден", "ErrorMessage", e.getMessage()
        );
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleOtherException(final RuntimeException e) {
        log.debug("Получен статус 500 Internal Server Exception {}", e.getMessage(), e);
        return Map.of(
                "ERROR", "Возникло исключение", "ErrorMessage", e.getMessage()
        );
    }
}
