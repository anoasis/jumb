/* 
 * Copyright (c) 2001, Sun Microsystems Laboratories 
 * All rights reserved. 
 * 
 * Redistribution and use in source and binary forms, 
 * with or without modification, are permitted provided 
 * that the following conditions are met: 
 * 
 *     Redistributions of source code must retain the 
 *     above copyright notice, this list of conditions 
 *     and the following disclaimer. 
 *             
 *     Redistributions in binary form must reproduce 
 *     the above copyright notice, this list of conditions 
 *     and the following disclaimer in the documentation 
 *     and/or other materials provided with the distribution. 
 *             
 *     Neither the name of Sun Microsystems, Inc. nor 
 *     the names of its contributors may be used to endorse 
 *     or promote products derived from this software without 
 *     specific prior written permission. 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND 
 * CONTRIBUTORS ``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, 
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 * DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, 
 * OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, 
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND 
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, 
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY 
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF 
 * THE POSSIBILITY OF SUCH DAMAGE. 
 */

/*
 * KeyProtector.java
 */
package com.sun.multicast.reliable.authentication;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.util.*;
import sun.security.pkcs.PKCS8Key;
import sun.security.pkcs.EncryptedPrivateKeyInfo;
import sun.security.x509.AlgorithmId;
import sun.security.util.ObjectIdentifier;
import sun.security.util.DerValue;

/**
 * This is an implementation of a Sun proprietary, exportable algorithm
 * intended for use when protecting (or recovering the cleartext version of)
 * sensitive keys.
 * This algorithm is not intended as a general purpose cipher.
 * 
 * This is how the algorithm works for key protection:
 * 
 * p - user password
 * s - random salt
 * X - xor key
 * P - to-be-protected key
 * Y - protected key
 * R - what gets stored in the keystore
 * 
 * Step 1:
 * Take the user's password, append a random salt (of fixed size) to it,
 * and hash it: d1 = digest(p, s)
 * Store d1 in X.
 * 
 * Step 2:
 * Take the user's password, append the digest result from the previous step,
 * and hash it: dn = digest(p, dn-1).
 * Store dn in X (append it to the previously stored digests).
 * Repeat this step until the length of X matches the length of the private key
 * P.
 * 
 * Step 3:
 * XOR X and P, and store the result in Y: Y = X XOR P.
 * 
 * Step 4:
 * Store s, Y, and digest(p, P) in the result buffer R:
 * R = s + Y + digest(p, P), where "+" denotes concatenation.
 * (NOTE: digest(p, P) is stored in the result buffer, so that when the key is
 * recovered, we can check if the recovered key indeed matches the original
 * key.) R is stored in the keystore.
 * 
 * The protected key is recovered as follows:
 * 
 * Step1 and Step2 are the same as above, except that the salt is not randomly
 * generated, but taken from the result R of step 4 (the first length(s)
 * bytes).
 * 
 * Step 3 (XOR operation) yields the plaintext key.
 * 
 * Then concatenate the password with the recovered key, and compare with the
 * last length(digest(p, P)) bytes of R. If they match, the recovered key is
 * indeed the same key as the original key.
 * 
 * @see java.security.KeyStore
 * @see JavaKeyStore
 * @see KeyTool
 * 
 * @since JDK1.2
 */
final class KeyProtector {
    private static final int SALT_LEN = 20;     // the salt length
    private static final String DIGEST_ALG = "SHA";
    private static final int DIGEST_LEN = 20;

    // defined by JavaSoft

    private static final String KEY_PROTECTOR_OID = "1.3.6.1.4.1.42.2.17.1.1";

    // The password used for protecting/recovering keys passed through this
    // key protector. We store it as a byte array, so that we can digest it.

    private byte[] passwdBytes;
    private MessageDigest md;

