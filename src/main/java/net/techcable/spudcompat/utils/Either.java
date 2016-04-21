package net.techcable.spudcompat.utils;

import com.google.common.base.Preconditions;

@SuppressWarnings("unchecked") // Generics are stupid
public class Either<L, R> {
    private final Object value;
    private final boolean left;

    private Either(Object value, boolean left) {
        this.value = Preconditions.checkNotNull(value, "Null value");
        this.left = left;
    }

    public boolean isLeft() {
        return left;
    }

    public boolean isRight() {
        return !isLeft();
    }

    public L getLeft() {
        return isLeft() ? (L) value : null;
    }

    public R getRight() {
        return isRight() ? (R) value : null;
    }

    public Object getRawValue() {
        return value;
    }

    public static <L, R> Either<L, R> ofLeft(L value) {
        return new Either<>(value, true);
    }

    public static <L, R> Either<L, R> ofRight(R value) {
        return new Either<>(value, false);
    }
}
