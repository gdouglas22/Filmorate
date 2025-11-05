package ru.yandex.practicum.filmorate.model.validators;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.annotations.*;

import java.lang.reflect.Field;
import java.time.LocalDate;

@Slf4j
public class FilmValidator {
    public static boolean validate(Film film) {
        boolean hasErrors = false;
        for (Field f : Film.class.getDeclaredFields()) {
            f.setAccessible(true);
            try {
                Object value = f.get(film);

                NotNull nn = f.getAnnotation(NotNull.class);
                if (nn != null && value == null) {
                    log.warn("{}: {}", f.getName(), nn.message());
                    hasErrors = true;
                    continue;
                }

                NotBlank nb = f.getAnnotation(NotBlank.class);
                if (nb != null) {
                    if (!(value instanceof String s) || s.trim().isEmpty()) {
                        log.warn("{}: {}", f.getName(), nb.message());
                        hasErrors = true;
                    }
                }

                MaxLen ml = f.getAnnotation(MaxLen.class);
                if (ml != null && value instanceof String s && s.length() > ml.value()) {
                    log.warn("{}: {} ({} > {})", f.getName(), ml.message(), s.length(), ml.value());
                    hasErrors = true;
                }

                Positive pos = f.getAnnotation(Positive.class);
                if (pos != null && value != null) {
                    if (value instanceof Number n) {
                        if (n.longValue() <= 0) {
                            log.warn(f.getName() + ": " + pos.message());
                            hasErrors = true;
                        }
                    } else {
                        log.warn("{}: {} (поле не число)", f.getName(), pos.message());
                        hasErrors = true;
                    }
                }

                NotBefore nbef = f.getAnnotation(NotBefore.class);
                if (nbef != null && value != null) {
                    if (value instanceof LocalDate d) {
                        LocalDate min = LocalDate.parse(nbef.value());
                        if (d.isBefore(min)) {
                            log.warn("{}: {} (минимум {})", f.getName(), nbef.message(), min);
                            hasErrors = true;
                        }
                    } else {
                        log.warn("{}: {} (поле не LocalDate)", f.getName(), nbef.message());
                        hasErrors = true;
                    }
                }

            } catch (IllegalAccessException e) {
                log.warn("{}: ошибка доступа {}", f.getName(), e.getMessage());
                hasErrors = true;
            }
        }
        return !hasErrors;
    }
}