    /**
     * Creates an instance of this class, and initializes it with the given
     * password.
     * 
     * <p>The password is expected to be in printable ASCII.
     * Normal rules for good password selection apply: at least
     * seven characters, mixed case, with punctuation encouraged.
     * Phrases or words which are easily guessed, for example by
     * being found in dictionaries, are bad.
     */
    public KeyProtector(char[] password) throws NoSuchAlgorithmException {
        int i, j;

        if (password == null) {
            throw new IllegalArgumentException("password can't be null");
        }

        md = MessageDigest.getInstance(DIGEST_ALG);

        // Convert password to byte array, so that it can be digested

        passwdBytes = new byte[password.length * 2];

        for (i = 0, j = 0; i < password.length; i++) {
            passwdBytes[j++] = (byte) (password[i] >> 8);
            passwdBytes[j++] = (byte) password[i];
        }
    }

    /**
     * Ensures that the password bytes of this key protector are
     * set to zero when there are no more references to it.
     */
    protected void finalize() {
        if (passwdBytes != null) {
            Arrays.fill(passwdBytes, (byte) 0x00);

            passwdBytes = null;
        }
    }

    /*
     * Protects the given plaintext key, using the password provided at
     * construction time.
     */

    public byte[] protect(Key key) throws KeyStoreException {
        int i;
        int numRounds;
        byte[] digest;
        int xorOffset;      // offset in xorKey where next digest will be stored
        int encrKeyOffset = 0;

        if (key == null) {
            throw new IllegalArgumentException("plaintext key can't be null");
        }

        byte[] plainKey = key.getEncoded();

        // Determine the number of digest rounds

        numRounds = plainKey.length / DIGEST_LEN;

        if ((plainKey.length % DIGEST_LEN) != 0) {
            numRounds++;
        } 

        // Create a random salt

        byte[] salt = new byte[SALT_LEN];
        SecureRandom random = new SecureRandom();

        random.nextBytes(salt);

        // Set up the byte array which will be XORed with "plainKey"

        byte[] xorKey = new byte[plainKey.length];

        // Compute the digests, and store them in "xorKey"

        for (i = 0, xorOffset = 0, digest = salt; i < numRounds; 
                i++, xorOffset += DIGEST_LEN) {
            md.update(passwdBytes);
            md.update(digest);

            digest = md.digest();

            md.reset();

            // Copy the digest into "xorKey"

            if (i < numRounds - 1) {
                System.arraycopy(digest, 0, xorKey, xorOffset, digest.length);
            } else {
                System.arraycopy(digest, 0, xorKey, xorOffset, 
                                 xorKey.length - xorOffset);
            }
        }

        // XOR "plainKey" with "xorKey", and store the result in "tmpKey"

        byte[] tmpKey = new byte[plainKey.length];

        for (i = 0; i < tmpKey.length; i++) {
            tmpKey[i] = (byte) (plainKey[i] ^ xorKey[i]);
        }

        // Store salt and "tmpKey" in "encrKey"

        byte[] encrKey = new byte[salt.length + tmpKey.length + DIGEST_LEN];

        System.arraycopy(salt, 0, encrKey, encrKeyOffset, salt.length);

        encrKeyOffset += salt.length;

        System.arraycopy(tmpKey, 0, encrKey, encrKeyOffset, tmpKey.length);

        encrKeyOffset += tmpKey.length;

        // Append digest(password, plainKey) as an integrity check to "encrKey"

        md.update(passwdBytes);
        Arrays.fill(passwdBytes, (byte) 0x00);

        passwdBytes = null;

        md.update(plainKey);

        digest = md.digest();

        md.reset();
        System.arraycopy(digest, 0, encrKey, encrKeyOffset, digest.length);

        // wrap the protected private key in a PKCS#8-style
        // EncryptedPrivateKeyInfo, and returns its encoding

        AlgorithmId encrAlg;

        encrAlg = new AlgorithmId(new ObjectIdentifier(KEY_PROTECTOR_OID));

        try {
            return new EncryptedPrivateKeyInfo(encrAlg, encrKey).getEncoded();
        } catch (IOException ioe) {
            throw new KeyStoreException(ioe.getMessage());
        }
    }

