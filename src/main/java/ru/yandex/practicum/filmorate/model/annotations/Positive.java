package ru.yandex.practicum.filmorate.model.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Positive {
    String message() default "Число должно быть положительным";
}
