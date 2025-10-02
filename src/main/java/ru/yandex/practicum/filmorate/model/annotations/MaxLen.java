package ru.yandex.practicum.filmorate.model.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface MaxLen {
    int value();

    String message() default "Превышена максимальная длина";
}