    /*
     * Recovers the plaintext version of the given key (in protected format),
     * using the password provided at construction time.
     */

    public byte[] recover(EncryptedPrivateKeyInfo encrInfo) 
            throws UnrecoverableKeyException {
        int i;
        byte[] digest;
        int numRounds;
        int xorOffset;      // offset in xorKey where next digest will be stored
        int encrKeyLen;     // the length of the encrpyted key

        // do we support the algorithm?

        AlgorithmId encrAlg = encrInfo.getAlgorithm();

        if (!(encrAlg.getOID().toString().equals(KEY_PROTECTOR_OID))) {
            throw new UnrecoverableKeyException("Unsupported key protection " 
                                                + "algorithm");
        }

        byte[] protectedKey = encrInfo.getEncryptedData();

        /*
         * Get the salt associated with this key (the first SALT_LEN bytes of
         * <code>protectedKey</code>)
         */
        byte[] salt = new byte[SALT_LEN];

        System.arraycopy(protectedKey, 0, salt, 0, SALT_LEN);

        // Determine the number of digest rounds

        encrKeyLen = protectedKey.length - SALT_LEN - DIGEST_LEN;
        numRounds = encrKeyLen / DIGEST_LEN;

        if ((encrKeyLen % DIGEST_LEN) != 0) {
            numRounds++;
        } 

        // Get the encrypted key portion and store it in "encrKey"

        byte[] encrKey = new byte[encrKeyLen];

        System.arraycopy(protectedKey, SALT_LEN, encrKey, 0, encrKeyLen);

        // Set up the byte array which will be XORed with "encrKey"

        byte[] xorKey = new byte[encrKey.length];

        // Compute the digests, and store them in "xorKey"

        for (i = 0, xorOffset = 0, digest = salt; i < numRounds; 
                i++, xorOffset += DIGEST_LEN) {
            md.update(passwdBytes);
            md.update(digest);

            digest = md.digest();

            md.reset();

            // Copy the digest into "xorKey"

            if (i < numRounds - 1) {
                System.arraycopy(digest, 0, xorKey, xorOffset, digest.length);
            } else {
                System.arraycopy(digest, 0, xorKey, xorOffset, 
                                 xorKey.length - xorOffset);
            }
        }

        // XOR "encrKey" with "xorKey", and store the result in "plainKey"

        byte[] plainKey = new byte[encrKey.length];

        for (i = 0; i < plainKey.length; i++) {
            plainKey[i] = (byte) (encrKey[i] ^ xorKey[i]);
        }

        /*
         * Check the integrity of the recovered key by concatenating it with
         * the password, digesting the concatenation, and comparing the
         * result of the digest operation with the digest provided at the end
         * of <code>protectedKey</code>. If the two digest values are
         * different, throw an exception.
         */
        md.update(passwdBytes);
        Arrays.fill(passwdBytes, (byte) 0x00);

        passwdBytes = null;

        md.update(plainKey);

        digest = md.digest();

        md.reset();

        for (i = 0; i < digest.length; i++) {
            if (digest[i] != protectedKey[SALT_LEN + encrKeyLen + i]) {
                throw new UnrecoverableKeyException("Cannot recover key");
            }
        }

        // for (i = 0; i < DESKeySpec.DES_KEY_LEN; i++)
        // System.out.println("encoded is " + plainKey[i]);

        return (plainKey);

    // The parseKey() method of PKCS8Key parses the key
    // algorithm and instantiates the appropriate key factory,
    // which in turn parses the key material.
    // try {
    // return PKCS8Key.parseKey(new DerValue(plainKey));
    // } catch (IOException ioe) {
    // throw new UnrecoverableKeyException(ioe.getMessage());
    // }

    }

}

