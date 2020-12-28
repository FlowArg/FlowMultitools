package fr.flowarg.flowcollections;

import java.util.HashMap;
import java.util.Map;

public class MapHelper
{
    public static <K, V> Map<K, V> of()
    {
        return new HashMap<>();
    }

    public static <K, V> Map<K, V> of(K k1, V v1)
    {
        final Map<K, V> res = of();
        res.put(k1, v1);
        return res;
    }

    public static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2)
    {
        final Map<K, V> res = of(k1, v1);
        res.put(k2, v2);
        return res;
    }

    public static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3)
    {
        final Map<K, V> res = of(k1, v1, k2, v2);
        res.put(k3, v3);
        return res;
    }

    public static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4)
    {
        final Map<K, V> res = of(k1, v1, k2, v2, k3, v3);
        res.put(k4, v4);
        return res;
    }

    public static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5)
    {
        final Map<K, V> res = of(k1, v1, k2, v2, k3, v3, k4, v4);
        res.put(k5, v5);
        return res;
    }

    public static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6)
    {
        final Map<K, V> res = of(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5);
        res.put(k6, v6);
        return res;
    }

    public static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6, K k7, V v7)
    {
        final Map<K, V> res = of(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5, k6, v6);
        res.put(k7, v7);
        return res;
    }

    public static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6, K k7, V v7, K k8, V v8)
    {
        final Map<K, V> res = of(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5, k6, v6, k7, v7);
        res.put(k8, v8);
        return res;
    }

    public static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6, K k7, V v7, K k8, V v8, K k9, V v9)
    {
        final Map<K, V> res = of(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5, k6, v6, k7, v7, k8, v8);
        res.put(k9, v9);
        return res;
    }

    public static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6, K k7, V v7, K k8, V v8, K k9, V v9, K k10, V v10)
    {
        final Map<K, V> res = of(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5, k6, v6, k7, v7, k8, v8, k9, v9);
        res.put(k10, v10);
        return res;
    }
}
