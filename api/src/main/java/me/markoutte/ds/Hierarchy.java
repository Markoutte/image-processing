package me.markoutte.ds;

import me.markoutte.image.Image;
import me.markoutte.image.Pixel;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface Hierarchy {

    /**
     * Задаёт исходное изображение для иерархии. Метод должен следить
     * за очисткой иерархии после его вызова. Уровень для него считается равным 0
     */
    void setImage(Image image);

    /**
     * Возвращает исходное изображение
     */
    Image getSourceImage();

    /**
     * Объединить 2 сегмента в один
     * @return true в случае, если точка была успешна добавлена
     */
    int union(int segment1, int segment2, double level);

    /**
     * Возвращает список пикселей из иерархии с уровня level,
     * заданного идентификатором сегмента id
     */
    List<Pixel> getArea(int id, int level);

    /**
     * Возвращает идентификатор точки px с уровня level
     */
    int getSegment(int id, double level);

    /**
     * Возвращает границы уровня сегментации.
     *
     * Для метода всегда верно, что:
     * <ul>
     *     <li>{@code getLevelBounds().length == 2}</li>
     *     <li>{@code getLevelBounds()[0] <= getLevelBounds()[1]</li>
     * </ul>
     */
    double[] getLevelBounds();

}

