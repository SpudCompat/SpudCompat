package net.techcable.spudcompat.nbt;

import lombok.*;

import java.io.DataOutputStream;
import java.io.IOException;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.util.AbstractReferenceCounted;
import io.netty.util.IllegalReferenceCountException;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.ReferenceCounted;

import org.spacehq.opennbt.NBTIO;
import org.spacehq.opennbt.tag.builtin.Tag;

@AllArgsConstructor
public class SimpleNBT extends AbstractReferenceCounted implements NBT {
    private Tag value;

    @Override
    public Tag getValue() {
        assertAccessible();
        return value;
    }

    @Override
    @SneakyThrows(IOException.class) // ByteBuf should not throw an exception...
    public void write(ByteBuf buf) {
        assertAccessible();
        NBTIO.writeTag(new DataOutputStream(new ByteBufOutputStream(buf)), value);
    }

    @Override
    protected void deallocate() {
        value = null;
    }

    private void assertAccessible() {
        if (refCnt() == 0) throw new IllegalReferenceCountException(0);
    }
}
