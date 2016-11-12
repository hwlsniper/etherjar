package org.ethereumclassic.etherjar.contract;

import org.bouncycastle.jcajce.provider.digest.Keccak;
import org.ethereumclassic.etherjar.model.Hex32;
import org.ethereumclassic.etherjar.model.HexData;
import org.ethereumclassic.etherjar.model.MethodId;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 *
 * baz(uint32,bool) with arguments (69, true) becomes:
 * 0xcdcd77c000000000000000000000000000000000000000000000000000000000000000450000000000000000000000000000000000000000000000000000000000000001
 *
 * The first four bytes of the call data for a function call specifies the function to be called. It is the
 * first (left, high-order in big-endian) four bytes of the Keccak (SHA-3) hash of the signature of the function. The
 * signature is defined as the canonical expression of the basic prototype, i.e. the function name with the
 * parenthesised list of parameter types. Parameter types are split by a single comma - no spaces are used.
 *
 * See https://github.com/ethereum/wiki/wiki/Ethereum-Contract-ABI
 *
 * @author Igor Artamonov
 */
public class ContractMethod {

    private MethodId id;

    private ContractMethod(MethodId id) {
        this.id = id;
    }

    /**
     * @return function id
     */
    public MethodId getId() {
        return id;
    }

    /**
     * Encodes call data to send through RPC
     *
     * @param params parameters of the call
     * @return encoded call
     */
    public HexData encodeCall(Hex32... params) {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        try {
            buf.write(id.getBytes());
            for (Hex32 param: params) {
                buf.write(param.getBytes());
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        return new HexData(buf.toByteArray());
    }

    static class Builder {

        private MethodId id;

        public Builder() {
        }

        /**
         * builds from full method signature like `name(datatype1,datatype2)`, or transfer(address,uint256)
         *
         * Make sure you're using full name for the type, e.g uint256 instead of simple uint
         *
         * @param signature full method signature
         * @return builder
         */
        public Builder fromFullName(String signature) {
            //TODO add regexp to validate signature format
            Keccak.Digest256 keccak = new Keccak.Digest256();
            keccak.update(signature.getBytes());
            byte[] hash = keccak.digest();
            byte[] head = new byte[4];
            System.arraycopy(hash, 0, head, 0, 4);
            id = MethodId.from(head);
            return this;
        }

        public ContractMethod build() {
            return new ContractMethod(id);
        }
    }

}