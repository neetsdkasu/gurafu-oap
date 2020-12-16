import java.io.*;
import java.util.*;

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
        DATE_Y = 14,     // YY
        DATE_YM = 13,    // YY-MM
        DATE_YMD = 12,   // YY-MM-DD
        DATE_YMDH = 11,  // YY-MM-DD HH
        DATE_YMDHM = 10; // YY-MM-DD HH:MM

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
        case Entry.DATE_YMDHM:
            return "Date YY-MM-DD HH:MM";
        case Entry.DATE_YMDH:
            return "Date YY-MM-DD HH";
        case Entry.DATE_YMD:
            return "Date YY-MM-DD";
        case Entry.DATE_YM:
            return "Date YY-MM";
        case Entry.DATE_Y:
            return "Date YY";
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
        case Entry.DATE_YMDHM:
            return (x >> 6) & 31;
        case Entry.DATE_YMDH:
            return x & 31;
        default:
            return 0;
        }
    }

    static int setHour(int type, int x, int hour)
    {
        switch (type)
        {
        case Entry.DATE_YMDHM:
            return x ^ (hour << 6) ^ (x & (31 << 6));
        case Entry.DATE_YMDH:
            return x ^ hour ^ (x & 31);
        default:
            return x;
        }
    }

    static int getDay(int type, int x)
    {
        switch (type)
        {
        case Entry.DATE_YMDHM:
            return ((x >> (5+6)) & 31) + 1;
        case Entry.DATE_YMDH:
            return ((x >> 5) & 31) + 1;
        case Entry.DATE_YMD:
            return (x & 31) + 1;
        default:
            return 0;
        }
    }

    static int setDay(int type, int x, int day)
    {
        switch (type)
        {
        case Entry.DATE_YMDHM:
            return x ^ ((day-1) << (5+6)) ^ (x & (31 << (5+6)));
        case Entry.DATE_YMDH:
            return x ^ ((day-1) << 5) ^ (x & (31 << 5));
        case Entry.DATE_YMD:
            return x ^ (day-1) ^ (x & 31);
        default:
            return x;
        }
    }

    static int getMonth(int type, int x)
    {
        switch (type)
        {
        case Entry.DATE_YMDHM:
            return ((x >> (5+5+6)) & 15) + 1;
        case Entry.DATE_YMDH:
            return ((x >> (5+5)) & 15) + 1;
        case Entry.DATE_YMD:
            return ((x >> 5) & 15) + 1;
        case Entry.DATE_YM:
            return (x & 15) + 1;
        default:
            return 0;
        }
    }

    static int setMonth(int type, int x, int month)
    {
        switch (type)
        {
        case Entry.DATE_YMDHM:
            return x ^ ((month-1) << (5+5+6)) ^ (x & (15 << (5+5+6)));
        case Entry.DATE_YMDH:
            return x ^ ((month-1) << (5+5)) ^ (x & (15 << (5+5)));
        case Entry.DATE_YMD:
            return x ^ ((month-1) << 5) ^ (x & (15 << 5));
        case Entry.DATE_YM:
            return x ^ (month-1) ^ (x & 15);
        default:
            return x;
        }
    }

    static int getYear(int type, int x)
    {
        switch (type)
        {
        case Entry.DATE_YMDHM:
            return x >> (4+5+5+6);
        case Entry.DATE_YMDH:
            return x >> (4+5+5);
        case Entry.DATE_YMD:
            return x >> (4+5);
        case Entry.DATE_YM:
            return x >> 4;
        case Entry.DATE_Y:
            return x;
        default:
            return 0;
        }
    }

    static int setYear(int type, int x, int year)
    {
        switch (type)
        {
        case Entry.DATE_YMDHM:
            return x ^ ((year ^ (x >> (4+5+5+6))) << (4+5+5+6));
        case Entry.DATE_YMDH:
            return x ^ ((year ^ (x >> (4+5+5))) << (4+5+5));
        case Entry.DATE_YMD:
            return x ^ ((year ^ (x >> (4+5))) << (4+5));
        case Entry.DATE_YM:
            return x ^ ((year ^ (x >> 4)) << 4);
        case Entry.DATE_Y:
            return year;
        default:
            return x;
        }
    }

    Element newElement()
    {
        Element element = new Element();
        Calendar cal = getCalnder();
        switch (xAxisType)
        {
        case Entry.POINT_0:
        case Entry.POINT_1:
        case Entry.POINT_2:
        case Entry.POINT_3:
        case Entry.POINT_4:
        case Entry.POINT_5:
        case Entry.POINT_6:
        case Entry.POINT_7:
        case Entry.POINT_8:
            break;
        case Entry.COUNTER:
            Storage.loadElements();
            Element el = Storage.getLastElement();
            if (el != null)
            {
                element.x = el.x + 1;
            }
            else
            {
                element.x = 1;
            }
            break;
        case Entry.DATE_YMDHM:
            setMinute(element, cal.get(Calendar.MINUTE));
        case Entry.DATE_YMDH:
            setHour(element, cal.get(Calendar.HOUR_OF_DAY));
        case Entry.DATE_YMD:
            setDay(element, cal.get(Calendar.DAY_OF_MONTH));
        case Entry.DATE_YM:
            setMonth(element, cal.get(Calendar.MONTH)+1);
        case Entry.DATE_Y:
            setYear(element, cal.get(Calendar.YEAR));
            break;
        default:
            break;
        }
        return element;
    }

    static Calendar getCalnder()
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        if (cal.get(Calendar.YEAR) < 2020)
        {
            cal.set(Calendar.YEAR, 2021);
        }
        return cal;
    }
}