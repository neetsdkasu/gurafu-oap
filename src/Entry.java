import java.io.*;

final class Entry
{
    static final int
        POINT_0 = 0,  // 00000000.
        POINT_1 = 1,  // 0000000.0
        POINT_2 = 2,  // 000000.00
        POINT_3 = 3,  // 00000.000
        POINT_4 = 4,  // 0000.0000
        POINT_5 = 5,  // 000.00000
        POINT_6 = 6,  // 00.000000
        POINT_7 = 7,  // 0.0000000
        POINT_8 = 8,  // .00000000
        COUNTER = 9,  // 00000001 (auto incremental)
        DATE_Y = 10,     // YY
        DATE_YM = 11,    // YY-MM
        DATE_YMD = 12,   // YY-MM-DD
        DATE_YMDH = 13,  // YY-MM-DD HH
        DATE_YMDHM = 14; // YY-MM-DD HH:MM

    int id = 0;
    String title = "";
    int xAxisType = 0, yAxisType = 0;
    int lastAccess = 0;

    void writeTo(DataOutput out) throws IOException
    {
        out.writeInt(lastAccess);
        out.writeByte(xAxisType);
        out.writeByte(yAxisType);
        out.writeUTF(title);
    }

    void readFrom(DataInput in) throws IOException
    {
        lastAccess = in.readInt();
        xAxisType = in.readUnsignedByte();
        yAxisType = in.readUnsignedByte();
        title = in.readUTF();
    }

    static String getTypeDescription(int type)
    {
        switch (type)
        {
        case Entry.POINT_0:
            return "Integer 00000000";
        case Entry.POINT_1:
            return "Decimal 0000000.0";
        case Entry.POINT_2:
            return "Decimal 000000.00";
        case Entry.POINT_3:
            return "Decimal 00000.000";
        case Entry.POINT_4:
            return "Decimal 0000.0000";
        case Entry.POINT_5:
            return "Decimal 000.00000";
        case Entry.POINT_6:
            return "Decimal 00.000000";
        case Entry.POINT_7:
            return "Decimal 0.0000000";
        case Entry.POINT_8:
            return "Decimal .00000000";
        case Entry.COUNTER:
            return "Integer Counter";
        case Entry.DATE_Y:
            return "Date YYYY";
        case Entry.DATE_YM:
            return "Date YYYY-MM";
        case Entry.DATE_YMD:
            return "Date YYYY-MM-DD";
        case Entry.DATE_YMDH:
            return "Date YYYY-MM-DD HH";
        case Entry.DATE_YMDHM:
            return "Date YYYY-MM-DD HH:MM";
        default:
            return "[UNKNOWN]";
        }
    }
}