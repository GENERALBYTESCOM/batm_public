/*************************************************************************************
 * Copyright (C) 2014-2019 GENERAL BYTES s.r.o. All rights reserved.
 *
 * This software may be distributed and modified under the terms of the GNU
 * General Public License version 2 (GPL2) as published by the Free Software
 * Foundation and appearing in the file GPL2.TXT included in the packaging of
 * this file. Please note that GPL2 Section 2[b] requires that all works based
 * on this software must also be made publicly available under the terms of
 * the GPL2 ("Copyleft").
 *
 * Contact information
 * -------------------
 *
 * GENERAL BYTES s.r.o.
 * Web      :  http://www.generalbytes.com
 *
 ************************************************************************************/
package com.generalbytes.batm.sshtunnel;

import org.apache.sshd.server.auth.password.PasswordAuthenticator;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.PosixFilePermission;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

public class Auth {
    private static final Set<PosixFilePermission> PASSWD_FILE_PERMISSIONS = EnumSet.of(PosixFilePermission.OWNER_READ, PosixFilePermission.OWNER_WRITE);
    private static final int SALT_LENGTH = 16;
    private static final int GENERATED_PASSWORD_LENGTH = 32;
    private static final char[] GENERATED_PASSWORD_ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ-_.?!@#$%^&*".toCharArray();
    private static final String PASSWD_FILE = "passwd";
    private final Path PASSWD_PATH;

    public Auth(File configDir) {
        PASSWD_PATH = new File(configDir, PASSWD_FILE).toPath();
    }

    public static String generateRandomPassword() {
        SecureRandom random = new SecureRandom();
        char[] password = new char[GENERATED_PASSWORD_LENGTH];
        for (int i = 0; i < password.length; i++) {
            password[i] = GENERATED_PASSWORD_ALPHABET[random.nextInt(GENERATED_PASSWORD_ALPHABET.length)];
        }
        return new String(password);
    }

    public static byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return salt;
    }

    public void storePassword(byte[] salt, String password) throws IOException, GeneralSecurityException {
        Files.write(PASSWD_PATH, salt, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        Files.write(PASSWD_PATH, hash(salt, password), StandardOpenOption.APPEND);
        Files.setPosixFilePermissions(PASSWD_PATH, PASSWD_FILE_PERMISSIONS);
    }

    public PasswordAuthenticator getPasswordAuthenticator() throws IOException {
        byte[] passwdAllBytes = Files.readAllBytes(PASSWD_PATH);
        byte[] salt = Arrays.copyOfRange(passwdAllBytes, 0, SALT_LENGTH);
        byte[] hash = Arrays.copyOfRange(passwdAllBytes, SALT_LENGTH, passwdAllBytes.length);
        return new BruteforceBlockingPasswordAuthenticator(30, (username, password, session) -> {
            try {
                return Arrays.equals(hash, hash(salt, password));
            } catch (GeneralSecurityException e) {
                return false;
            }
        });
    }

    private static byte[] hash(byte[] salt, String password) throws GeneralSecurityException {
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        return factory.generateSecret(spec).getEncoded();
    }

    /**
     * Generates a new password and stores it to a file
     *
     * @return generated password
     * @throws IOException
     * @throws GeneralSecurityException
     */
    public String init() throws IOException, GeneralSecurityException {
        String generatedPassword = generateRandomPassword();
        storePassword(generateSalt(), generatedPassword);
        return generatedPassword;
    }
}
