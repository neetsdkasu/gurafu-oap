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
            return "Date YY";
        case Entry.DATE_YM:
            return "Date YY-MM";
        case Entry.DATE_YMD:
            return "Date YY-MM-DD";
        case Entry.DATE_YMDH:
            return "Date YY-MM-DD HH";
        case Entry.DATE_YMDHM:
            return "Date YY-MM-DD HH:MM";
        default:
            return "[UNKNOWN]";
        }
    }

    int getYear(Element e)
    {
        return getYear(xAxisType, e.x);
    }

    void setYear(Element e, int year)
    {
        e.x = setYear(xAxisType, e.x, year);
    }

    int getMonth(Element e)
    {
        return getMonth(xAxisType, e.x);
    }

    void setMonth(Element e, int month)
    {
        e.x = setMonth(xAxisType, e.x, month);
    }

    int getDay(Element e)
    {
        return getDay(xAxisType, e.x);
    }

    void setDay(Element e, int day)
    {
        e.x = setDay(xAxisType, e.x, day);
    }

    int getHour(Element e)
    {
        return getHour(xAxisType, e.x);
    }

    void setHour(Element e, int hour)
    {
        e.x = setHour(xAxisType, e.x, hour);
    }

    int getMinute(Element e)
    {
        return getMinute(xAxisType, e.x);
    }

    void setMinute(Element e, int minute)
    {
        e.x = setMinute(xAxisType, e.x, minute);
    }

    static int getMinute(int type, int x)
    {
        return type == Entry.DATE_YMDHM
             ? (x & 63)
             : 0;
    }

    static int setMinute(int type, int x, int minute)
    {
        return type == Entry.DATE_YMDHM
             ? (x ^ minute ^ (x & 63))
             : x;
    }

    static int getHour(int type, int x)
    {
        switch (type)
        {
        case Entry.DATE_YMDH:
            return x & 31;
        case Entry.DATE_YMDHM:
            return (x >> 6) & 31;
        default:
            return 0;
        }
    }

    static int setHour(int type, int x, int hour)
    {
        switch (type)
        {
        case Entry.DATE_YMDH:
            return x ^ hour ^ (x & 31);
        case Entry.DATE_YMDHM:
            return x ^ (hour << 6) ^ (x & (31 << 6));
        default:
            return x;
        }
    }

    static int getDay(int type, int x)
    {
        switch (type)
        {
        case Entry.DATE_YMD:
            return (x & 31) + 1;
        case Entry.DATE_YMDH:
            return ((x >> 5) & 31) + 1;
        case Entry.DATE_YMDHM:
            return ((x >> (5+6)) & 31) + 1;
        default:
            return 0;
        }
    }

    static int setDay(int type, int x, int day)
    {
        switch (type)
        {
        case Entry.DATE_YMD:
            return x ^ (day-1) ^ (x & 31);
        case Entry.DATE_YMDH:
            return x ^ ((day-1) << 5) ^ (x & (31 << 5));
        case Entry.DATE_YMDHM:
            return x ^ ((day-1) << (5+6)) ^ (x & (31 << (5+6)));
        default:
            return x;
        }
    }

    static int getMonth(int type, int x)
    {
        switch (type)
        {
        case Entry.DATE_YM:
            return (x & 15) + 1;
        case Entry.DATE_YMD:
            return ((x >> 5) & 15) + 1;
        case Entry.DATE_YMDH:
            return ((x >> (5+5)) & 15) + 1;
        case Entry.DATE_YMDHM:
            return ((x >> (5+5+6)) & 15) + 1;
        default:
            return 0;
        }
    }

    static int setMonth(int type, int x, int month)
    {
        switch (type)
        {
        case Entry.DATE_YM:
            return x ^ (month-1) ^ (x & 15);
        case Entry.DATE_YMD:
            return x ^ ((month-1) << 5) ^ (x & (15 << 5));
        case Entry.DATE_YMDH:
            return x ^ ((month-1) << (5+5)) ^ (x & (15 << (5+5)));
        case Entry.DATE_YMDHM:
            return x ^ ((month-1) << (5+5+6)) ^ (x & (15 << (5+5+6)));
        default:
            return x;
        }
    }

    static int getYear(int type, int x)
    {
        switch (type)
        {
        case Entry.DATE_Y:
            return x;
        case Entry.DATE_YM:
            return x >> 4;
        case Entry.DATE_YMD:
            return x >> (4+5);
        case Entry.DATE_YMDH:
            return x >> (4+5+5);
        case Entry.DATE_YMDHM:
            return x >> (4+5+5+6);
        default:
            return 0;
        }
    }

    static int setYear(int type, int x, int year)
    {
        switch (type)
        {
        case Entry.DATE_Y:
            return year;
        case Entry.DATE_YM:
            return x ^ ((year ^ (x >> 4)) << 4);
        case Entry.DATE_YMD:
            return x ^ ((year ^ (x >> (4+5))) << (4+5));
        case Entry.DATE_YMDH:
            return x ^ ((year ^ (x >> (4+5+5))) << (4+5+5));
        case Entry.DATE_YMDHM:
            return x ^ ((year ^ (x >> (4+5+5+6))) << (4+5+5+6));
        default:
            return x;
        }
    }


}