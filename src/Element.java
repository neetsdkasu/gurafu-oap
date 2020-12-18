import java.io.*;

final class Element
{
    int id = 0, x = 0, y = 0;

    void writeTo(DataOutput out) throws IOException
    {
        out.writeInt(x);
        out.writeInt(y);
    }

    void readFrom(DataInput in) throws IOException
    {
        x = in.readInt();
        y = in.readInt();
    }

    int getYear()
    {
        return getYear(x);
    }

    void setYear(int year)
    {
        x = setYear(x, year);
    }

    int getMonth()
    {
        return getMonth(x);
    }

    void setMonth(int month)
    {
        x = setMonth(x, month);
    }

    int getDay()
    {
        return getDay(x);
    }

    void setDay(int day)
    {
        x = setDay(x, day);
    }

    int getHour()
    {
        return getHour(x);
    }

    void setHour(int hour)
    {
        x = setHour(x, hour);
    }

    int getMinute()
    {
        return getMinute(x);
    }

    void setMinute(int minute)
    {
        x = setMinute(x, minute);
    }

    static int getMinute(int x)
    {
        return x & 63;
    }

    static int setMinute(int x, int minute)
    {
        return x ^ minute ^ (x & 63);
    }

    static int getHour(int x)
    {
        return (x >> 6) & 31;
    }

    static int setHour(int x, int hour)
    {
        return x ^ (hour << 6) ^ (x & (31 << 6));
    }

    static int getDay(int x)
    {
        return ((x >> (5+6)) & 31) + 1;
    }

    static int setDay(int x, int day)
    {
        return x ^ ((day-1) << (5+6)) ^ (x & (31 << (5+6)));
    }

    static int getMonth(int x)
    {
        return ((x >> (5+5+6)) & 15) + 1;
    }

    static int setMonth(int x, int month)
    {
        return x ^ ((month-1) << (5+5+6)) ^ (x & (15 << (5+5+6)));
    }

    static int getYear(int x)
    {
        return x >> (4+5+5+6);
    }

    static int setYear(int x, int year)
    {
        return x ^ ((year ^ (x >> (4+5+5+6))) << (4+5+5+6));
    }

}