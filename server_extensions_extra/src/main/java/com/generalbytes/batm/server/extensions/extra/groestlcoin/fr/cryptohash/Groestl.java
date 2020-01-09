package com.generalbytes.batm.server.extensions.extra.groestlcoin.fr.cryptohash;


/**
 * Created by Hash Engineering on 09/16/19 for the Groestl algorithm
 */
public class Groestl {

    private static final Groestl512 digestGroestl = new Groestl512();

    static byte [] digest(byte input[])
    {
        return digest(input, 0, input.length);
    }

    static byte [] digest(byte header[], int offset, int length)
    {
        digestGroestl.reset();
        digestGroestl.update(header, offset, length);
        byte [] hash512 = digestGroestl.digest();


        byte [] hash512_2 = digestGroestl.digest(hash512);

        byte [] result = new byte[32];

        for (int i = 0; i < 32; i++){
            result[i] = hash512_2[i];
        }
        return result;
    }
}
