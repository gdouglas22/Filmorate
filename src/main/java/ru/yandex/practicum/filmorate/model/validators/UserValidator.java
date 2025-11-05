package ru.yandex.practicum.filmorate.model.validators;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.annotations.*;

import java.lang.reflect.Field;
import java.time.LocalDate;

@Slf4j
public class UserValidator {

    public static boolean validate(User user) {
        boolean hasErrors = false;

        for (Field f : User.class.getDeclaredFields()) {
            f.setAccessible(true);
            try {
                Object value = f.get(user);

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

                NoSpaces ns = f.getAnnotation(NoSpaces.class);
                if (ns != null && value instanceof String s) {
                    if (s.contains(" ")) {
                        log.warn("{}: {}", f.getName(), ns.message());
                        hasErrors = true;
                    }
                }

                CorrectEmail es = f.getAnnotation(CorrectEmail.class);
                if (es != null && value instanceof String s) {
                    if (!s.contains("@") || s.startsWith("@") || s.endsWith("@")) {
                        log.warn("{}: {}", f.getName(), es.message());
                        hasErrors = true;
                    }
                }

                NotFuture nf = f.getAnnotation(NotFuture.class);
                if (nf != null && value != null) {
                    if (value instanceof LocalDate d) {
                        if (d.isAfter(LocalDate.now())) {
                            log.warn("{}: {}", f.getName(), nf.message());
                            hasErrors = true;
                        }
                    } else {
                        log.warn("{}: {} (поле не LocalDate)", f.getName(), nf.message());
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
