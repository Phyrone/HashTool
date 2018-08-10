package de.phyrone.hashmonitor;

import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

import java.util.function.Function;

public enum HashAlgorythm {
    SHA1(Hashing.sha1()),
    SHA265(Hashing.sha256()),
    SHA384(Hashing.sha384()),
    SHA512(Hashing.sha512()),
    MD5(Hashing.md5()),
    CRC32(Hashing.crc32()),
    CRC32C(Hashing.crc32c()),
    SIPHASH24(Hashing.sipHash24()),
    MUEMUR3_128(Hashing.murmur3_128()),
    FARMHASHFINGERPRINT64(Hashing.farmHashFingerprint64()),
    ALDER32(Hashing.adler32());

    HashFunction function;

    HashAlgorythm(HashFunction function) {
        this.function = function;
    }

    public HashFunction get() {
        return function;
    }
}