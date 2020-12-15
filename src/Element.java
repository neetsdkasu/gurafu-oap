import java.io.*;
import java.util.*;

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

    void init(Entry entry)
    {
        Calendar cal = getCalnder();
        switch (entry.xAxisType)
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
                x = el.x + 1;
            }
            else
            {
                x = 1;
            }
            break;
        case Entry.DATE_YMDHM:
            entry.setMinute(this, cal.get(Calendar.MINUTE));
        case Entry.DATE_YMDH:
            entry.setHour(this, cal.get(Calendar.HOUR_OF_DAY));
        case Entry.DATE_YMD:
            entry.setDay(this, cal.get(Calendar.DAY_OF_MONTH));
        case Entry.DATE_YM:
            entry.setMonth(this, cal.get(Calendar.MONTH)+1);
        case Entry.DATE_Y:
            entry.setYear(this, cal.get(Calendar.YEAR));
            break;
        default:
            break;
        }
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