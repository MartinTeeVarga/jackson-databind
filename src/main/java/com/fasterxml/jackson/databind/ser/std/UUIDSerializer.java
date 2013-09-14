package com.fasterxml.jackson.databind.ser.std;

import java.io.IOException;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.*;

public class UUIDSerializer
    extends StdScalarSerializer<UUID>
{
    final static char[] HEX_CHARS = "01234567890abcdef".toCharArray();
    
    public UUIDSerializer() { super(UUID.class); }

    @Override
    public boolean isEmpty(UUID value)
    {
        if (value == null) {
            return true;
        }
        // Null UUID is empty, so...
        if (value.getLeastSignificantBits() == 0L
                && value.getMostSignificantBits() == 0L) {
            return true;
        }
        return false;
    }
    
    @Override
    public void serialize(UUID value, JsonGenerator jgen, SerializerProvider provider)
        throws IOException, JsonGenerationException
    {
        // UUID.toString() works ok but we can make it go faster...

        final char[] ch = new char[36];
        final long msb = value.getMostSignificantBits();
        _appendInt((int) (msb >> 32), ch, 0);
        ch[8] = '-';
        int i = (int) msb;
        _appendShort(i >>> 16, ch, 9);
        ch[13] = '-';
        _appendShort(i, ch, 14);
        ch[18] = '-';

        final long lsb = value.getLeastSignificantBits();
        _appendShort((int) (lsb >>> 48), ch, 19);
        ch[23] = '-';
        _appendShort((int) (lsb >>> 32), ch, 24);
        _appendInt((int) lsb, ch, 28);

        jgen.writeString(ch, 0, 36);
    }

    private static void _appendInt(int bits, char[] ch, int offset)
    {
        _appendShort(bits >> 16, ch, offset);
        _appendShort(bits, ch, offset+4);
    }
    
    private static void _appendShort(int bits, char[] ch, int offset)
    {
        ch[offset] = HEX_CHARS[(bits >> 12) & 0xF];
        ch[++offset] = HEX_CHARS[(bits >> 8) & 0xF];
        ch[++offset] = HEX_CHARS[(bits >> 4) & 0xF];
        ch[++offset] = HEX_CHARS[bits  & 0xF];

    }
}
