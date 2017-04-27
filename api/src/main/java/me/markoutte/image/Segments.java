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
     * Возвращает список идентификторов сегментов
     */
    int[] roots();

    /**
     * Возвращает список точек сегмента, включая сам корень.
     * 
     * Верно, что {@code pixels(id)[0] == id}.
     * 
     * @param id идентификтора сегмента
     */
    int[] pixels(int id);
    
}
