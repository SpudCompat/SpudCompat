package net.techcable.spudcompat;

import lombok.*;

@RequiredArgsConstructor
public enum ProtocolVersion {
    v1_7_10(5),
    v1_8(47);

    public boolean isBefore(ProtocolVersion other) {
        return this.getId() < other.getId();
    }

    private final int versionNumber;

    public int getId() {
        return versionNumber;
    }

    public static final int MAX_ID;
    private static final ProtocolVersion[] BY_ID;

    static {
        int maxId = 0;
        for (ProtocolVersion version : values()) {
            if (version.getId() > maxId) {
                maxId = version.getId();
            }
        }
        MAX_ID = maxId;
        BY_ID = new ProtocolVersion[MAX_ID + 1];
        for (ProtocolVersion version : values()) {
            int id = version.getId();
            if (BY_ID[id] != null) throw new AssertionError("Already a version with id: " + id);
            BY_ID[id] = version;
        }
    }

    public static ProtocolVersion getById(int id) {
        if (id < 0) {
            throw new IllegalArgumentException("Negative id: " + id);
        } else if (id < BY_ID.length) {
            return BY_ID[id];
        } else {
            return null;
        }
    }
}
