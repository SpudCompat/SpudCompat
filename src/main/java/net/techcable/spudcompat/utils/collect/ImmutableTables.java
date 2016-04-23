package net.techcable.spudcompat.utils.collect;

import java.util.Map;

import com.google.common.collect.ImmutableTable;

import static com.google.common.base.Preconditions.checkNotNull;

public class ImmutableTables {
    public static <R, C, V> ImmutableTable<R, C, V> ofMapWithColumn(Map<R, V> map, C column) {
        checkNotNull(map, "Null map");
        checkNotNull(column, "Null column");
        ImmutableTable.Builder<R, C, V> tableBuilder = ImmutableTable.builder();
        map.forEach((key, value) -> tableBuilder.put(key, column, value));
        return tableBuilder.build();
    }

    public static <R, C, V> ImmutableTable<R, C, V> of(R r1, C c1, V v1, R r2, C c2, V v2) {
        ImmutableTable.Builder<R, C, V> builder = ImmutableTable.builder();
        builder.put(checkNotNull(r1, "Null first row"), checkNotNull(c1, "Null first column"), checkNotNull(v1, "Null first value"));
        builder.put(checkNotNull(r2, "Null second row"), checkNotNull(c2, "Null second column"), checkNotNull(v2, "Null second value"));
        return builder.build();
    }
}
