package me.markoutte.image;

/**
 * Pelevin Maksim <maks.pelevin@oogis.ru>
 *
 * @since 2017/04/27
 */
public interface Segments {

    /**
     * Возвращает количество сегментов
     */
    int size();

    /**
     * Возвращает идентификтор сегмента.
     */
    int root(int index);

    /**
     * Возвращает список точек сегмента, включая сам корень.
     *
     * @param index индекс сегмента, может принимать значение от 0 и до size().
     */
    int[] pixels(int index);
    
}
