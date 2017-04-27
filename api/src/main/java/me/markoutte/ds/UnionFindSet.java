package me.markoutte.ds;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface UnionFindSet {

    /**
     * Добавить элемент x в систему множеств как единоличное множество
     */
    void set(int id);

    /**
     * Объединить элементы x и y. Метод вернёт идентификатор нового корня множества,
     * который не обязательно равен x или y; или вернёт -1, если объединить не получится.
     * @return идентификатор нового корня, после объединения x и y
     */
    int union(int left, int right);

    /**
     * Получить идентификатор сегмента, к которому принадлежит эелемент x.
     */
    int find(int id);

    /**
     * @return Количество сегментов
     */
    int size();

    /**
     * Спрямляет дерево
     */
    void compress();

}

