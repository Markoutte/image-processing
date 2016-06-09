package me.markoutte.ds;

import me.markoutte.image.Image;
import me.markoutte.image.Pixel;

import java.util.List;
import java.util.Set;

public interface Hierarchy {

    /**
     * Задаёт исходное изображение для иерархии. Метод должен следить
     * за очисткой иерархии после его вызова. Уровень для него считается равным 0
     */
    public void setImage(Image image);

    /**
     * Возвращает исходное изображение
     */
    public Image getSourceImage();

    /**
     * Задаёт исходное изображение для иерархии. Метод должен следить
     * за очисткой иерархии после его вызова. level считается исходным
     */
    public void setImage(Image image, int level);

    /**
     * Возращает изображение с заданного уровня
     */
    public Image getImage(double level, PseudoColorizeMethod colorize);

    /**
     * Объединить 2 сегмента в один
     * @return true в случае, если точка была успешна добавлена
     */
    public int union(int segment1, int segment2, double level);

    /**
     * Возвращает список пикселей из иерархии с уровня level,
     * заданного идентификатором сегмента id
     */
    public List<Pixel> getArea(int id, int level);

    /**
     * Возвращает идентификатор точки px с уровня level
     */
    public int getSegment(int id, double level);

    public Set<Integer> getSegments(double level);

}

