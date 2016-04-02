package net.techcable.spudcompat;

import lombok.*;

@RequiredArgsConstructor
public enum ProtocolVersion {
    v1_7_10(5),
    v1_8(47);

    public boolean isBefore(ProtocolVersion other) {
        return compareTo(other) < 0;
    }

    private final int versionNumber;
}
