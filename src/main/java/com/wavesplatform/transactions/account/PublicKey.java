package com.wavesplatform.transactions.account;

import im.mak.waves.crypto.Crypto;
import com.wavesplatform.transactions.common.Base58String;
import com.wavesplatform.transactions.common.Proof;
import com.wavesplatform.transactions.WavesConfig;

import java.util.Arrays;

/**
 * Public key is used as sender of transactions and orders.
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class PublicKey extends Base58String {

    public static final int BYTES_LENGTH = 32;

    /**
     * Generate public key from the private key.
     *
     * @param privateKey public key
     * @return public key instance
     */
    public static PublicKey from(PrivateKey privateKey) {
        return new PublicKey(privateKey);
    }

    /**
     * Create public key instance from its base58 representation.
     *
     * @param base58Encoded public key bytes as base58-encoded string
     * @return public key instance
     */
    public static PublicKey as(String base58Encoded) {
        return new PublicKey(base58Encoded);
    }

    /**
     * Create public key instance from its bytes.
     *
     * @param bytes public key bytes
     * @return public key instance
     */
    public static PublicKey as(byte[] bytes) {
        return new PublicKey(bytes);
    }

    /**
     * Generate public key from the private key.
     *
     * @param privateKey public key
     */
    public PublicKey(PrivateKey privateKey) {
        this(Crypto.getPublicKey(privateKey.bytes()));
    }

    /**
     * Create public key instance from its base58 representation.
     *
     * @param publicKey public key bytes as base58-encoded string
     */
    public PublicKey(String publicKey) {
        super(publicKey);
    }

    /**
     * Create public key instance from its bytes.
     *
     * @param publicKey public key bytes
     */
    public PublicKey(byte[] publicKey) {
        super(publicKey);

        if (publicKey.length != BYTES_LENGTH)
            throw new IllegalArgumentException("Public key has wrong size in bytes. "
                + "Expected: " + BYTES_LENGTH + ", actual: " + publicKey.length);
    }

    /**
     * Get an address generated from the public key.
     * Depends on the Id of a particular blockchain network.
     *
     * @param chainId blockchain network Id.
     * @return address
     */
    public Address address(byte chainId) {
        return Address.from(chainId, this);
    }

    /**
     * Get an address generated from the public key.
     * Depends on the Id of a particular blockchain network.
     *
     * @return address
     */
    public Address address() {
        return Address.from(WavesConfig.chainId(), this);
    }

    /**
     * Check if the message is actually signed by the private key of this public key.
     *
     * @param message message bytes
     * @param signature signature to validate
     * @return true if the signature is valid
     */
    public boolean isSignatureValid(byte[] message, byte[] signature) {
        if (signature.length != Proof.BYTE_LENGTH)
            throw new IllegalArgumentException("Signature has wrong size in bytes. "
                    + "Expected: " + Proof.BYTE_LENGTH + ", actual: " + signature.length);
        return Crypto.isProofValid(bytes, message, signature);
    }

    public boolean equals(byte[] anotherKey) {
        return Arrays.equals(this.bytes, anotherKey);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PublicKey publicKey = (PublicKey) o;
        return Arrays.equals(bytes, publicKey.bytes);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(bytes);
    }

    @Override
    public String toString() {
        return encoded();
    }

}
