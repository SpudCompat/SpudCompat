package net.techcable.spudcompat.nbt;

import lombok.*;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.util.ReferenceCounted;

import org.spacehq.opennbt.NBTIO;
import org.spacehq.opennbt.tag.builtin.Tag;

public interface NBT {
    public Tag getValue();
    public void write(ByteBuf buf);


    /**
     * Calculate the length of the NBT, then return a lazy reading nbt-object.
     * <p>This allows you to defer reading the nbt until later, often saving space and time.</p>
     * <p>Caution: Some errors may not be found until later.</p>
     *
     * @param buf    the buf to read from
     * @return a lazy-reading NBT object
     */
    public static NBT lazyRead(ByteBuf buf) {
        return lazyRead(buf, NBTUtils.calculateNBTLength(buf));
    }

    /**
     * Return a NBT object that will read the given number of bytes from the buffer, and turn it into NBT only if/when needed.
     * <p>This allows you to defer reading the nbt until later, saving space and time.</p>
     * <p>Warning: Errors in the NBT will not be found until later.</p>
     *
     * @param buf    the buf to read from
     * @param length the amount to read
     * @return a lazy-reading NBT object
     */
    public static NBT lazyRead(ByteBuf buf, int length) {
        return new LazyReadingNBT(buf.readBytes(length));
    }

    /**
     * Read the nbt from the given byte buf
     *
     * @param buf the buf to read the nbt from
     * @return the nbt
     */
    @SneakyThrows(IOException.class) // ByteBufs don't throw IOExceptions
    public static NBT read(ByteBuf buf) {
        return read(new ByteBufInputStream(buf));
    }

    /**
     * Read the nbt from the given input stream
     *
     * @param in the stream to read the nbt from
     * @return the nbt
     */
    public static NBT read(InputStream in) throws IOException {
        return new SimpleNBT(NBTIO.readTag(new DataInputStream(in)));
    }

    public static NBT create(Tag tag) {
        return new SimpleNBT(tag);
    }
}
