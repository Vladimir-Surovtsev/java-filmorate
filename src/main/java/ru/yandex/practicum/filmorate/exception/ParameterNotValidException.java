package ru.yandex.practicum.filmorate.exception;

import lombok.Getter;

@Getter
public class ParameterNotValidException extends IllegalArgumentException {
    private final String parameter;
    private final String reason;

    public ParameterNotValidException(String parameter, String reason) {
        super("Неверно указано значение параметра [" + parameter + "]: " + reason);
        this.parameter = parameter;
        this.reason = reason;
    }
}
