package fr.flowarg.flowcollections;

import java.util.ArrayList;
import java.util.List;

public class ListHelper
{
    public static <E> List<E> of()
    {
        return new ArrayList<>();
    }

    public static <E> List<E> of(E e1)
    {
        final List<E> res = of();
        res.add(e1);
        return res;
    }

    public static <E> List<E> of(E e1, E e2)
    {
        final List<E> res = of(e1);
        res.add(e2);
        return res;
    }

    public static <E> List<E> of(E e1, E e2, E e3)
    {
        final List<E> res = of(e1, e2);
        res.add(e3);
        return res;
    }

    public static <E> List<E> of(E e1, E e2, E e3, E e4)
    {
        final List<E> res = of(e1, e2, e3);
        res.add(e4);
        return res;
    }

    public static <E> List<E> of(E e1, E e2, E e3, E e4, E e5)
    {
        final List<E> res = of(e1, e2, e3, e4);
        res.add(e5);
        return res;
    }

    public static <E> List<E> of(E e1, E e2, E e3, E e4, E e5, E e6)
    {
        final List<E> res = of(e1, e2, e3, e4, e5);
        res.add(e6);
        return res;
    }

    public static <E> List<E> of(E e1, E e2, E e3, E e4, E e5, E e6, E e7)
    {
        final List<E> res = of(e1, e2, e3, e4, e5, e6);
        res.add(e7);
        return res;
    }

    public static <E> List<E> of(E e1, E e2, E e3, E e4, E e5, E e6, E e7, E e8)
    {
        final List<E> res = of(e1, e2, e3, e4, e5, e6, e7);
        res.add(e8);
        return res;
    }

    public static <E> List<E> of(E e1, E e2, E e3, E e4, E e5, E e6, E e7, E e8, E e9)
    {
        final List<E> res = of(e1, e2, e3, e4, e5, e6, e7, e8);
        res.add(e9);
        return res;
    }

    public static <E> List<E> of(E e1, E e2, E e3, E e4, E e5, E e6, E e7, E e8, E e9, E e10)
    {
        final List<E> res = of(e1, e2, e3, e4, e5, e6, e7, e8, e9);
        res.add(e10);
        return res;
    }
}
