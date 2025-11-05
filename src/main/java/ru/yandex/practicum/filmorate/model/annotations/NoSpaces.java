package ru.yandex.practicum.filmorate.model.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface NoSpaces {
    String message() default "Поле не должно содержать пробелы";
}