package net.techcable.spudcompat;

import java.io.IOException;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import net.md_5.bungee.api.plugin.Plugin;
import net.techcable.spudcompat.entity.EntityType;
import net.techcable.spudcompat.libs.mcstats.Metrics;
import net.techcable.spudcompat.metadata.BasicMetadataTransformer;
import net.techcable.spudcompat.metadata.DefaultingMetadataTransformer;
import net.techcable.spudcompat.metadata.MetadataDataType;
import net.techcable.spudcompat.metadata.MetadataDataValue;
import net.techcable.spudcompat.metadata.MetadataTransformerRegistry;
import net.techcable.spudcompat.protocol.PacketManager;
import net.techcable.spudcompat.protocol.injector.BungeeProtocolInjector;

public class SpudCompat extends Plugin {
    private PacketManager packetManager;

    @Override
    public void onEnable() {
        registerMetadataTransformers();
        try {
            if (new Metrics(this).start()) {
                getLogger().info("Successfully started metrics");
            }
        } catch (IOException e) {
            getLogger().warning("Unable to start metrics");
        }
        packetManager = new PacketManager(this);
    }

    private void registerMetadataTransformers() {
        // Type of age changed from 1.7 to 1.8
        MetadataTransformerRegistry.register(
                BasicMetadataTransformer.create(12,
                        ProtocolVersion.v1_8, MetadataDataType.BYTE,
                        ProtocolVersion.v1_7_10, MetadataDataType.INT
                ), EntityType::isAgeable, 12
        );
        // Type of enderman carried block changed from 1.7 to 1.8
        MetadataTransformerRegistry.register(
                BasicMetadataTransformer.create(12,
                        ProtocolVersion.v1_8, MetadataDataType.SHORT,
                        ProtocolVersion.v1_7_10, MetadataDataType.BYTE
                ), ImmutableSet.of(EntityType.ENDERMAN), 16
        );
        // Skin flags aren't present on 1.7, default to showing all skins
        MetadataTransformerRegistry.register(
                DefaultingMetadataTransformer.create(10,
                        ProtocolVersion.v1_8, MetadataDataValue.ofByte((byte) (0x7F)) // Show everything by default (all on)
                ), ImmutableSet.of(EntityType.PLAYER), 10
        );
        // Name tag switched from id 10 to 2
        MetadataTransformerRegistry.register(
                BasicMetadataTransformer.create(
                        ImmutableMap.of(
                                ProtocolVersion.v1_7_10, 10,
                                ProtocolVersion.v1_8, 2
                        ), MetadataDataType.STRING
                ), ImmutableSet.copyOf(EntityType.values()), 2, 10
        );
        // The name tag display flag switched from id 11 to 3
        MetadataTransformerRegistry.register(
                BasicMetadataTransformer.create(
                        ImmutableMap.of(
                                ProtocolVersion.v1_7_10, 11,
                                ProtocolVersion.v1_8, 3
                        ), MetadataDataType.BYTE
                ), ImmutableSet.copyOf(EntityType.values()), 2, 10
        );
        // NO AI flag
        MetadataTransformerRegistry.register(
                DefaultingMetadataTransformer.create(15,
                        ProtocolVersion.v1_8, MetadataDataValue.ofByte((byte) 0)// Default to telling the client we have ai
                ), EntityType::isLiving, 15
        );
        // Item frame value switched from 2 to 8
        MetadataTransformerRegistry.register(
                BasicMetadataTransformer.create(
                        ImmutableMap.of(
                                ProtocolVersion.v1_7_10, 2,
                                ProtocolVersion.v1_8, 8
                        ), MetadataDataType.STACK
                ), ImmutableSet.of(EntityType.ITEM_FRAMES), 2, 10
        );
        // Item frame 'rotation' switched from 3 to 9
        MetadataTransformerRegistry.register(
                BasicMetadataTransformer.create(
                        ImmutableMap.of(
                                ProtocolVersion.v1_7_10, 3,
                                ProtocolVersion.v1_8, 9
                        ), MetadataDataType.STACK
                ), ImmutableSet.of(EntityType.ITEM_FRAMES), 2, 10
        );
    }
}
