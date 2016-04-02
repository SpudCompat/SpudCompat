package net.techcable.spudcompat.metadata;

import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.function.Predicate;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;

import net.techcable.spudcompat.ProtocolVersion;
import net.techcable.spudcompat.entity.EntityType;

public class MetadataTransformerRegistry {
    public static final int MAX_ID = 22;

    private static final Object lock = new Object();
    private static final AtomicReferenceArray<MetadataTransformer> TRANSFORMERS = new AtomicReferenceArray<>(EntityType.values().length * (MAX_ID + 1));

    public static boolean hasTransformer(EntityType entityType, int index) {
        return TRANSFORMERS.get(entityType.ordinal() * EntityType.values().length + index) != null;
    }

    public static MetadataTransformer getTransformer(EntityType entityType, ProtocolVersion version, int index) {
        assert version != null : "Null version";
        if (index < 0 || index > MAX_ID) {
            throw new IllegalArgumentException("Illegal index: " + index);
        }
        return TRANSFORMERS.get(entityType.ordinal() * EntityType.values().length + index);
    }

    public static void register(MetadataTransformer transformer, Predicate<EntityType> entityTypeFilter, int index) {
        Preconditions.checkNotNull(transformer, "Null transformer");
        Preconditions.checkNotNull(entityTypeFilter, "Null entity type predicate");
        for (EntityType entityType : EntityType.values()) {
            if (!entityTypeFilter.test(entityType)) continue;
            register(transformer, entityType, index);
        }
    }


    public static void register(MetadataTransformer transformer, Predicate<EntityType> entityTypeFilter, int... indexes) {
        Preconditions.checkNotNull(indexes, "Null indexes");
        for (int index : indexes) {
            register(transformer, entityTypeFilter, index);
        }
    }

    public static void register(MetadataTransformer transformer, ImmutableSet<EntityType> entityTypes, int index) {
        Preconditions.checkNotNull(transformer, "Null transformer");
        Preconditions.checkNotNull(entityTypes, "Null entity types");
        for (EntityType entityType : entityTypes) {
            register(transformer, entityType, index);
        }
    }

    public static void register(MetadataTransformer transformer, ImmutableSet<EntityType> entityTypes, int... indexes) {
        Preconditions.checkNotNull(indexes, "Null indexes");
        for (int index : indexes) {
            register(transformer, entityTypes, index);
        }
    }


    public static void register(MetadataTransformer transformer, EntityType entityType, int index) {
        Preconditions.checkNotNull(transformer, "Null transformer");
        Preconditions.checkArgument(index >= 0 && index <= MAX_ID, "Invalid index: %s", index);
        synchronized (lock) {
            Preconditions.checkState(hasTransformer(entityType, index), "Transformer %s already registered", transformer);
            TRANSFORMERS.set(index, transformer);
        }
    }
}
