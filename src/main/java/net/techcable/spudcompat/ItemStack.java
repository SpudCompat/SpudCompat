package net.techcable.spudcompat;

import lombok.*;
import lombok.experimental.*;

import java.util.Optional;

import com.google.common.base.Preconditions;

import io.netty.buffer.ByteBuf;

import net.techcable.spudcompat.nbt.NBT;

/**
 * An immutable item stack, with mutable nbt
 */
@Getter
@Wither
public class ItemStack {
    private final Material type;
    private final int amount;
    @Getter(AccessLevel.NONE) // we call this 'getRawData' in case we ever provide an abstraction
    private final short data;
    private final Optional<NBT> nbt;

    public ItemStack withNbt(NBT nbt) {
        Preconditions.checkNotNull(nbt, "Null nbt");
        if (hasNbt() && nbt == this.nbt.get()) { // Only do a reference check, since a full equality check is expensive
            return this;
        } else {
            return new ItemStack(type, amount, data, Optional.of(nbt));
        }
    }

    public ItemStack withNoNbt() {
        return !hasNbt() ? this : new ItemStack(type, amount, data, Optional.empty());
    }

    public ItemStack withData(int data) {
        Preconditions.checkArgument(data == (short) data, "Data %s doesn't fit into a short", data);
        return data == this.data ? null : new ItemStack(type, amount, data, nbt);
    }

    public ItemStack withNoData() {
        return withData(0);
    }

    public ItemStack withTypeAndData(Material type, int data) {
        return new ItemStack(type, getAmount(), data, getNbt());
    }

    public ItemStack withTypeAndNoData(Material type) {
        return new ItemStack(type, getAmount(), 0, getNbt());
    }

    private ItemStack(Material type, int amount, int data, Optional<NBT> nbt) {
        Preconditions.checkArgument(type != Material.AIR, "Air is not a valid type");
        Preconditions.checkArgument(data == (short) data, "Data %s doesn't fit into a short", data);
        this.type = Preconditions.checkNotNull(type, "Null type");
        this.amount = amount;
        this.data = (short) data;
        this.nbt = Preconditions.checkNotNull(nbt, "Null nbt optional");
    }

    public ItemStack(Material type, int amount, int data) {
        this(type, amount, data, Optional.empty());
    }


    public ItemStack(Material type, int amount, int data, NBT nbt) {
        this(type, amount, data, Optional.of(nbt));
    }

    public short getRawData() {
        return data;
    }

    public static Optional<ItemStack> read(ProtocolVersion version, ByteBuf buf) {
        short itemId = buf.readShort();
        if (itemId < 0) return Optional.empty();
        int amount = buf.readByte();
        short data = buf.readShort();
        Optional<NBT> nbt;
        if (buf.getByte(buf.readerIndex()) != 0) {
            nbt = Optional.of(NBT.lazyRead(buf));
        } else {
            nbt = Optional.empty();
        }
        return Optional.of(new ItemStack(Material.getMaterial(itemId), amount, data, nbt));
    }

    public Optional<NBT> getNbt() {
        return nbt;
    }

    public boolean hasNbt() {
        return nbt.isPresent();
    }

