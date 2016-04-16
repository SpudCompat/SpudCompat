package net.techcable.spudcompat.nbt;

import lombok.*;
import sun.nio.ch.IOUtil;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.google.common.base.Preconditions;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.util.AbstractReferenceCounted;
import io.netty.util.IllegalReferenceCountException;

import org.spacehq.opennbt.NBTIO;
import org.spacehq.opennbt.tag.builtin.Tag;

public class LazyReadingNBT implements NBT {
    private ByteBuf buf;
    private volatile Tag tag;

    public LazyReadingNBT(ByteBuf buf) {
        Preconditions.checkNotNull(buf, "Null buf");
        buf.retain();
        this.buf = buf;
    }

    @SneakyThrows(IOException.class)
    public synchronized void load() {
        if (isLoaded()) return;
        tag = NBTIO.readTag(new DataInputStream(new ByteBufInputStream(buf)));
        buf = null;
    }

    public boolean isLoaded() {
        return tag != null;
    }

    @Override
    public Tag getValue() {
        if (!isLoaded()) load();
        Tag tag = this.tag;
        if (tag == null) {
            throw new AssertionError("Null tag");
        }
        return tag;
    }

    @Override
    @SneakyThrows(IOException.class) // Shouldn't happen with byte bufs
    public synchronized void write(ByteBuf buf) {
        if (isLoaded()) {
            NBTIO.writeTag(new DataOutputStream(new ByteBufOutputStream(buf)), tag);
        } else {
            buf.writeBytes(this.buf);
        }
    }
}
