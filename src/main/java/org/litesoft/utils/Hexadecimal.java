package org.litesoft.utils;

import java.nio.charset.StandardCharsets;

public class Hexadecimal {
    public static String encode( String source ) {
        if (source == null) {
            return null;
        }
        byte[] bytes = source.getBytes( StandardCharsets.UTF_8 );
        StringBuilder sb = new StringBuilder( bytes.length * 2 );
        for ( byte zByte : bytes ) {
            sb.append( toHexDigit( zByte >> 4 ) ); // Hi Nibble
            sb.append( toHexDigit( zByte ) ); // Low Nibble
        }
        return sb.toString();
    }

    public static String decode( String encoded ) {
        if (encoded == null) {
            return null;
        }
        if ( !Ints.isEven( encoded.length() ) ) {
            throw new IllegalArgumentException( "Invalid encoding: " + encoded );
        }
        byte[] bytes = new byte[encoded.length() >> 1]; // divide by two
        int sourceOffset = 0;
        try {
            for ( int i = 0; i < bytes.length; i++ ) {
                int hiNibble = fromHexDigit( encoded.charAt( sourceOffset++) ) << 4;
                int lowNibble = fromHexDigit( encoded.charAt( sourceOffset++) );
                bytes[i] = (byte)(hiNibble + lowNibble);
            }
        }
        catch ( IllegalArgumentException e ) {
            throw new IllegalArgumentException( e.getMessage() + ": " + encoded );
        }
        return new String( bytes, StandardCharsets.UTF_8 );
    }

    public static int fromHexDigit( char value ) {
        if ( ('0' <= value) && (value <= '9') ) {
            return value - '0';
        }
        if ( ('A' <= value) && (value <= 'F') ) {
            return 10 + (value - 'A');
        }
        throw new IllegalArgumentException( "Invalid encoding of non-hex digit ('" + value + "')" );
    }

    public static char toHexDigit( int value ) {
        int lowBits = value & 15;
        return HEX_DIGITS[lowBits];
    }

    private static final char[] HEX_DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
}
