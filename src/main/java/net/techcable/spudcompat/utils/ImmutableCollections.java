package net.techcable.spudcompat.utils;

import java.util.EnumMap;
import java.util.Map;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;

public class ImmutableCollections {

    public static <K extends Enum<K>, V> ImmutableMap<K, V> enumMapOf(K key, V value) {
        Preconditions.checkNotNull(key, "Null key");
        Preconditions.checkNotNull(value, "Null value");
        EnumMap<K, V> map = new EnumMap<K, V>(key.getDeclaringClass());
        map.put(key, value);
        return assertEnumMap(ImmutableMap.copyOf(map));
    }

    public static <K extends Enum<K>, V> ImmutableMap<K, V> enumMapOf(K key1, V value1, K key2, V value2) {
        Preconditions.checkNotNull(key1, "Null first key");
        Preconditions.checkNotNull(value1, "Null first value");
        Preconditions.checkNotNull(key2, "Null second key");
        Preconditions.checkNotNull(value2, "Null second value");
        Preconditions.checkArgument(key1.getDeclaringClass() == key2.getDeclaringClass(), "Key classes %s and %s don't match", key1.getDeclaringClass(), key2.getDeclaringClass());
        EnumMap<K, V> map = new EnumMap<>(key1.getDeclaringClass());
        map.put(key1, value1);
        map.put(key2, value2);
        return assertEnumMap(ImmutableMap.copyOf(map));
    }


    public static <K extends Enum<K>, V> ImmutableMap<K, V> enumMapOf(K key1, V value1, K key2, V value2, K key3, V value3) {
        Preconditions.checkNotNull(key1, "Null first key");
        Preconditions.checkNotNull(value1, "Null first value");
        Preconditions.checkNotNull(key2, "Null second key");
        Preconditions.checkNotNull(value2, "Null second value");
        Preconditions.checkNotNull(key3, "Null third key");
        Preconditions.checkNotNull(value3, "Null third value");
        Preconditions.checkArgument(key1.getDeclaringClass() == key2.getDeclaringClass() && key2.getDeclaringClass() == key3.getDeclaringClass(), "Key classes %s, %s, and %s don't match", key1.getDeclaringClass(), key2.getDeclaringClass(), key3.getDeclaringClass());
        EnumMap<K, V> map = new EnumMap<>(key1.getDeclaringClass());
        map.put(key1, value1);
        map.put(key2, value2);
        map.put(key3, value3);
        return assertEnumMap(ImmutableMap.copyOf(map));
    }

    private static final Class<? extends ImmutableMap> IMMUTABLE_ENUM_MAP;

    static {
        try {
            IMMUTABLE_ENUM_MAP = Class.forName("com.google.common.collect.ImmutableEnumMap").asSubclass(ImmutableMap.class);
        } catch (ClassNotFoundException e) {
            throw new AssertionError("Could not find ImmutableEnumMap", e);
        }
    }

    public static <K extends Enum<K>, V> ImmutableMap<K, V> enumMapOf(Map<K, V> map) {
        Preconditions.checkNotNull(map, "Null map");
        if (IMMUTABLE_ENUM_MAP.isInstance(map)) {
            return (ImmutableMap<K, V>) map;
        }
        if (!(map instanceof EnumMap)) {
            map = new EnumMap<>(map);
        }
        return assertEnumMap(ImmutableMap.copyOf(map));
    }


    private static <T> T assertEnumMap(T t) {
        assert IMMUTABLE_ENUM_MAP.isInstance(t) : t.getClass() + " is not an ImmutableEnumMap";
        return t;
    }
}
