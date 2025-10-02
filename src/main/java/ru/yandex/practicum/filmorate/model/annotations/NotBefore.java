package ru.yandex.practicum.filmorate.model.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface NotBefore {
    String value();

    String message() default "Дата слишком ранняя";
}