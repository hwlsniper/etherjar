package io.infinitape.etherjar.contract.type;

import io.infinitape.etherjar.model.Hex32;
import io.infinitape.etherjar.model.HexData;

import java.util.Objects;
import java.util.Optional;

/**
 * Dynamic sized byte sequence.
 */
public class DynamicBytesType implements DynamicType<byte[]> {

    public final static DynamicBytesType DEFAULT = new DynamicBytesType();

    /**
     * Try to parse a {@link DynamicBytesType} string representation (either canonical form or not).
     *
     * @param str a string
     * @return a {@link DynamicBytesType} instance is packed as {@link Optional} value,
     * or {@link Optional#empty()} instead
     * @throws NullPointerException if a {@code str} is <code>null</code>
     *
     * @see #getCanonicalName()
     */
    public static Optional<DynamicBytesType> from(String str) {
        Objects.requireNonNull(str);

        if (!Objects.equals(str, "bytes"))
            return Optional.empty();

        return Optional.of(DEFAULT);
    }

    @Override
    public String getCanonicalName() {
        return "bytes";
    }

    @Override
    public HexData encode(byte... bytes) {
        int rem = bytes.length % Hex32.SIZE_BYTES;

        HexData data = Type.encodeLength(bytes.length).concat(new HexData(bytes));

        return rem == 0 ? data :
                data.concat(new HexData(new byte[Hex32.SIZE_BYTES - rem]));
    }

    @Override
    public byte[] decode(HexData data) {
        int len = Type.decodeLength(
                data.extract(Hex32.SIZE_BYTES, Hex32::from)).intValueExact();

        int size = len % Hex32.SIZE_BYTES == 0 ? len :
                len + Hex32.SIZE_BYTES - len % Hex32.SIZE_BYTES;

        if (data.getSize() != Hex32.SIZE_BYTES + size)
            throw new IllegalArgumentException("Wrong data length to decode bytes: " + data);

        return data.extract(len, Hex32.SIZE_BYTES).getBytes();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;

        if (Objects.isNull(obj)) return false;

        return Objects.equals(getClass(), obj.getClass());
    }

    @Override
    public String toString() {
        return getCanonicalName();
    }
}