    public ItemStack convertTo(ProtocolVersion version) {
        if (version.isBefore(ProtocolVersion.v1_8)) {
            switch (this.getType()) {
                case SLIME_BLOCK:
                    assert getRawData() == 0 : getType() + " has data " + getRawData();
                    return withTypeAndNoData(Material.EMERALD_BLOCK);
                case BARRIER:
                    assert getRawData() == 0 : getType() + " has data " + getRawData();
                    return withTypeAndData(Material.STAINED_GLASS_PANE, 14); // Red stained glass
                case IRON_TRAPDOOR:
                    return withType(Material.TRAP_DOOR); // Keep data, as trapdoor data is unchanged
                case PRISMARINE:
                    switch (getRawData()) {
                        case 0: // Regular prismarine
                        case 3: // dark prismarine
                            return withTypeAndNoData(Material.MOSSY_COBBLESTONE);
                        case 1: // Prismarine bricks
                            return withTypeAndData(Material.SMOOTH_BRICK, 1); // Mossy cobblestone bricks
                        default:
                            throw new IllegalArgumentException("Invalid prismarine data: " + getRawData());
                    }
                case SEA_LANTERN:
                    assert getRawData() == 0 : getType() + " has data " + getRawData();
                    return withTypeAndNoData(Material.REDSTONE_LAMP_ON);
                case STANDING_BANNER:
                    return withType(Material.SIGN_POST); // we keep data since it contains orientation which can be used for the sign
                case WALL_BANNER:
                    return withType(Material.WALL_SIGN); // we keep data since it contains orientation which can be used for the sign
                case RED_SANDSTONE:
                    switch (getRawData()) {
                        case 0: // Regular red sandstone
                            return withTypeAndNoData(Material.SANDSTONE);
                        case 1: // chiseled red sandstone
                            return withTypeAndData(Material.SANDSTONE, 1); // Chiseled sandstone
                        case 2: // smooth sandstone
                            return withTypeAndData(Material.SANDSTONE, 2); // smooth sandstone
                        default:
                            throw new IllegalArgumentException("Invalid red sandstone data: " + getRawData());
                    }
                case RED_SANDSTONE_STAIRS:
                    assert getRawData() == 0 : getType() + " has data " + getRawData();
                    return withTypeAndNoData(Material.SANDSTONE_STAIRS);
                case DOUBLE_STONE_SLAB2: // double red sandstone slab
                    switch (getRawData()) {
                        case 0: // Double red sandstone slab
                            return withTypeAndData(Material.DOUBLE_STEP, 1); // double sandstone slab
                        case 8: // Double smooth red sandstone slab
                            return withTypeAndData(Material.DOUBLE_STEP, 9); // double smooth sandstone slab
                        default:
                            throw new IllegalArgumentException("Illegal red double sandstone slab data: " + getRawData());
                    }
                case STONE_SLAB2:
                    switch (getRawData()) {
                        case 0: // red sandstone slab
                            return withTypeAndData(Material.STEP, 1); // sandstone slab
                        case 8: // upper red sandstone slab
                            return withTypeAndData(Material.STEP, 8); // upper sandstone slab
                        default:
                            throw new IllegalArgumentException("Illegal red sandstone slab data: " + getRawData());
                    }
                case SPRUCE_FENCE_GATE:
                case BIRCH_FENCE_GATE:
                case JUNGLE_FENCE_GATE:
                case DARK_OAK_FENCE_GATE:
                case ACACIA_FENCE_GATE:
                    return withType(Material.FENCE_GATE); // Fence gates still keep the same data
                case SPRUCE_FENCE:
                case BIRCH_FENCE:
                case JUNGLE_FENCE:
                case DARK_OAK_FENCE:
                case ACACIA_FENCE:
                    return withType(Material.FENCE);  // Fences still keep the same data
                case SPRUCE_DOOR:
                case BIRCH_DOOR:
                case JUNGLE_DOOR:
                case DARK_OAK_DOOR:
                case ACACIA_DOOR:
                    return withType(Material.WOODEN_DOOR); // Doors keep the same data
                case DAYLIGHT_DETECTOR_INVERTED:
                    return withType(Material.DAYLIGHT_DETECTOR); // Daylight detectors keep the same data
                //
                // Items
                case SPRUCE_DOOR_ITEM:
                case BIRCH_DOOR_ITEM:
                case JUNGLE_DOOR_ITEM:
                case DARK_OAK_DOOR_ITEM:
                case ACACIA_DOOR_ITEM:
                case WOOD_DOOR:
                    assert getRawData() == 0 : getType() + " has data " + getRawData();
                    return withTypeAndNoData(Material.WOOD_DOOR);
                case RABBIT:
                    assert getRawData() == 0 : getType() + " has data " + getRawData();
                    return withTypeAndNoData(Material.RAW_CHICKEN);
                case COOKED_RABBIT:
                    assert getRawData() == 0 : getType() + " has data " + getRawData();
                    return withTypeAndNoData(Material.COOKED_CHICKEN);
                case MUTTON:
                    assert getRawData() == 0 : getType() + " has data " + getRawData();
                    return withTypeAndNoData(Material.RAW_CHICKEN);
                case COOKED_MUTTON:
                    assert getRawData() == 0 : getType() + " has data " + getRawData();
                    return withTypeAndNoData(Material.COOKED_CHICKEN);
                case BANNER:
                    assert getRawData() == 0 : getType() + " has data " + getRawData();
                    return withTypeAndNoData(Material.SIGN);
                case RABBIT_FOOT:
                    assert getRawData() == 0 : getType() + " has data " + getRawData();
                    return withTypeAndNoData(Material.ROTTEN_FLESH);
                case RABBIT_HIDE:
                    assert getRawData() == 0 : getType() + " has data " + getRawData();
                    return withTypeAndNoData(Material.LEATHER);
                // Can't create a meaningful mapping for these items :(
                case PRISMARINE_SHARD:
                case PRISMARINE_CRYSTALS:
                case ARMOR_STAND:
                    return withTypeAndNoData(Material.STONE);
                default:
                    return this;
            }
        } else {
            return this;
        }
    }
}
