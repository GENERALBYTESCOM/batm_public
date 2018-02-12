package com.generalbytes.batm.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

import javax.crypto.Cipher
import javax.crypto.CipherOutputStream
import javax.crypto.spec.SecretKeySpec

class EncryptFile extends DefaultTask {
    public static final int REQUIRED_KEY_LENGTH = 32
    private Object outputFile;
    private Object inputFile;
    private byte[] key = null;

    @OutputFile
    File getOutputFile() {
        return getProject().file(outputFile);
    }

    void setOutputFile(Object outputFile) {
        this.outputFile = outputFile;
    }

    @InputFile
    File getInputFile() {
        return getProject().file(inputFile)
    }

    void setInputFile(Object inputFile) {
        this.inputFile = inputFile
    }

    @Input
    byte[] getKey() {
        return key
    }

    void setKey(byte[] key) {
        if (key == null) {
            throw new IllegalArgumentException("Key can't be null.")
        }
        if (key.length != REQUIRED_KEY_LENGTH) {
            throw new IllegalArgumentException("Incorrect key length (${key.length}); should be ${REQUIRED_KEY_LENGTH}.")
        }
        this.key = key
    }

    @TaskAction
    void encrypt() throws IOException {
        if (key == null || key.length != REQUIRED_KEY_LENGTH) {
            throw new IllegalStateException("Invalid key: ${key}")
        }

        // Length is 16 byte
        final SecretKeySpec sks = new SecretKeySpec(key, "AES");

        // Create cipher
        final Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, sks);

        getInputFile().withInputStream { is ->
            getOutputFile().withOutputStream { os ->
                // Wrap the output stream
                new CipherOutputStream(os, cipher).withStream { cos ->
                    // Write bytes
                    int b;
                    byte[] d = new byte[8];
                    while((b = is.read(d)) != -1) {
                        cos.write(d, 0, b);
                    }
                }
            }
        }
    }
}
