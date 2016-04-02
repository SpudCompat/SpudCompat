package net.techcable.spudcompat.entity;

import lombok.*;
import sun.nio.cs.ext.IBM037;

import java.util.Arrays;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableSet;

import net.techcable.spudcompat.ProtocolVersion;

@Getter
@RequiredArgsConstructor
public enum EntityType {
    // Mobs
    CREEPER(Sort.MOB, 50),
    SKELETON(Sort.MOB, 51),
    SPIDER(Sort.MOB, 52),
    GIANT_ZOMBIE(Sort.MOB, 53),
    ZOMBIE(Sort.MOB, 54),
    SLIME(Sort.MOB, 55),
    GHAST(Sort.MOB, 56),
    ZOMBIE_PIGMAN(Sort.MOB, 57),
    ENDERMAN(Sort.MOB, 58),
    CAVE_SPIDER(Sort.MOB, 59),
    SILVERFISH(Sort.MOB, 60),
    BLAZE(Sort.MOB, 61),
    MAGMA_CUBE(Sort.MOB, 62),
    ENDER_DRAGON(Sort.MOB, 63),
    WITHER(Sort.MOB, 64),
    BAT(Sort.MOB, 65),
    WITCH(Sort.MOB, 66),
    ENDERMITE(Sort.MOB, 67) {
        @Override
        public int getId(ProtocolVersion version) {
            if (version.isBefore(ProtocolVersion.v1_8)) {
                return super.getId();
            } else {
                return SILVERFISH.getId(); // Pretend we're a silverfish
            }
        }
    },
    GUARDIAN(Sort.MOB, 68) {
        @Override
        public int getId(ProtocolVersion version) {
            if (version.isBefore(ProtocolVersion.v1_8)) {
                return super.getId();
            } else {
                return SQUID.getId(); // Pretend we're a squid
            }
        }
    },
    PIG(Sort.MOB, 90),
    SHEEP(Sort.MOB, 91),
    COW(Sort.MOB, 92),
    CHICKEN(Sort.MOB, 93),
    SQUID(Sort.MOB, 94),
    WOLF(Sort.MOB, 95),
    MOOSHROOM(Sort.MOB, 96),
    SNOWMAN(Sort.MOB, 97),
    OCELOT(Sort.MOB, 98),
    IRON_GOLEM(Sort.MOB, 99),
    HORSE(Sort.MOB, 100),
    RABBIT(Sort.MOB, 101) {
        @Override
        public int getId(ProtocolVersion version) {
            if (version.isBefore(ProtocolVersion.v1_8)) {
                return super.getId();
            } else {
                return CHICKEN.getId(); // Pretend we're a chicken
            }
        }
    },
    VILLAGER(Sort.MOB, 120),
    // Objects
    BOAT(Sort.OBJECT, 1),
    DROPPED_ITEM(Sort.OBJECT, 2),
    MINECART(Sort.OBJECT, 10),
    /**
     * A storage minecart
     * @deprecated unused since 1.6.x
     */
    @Deprecated
    STORAGE_MINECART(Sort.OBJECT, 11),
    /**
     * A powered minecart
     * @deprecated unused since 1.6.x
     */
    @Deprecated
    POWERED_MINECART(Sort.OBJECT, 12),
    ACTIVATED_TNT(Sort.OBJECT, 50),
    ENDER_CRYSTAL(Sort.OBJECT, 51),
    ARROW(Sort.OBJECT, 60),
    SNOWBALL(Sort.OBJECT, 61),
    EGG(Sort.OBJECT, 62),
    FIREBALL(Sort.OBJECT, 63),
    FIRE_CHARGE(Sort.OBJECT, 64),
    THROWN_ENDERPEARL(Sort.OBJECT, 65),
    WITHER_SKULL(Sort.OBJECT, 66),
    FALLING_OBJECTS(Sort.OBJECT, 70),
    ITEM_FRAMES(Sort.OBJECT, 71),
    EYE_OF_ENDER(Sort.OBJECT, 72),
    THROWN_POTION(Sort.OBJECT, 73),
    FALLING_DRAGON_EGG(Sort.OBJECT, 74),
    THROWN_EXP_BOTTLE(Sort.OBJECT, 75),
    FIREWORK_ROCKET(Sort.OBJECT, 76),
    LEASH_KNOT(Sort.OBJECT, 77),
    ARMOR_STAND(Sort.OBJECT, 78) {
        @Override
        public int getId(ProtocolVersion version) {
            if (version.isBefore(ProtocolVersion.v1_8)) {
                return super.getId();
            } else {
                return ENDER_CRYSTAL.getId(); // Pretend we're an ender crystal
            }
        }
    },
    FISHING_FLOAT(Sort.OBJECT, 90),
    // Others
    PLAYER(Sort.OTHER, -1);

    private final Sort sort;
    @Getter(AccessLevel.PRIVATE) // We need to fake the ids for certain versions
    private final int id;

    public int getId(ProtocolVersion version) {
        return getId();
    }

    public boolean isLiving() {
        switch (this) {
            case ARMOR_STAND:
            case BAT:
            case BLAZE:
            case CAVE_SPIDER:
            case CHICKEN:
            case COW:
            case CREEPER:
            case ENDER_DRAGON:
            case ENDERMAN:
            case ENDERMITE:
            case GHAST:
            case GIANT_ZOMBIE:
            case GUARDIAN:
            case HORSE:
            case IRON_GOLEM:
            case MAGMA_CUBE:
            case MOOSHROOM:
            case OCELOT:
            case PIG:
            case ZOMBIE_PIGMAN:
            case PLAYER:
            case RABBIT:
            case SHEEP:
            case SILVERFISH:
            case SKELETON:
            case SLIME:
            case SNOWMAN:
            case SPIDER:
            case SQUID:
            case VILLAGER:
            case WITCH:
            case WITHER:
            case WOLF:
            case ZOMBIE:
                return true;
            default:
                return false;
        }
    }

    public boolean isAgeable() {
        switch (this) {
            case CHICKEN:
            case COW:
            case HORSE:
            case MOOSHROOM:
            case OCELOT:
            case PIG:
            case RABBIT:
            case SHEEP:
            case VILLAGER:
            case WOLF:
                return true;
            default:
                return false;
        }
    }
    
    private enum Sort {
        MOB, OBJECT, OTHER;
    }
}
