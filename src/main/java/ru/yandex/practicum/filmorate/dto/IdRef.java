package ru.yandex.practicum.filmorate.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IdRef implements Comparable<IdRef> {
    private Short id;

    @Override
    public int compareTo(IdRef o) {
        Short a = this.id, b = (o == null ? null : o.id);
        if (a == null && b == null) return 0;
        if (a == null) return 1;
        if (b == null) return -1;
        return Short.compare(a, b);
    }
}
