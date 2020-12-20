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

    String scaleX(int v)
    {
        return scale(xAxisType, v);
    }

    String scaleY(int v)
    {
        return scale(yAxisType, v);
    }

    static final int[] SCALE = new int[]{
        1,
        10,
        100,
        1000,
        10000,
        100000,
        1000000,
        10000000,
        100000000
    };

    static String scale(int type, int v)
    {
        switch (type)
        {
        case Entry.POINT_0:
            return Integer.toString(v);
        case Entry.POINT_1:
        case Entry.POINT_2:
        case Entry.POINT_4:
        case Entry.POINT_5:
        case Entry.POINT_6:
        case Entry.POINT_7:
        case Entry.POINT_8:
            String sf = v < 0 ? "-" : "";
            v = Math.abs(v);
            int s = SCALE[type];
            String st = sf + Integer.toString(v / s)
                + "."
                + Integer.toString(v % s + s).substring(1);
            for (int i = st.length() - 1; i >= 0; i--)
            {
                if (st.charAt(i) != '0')
                {
                    st = st.substring(0, i + 1);
                    break;
                }
            }
            return st;
        case Entry.COUNTER:
            return Integer.toString(v);
        case Entry.DATE_YMDHM:
            return Integer.toString(Element.getMinute(v));
        case Entry.DATE_YMDH:
            return Integer.toString(Element.getHour(v));
        case Entry.DATE_YMD:
            return Integer.toString(Element.getDay(v));
        case Entry.DATE_YM:
            return Integer.toString(Element.getMonth(v));
        case Entry.DATE_Y:
            return Integer.toString(Element.getYear(v%100+100)).substring(1);
        default:
            return "";
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
            element.setMinute(cal.get(Calendar.MINUTE));
        case Entry.DATE_YMDH:
            element.setHour(cal.get(Calendar.HOUR_OF_DAY));
        case Entry.DATE_YMD:
            element.setDay(cal.get(Calendar.DAY_OF_MONTH));
        case Entry.DATE_YM:
            element.setMonth(cal.get(Calendar.MONTH)+1);
        case Entry.DATE_Y:
            element.setYear(cal.get(Calendar.YEAR));
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

    static boolean isLeapYear(int year)
    {
        return year % 4 == 0
            && (year % 400 == 0 || year % 100 != 0);
    }

    static boolean isOverLeap(Element e)
    {
        return isOverLeap(e.getYear(), e.getMonth());
    }

    static boolean isOverLeap(int year, int month)
    {
        return isLeapYear(year) && month > 2;
    }

    static int getDaysOfMonth(int year, int month)
    {
        return DAYSUM[month]
             - DAYSUM[month - 1]
             + (isLeapYear(year) && month == 2 ? 1 : 0);
    }

    static final int[] DAYSUM = new int[]{
        0,
        31,
        31+28,
        31+28+31,
        31+28+31+30,
        31+28+31+30+31,
        31+28+31+30+31+30,
        31+28+31+30+31+30+31,
        31+28+31+30+31+30+31+31,
        31+28+31+30+31+30+31+31+30,
        31+28+31+30+31+30+31+31+30+31,
        31+28+31+30+31+30+31+31+30+31+30,
        31+28+31+30+31+30+31+31+30+31+30+31
    };

    static int diffYMD(Element e1, Element e2)
    {
        int diff = (DAYSUM[e1.getMonth()] + (isOverLeap(e1) ? 1 : 0))
                 - (DAYSUM[e2.getMonth()] + (isOverLeap(e2) ? 1 : 0))
                 + (e1.getDay() - e2.getDay());
        for (int year = e2.getYear(); year < e1.getYear(); year++)
        {
            diff += isLeapYear(year) ? 366 : 365;
        }
        return diff;
    }

    int interval(Element e1, Element e2)
    {
        if (e1.x < e2.x)
        {
            return -interval(e2, e1);
        }
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
        case Entry.COUNTER:
            return e1.x - e2.x;
        case Entry.DATE_YMDHM:
            return diffYMD(e1, e2) * 24 * 60
                 + (e1.getHour() - e2.getHour()) * 60
                 + (e1.getMinute() - e2.getMinute());
        case Entry.DATE_YMDH:
            return diffYMD(e1, e2) * 24
                 + (e1.getHour() - e2.getHour());
        case Entry.DATE_YMD:
            return diffYMD(e1, e2);
        case Entry.DATE_YM:
            return 12 * (e1.getYear() - e2.getYear())
                 + (e1.getMonth() - e2.getMonth());
        case Entry.DATE_Y:
            return e1.getYear() - e2.getYear();
        default:
            return 0;
        }
    }

    String valueXString(Element e)
    {
        return valueString(xAxisType, e.x);
    }

    String valueYString(Element e)
    {
        return valueString(yAxisType, e.y);
    }

    static String valueString(int type, int v)
    {
        String st = "";
        switch (type)
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
            String sf = v < 0 ? "-" : "+";
            v = Math.abs(v);
            int s = SCALE[type];
            return sf + Integer.toString(v / s)
                + "."
                + Integer.toString(v % s + s).substring(1);
        case Entry.COUNTER:
            return Integer.toString(v);
        case Entry.DATE_YMDHM:
            st = ":" + Integer.toString(
                Element.getMinute(v) % 100 + 100
            ).substring(1);
        case Entry.DATE_YMDH:
            st = " " + Integer.toString(
                Element.getHour(v) % 100 + 100
            ).substring(1) + st;
        case Entry.DATE_YMD:
            st = "-" + Integer.toString(
                Element.getDay(v) % 100 + 100
            ).substring(1) + st;
        case Entry.DATE_YM:
            st = "-" + Integer.toString(
                Element.getMonth(v) % 100 + 100
            ).substring(1) + st;
        case Entry.DATE_Y:
            return Integer.toString(Element.getYear(v)) + st;
        default:
            return "";
        }
    }
}