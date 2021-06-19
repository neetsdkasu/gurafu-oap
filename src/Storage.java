import java.io.*;
import javax.microedition.rms.*;

final class Storage
{
    private Storage() {}

    private static RecordStore
        mainListRS = null,
        dataRS = null;

    static Entry[] entries = null;
    static Element[] elements = null;
    static int[] intervals = null;

    static final String[]
        scaleX = new String[5],
        scaleY = new String[9];

    static Element
        minElement = null,
        maxElement = null;

    static int
        unit = 1,
        top = 1,
        bottom = 0;

    static long[]
        averageY = null;

    static void init()
    {
        try
        {
            mainListRS = RecordStore.openRecordStore("graph.mainlist", true);
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex.toString());
        }
    }

    static void release()
    {
        mainListRS = close(mainListRS);
        dataRS = close(dataRS);
    }

    private static int readInt(byte[] buf, int i)
    {
        return (((int)buf[i]   & 0xFF) << 24)
             | (((int)buf[i+1] & 0xFF) << 16)
             | (((int)buf[i+2] & 0xFF) <<  8)
             |  ((int)buf[i+3] & 0xFF);
    }

    private static final RecordComparator
        lastAccessOrder = new RecordComparator(){
            public int compare(byte[] rec1, byte[] rec2)
            {
                int t1 = Storage.readInt(rec1, 0);
                int t2 = Storage.readInt(rec2, 0);
                if (t1 > t2)
                {
                    return RecordComparator.PRECEDES;
                }
                else
                {
                    return t1 == t2
                         ? RecordComparator.EQUIVALENT
                         : RecordComparator.FOLLOWS;
                }
            }
        },
        xAxisOrder = new RecordComparator(){
            public int compare(byte[] rec1, byte[] rec2)
            {
                int x1 = Storage.readInt(rec1, 0);
                int x2 = Storage.readInt(rec2, 0);
                if (x1 < x2)
                {
                    return RecordComparator.PRECEDES;
                }
                else
                {
                    return x1 == x2
                         ? RecordComparator.EQUIVALENT
                         : RecordComparator.FOLLOWS;
                }
            }
        };

    static boolean existsEntry(int index)
    {
        return index >= 0
            && entries != null
            && index < entries.length;
    }

    static boolean saveEntry(Entry e)
    {
        ByteArrayOutputStream baos = null;
        try
        {
            if (entries == null || entries.length == 0)
            {
                e.lastAccess = 1;
            }
            else
            {
                e.lastAccess = entries[0].lastAccess + 1;
            }
            baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos);
            e.writeTo(dos);
            dos.flush();
            byte[] data = baos.toByteArray();
            if (e.id == 0)
            {
                e.id = mainListRS.addRecord(data, 0, data.length);
            }
            else
            {
                mainListRS.setRecord(e.id, data, 0, data.length);
            }
            return true;
        }
        catch (RecordStoreFullException _)
        {
            return false;
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex.toString());
        }
        finally
        {
            if (baos != null)
            {
                try
                {
                    baos.close();
                }
                catch (Exception __)
                {
                    // do nothing
                }
            }
        }
    }

    static void openData(Entry entry)
    {
        try
        {
            dataRS = RecordStore.openRecordStore(
                "graph.data." + Integer.toString(entry.id),
                true
            );
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex.toString());
        }
    }

    static boolean saveData(Element e)
    {
        ByteArrayOutputStream baos = null;
        try
        {
            baos = new ByteArrayOutputStream(8);
            DataOutputStream dos = new DataOutputStream(baos);
            e.writeTo(dos);
            dos.flush();
            byte[] data = baos.toByteArray();
            if (e.id == 0)
            {
                e.id = dataRS.addRecord(data, 0, data.length);
            }
            else
            {
                dataRS.setRecord(e.id, data, 0, data.length);
            }
            return true;
        }
        catch (RecordStoreFullException _)
        {
            return false;
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex.toString());
        }
        finally
        {
            if (baos != null)
            {
                try
                {
                    baos.close();
                }
                catch (Exception __)
                {
                    // do nothing
                }
            }
        }
    }

    static void closeData()
    {
        dataRS = close(dataRS);
    }

    static int lowerBound(int xValue)
    {
        if (elements == null || elements.length == 0)
        {
            return 0;
        }
        for (int i = 0; i < elements.length; i++)
        {
            if (xValue <= elements[i].x)
            {
                return i;
            }
        }
        return elements.length - 1;
    }

    static int upperBound(int xValue)
    {
        if (elements == null || elements.length == 0)
        {
            return 0;
        }
        for (int i = elements.length - 1; i >= 0; i--)
        {
            if (elements[i].x <= xValue)
            {
                return i;
            }
        }
        return 0;
    }

    static int rangeSize(int begin, int end)
    {
        if (elements == null || elements.length == 0)
        {
            return 0;
        }
        return upperBound(end) - lowerBound(begin) + 1;
    }

    static Element getFirstElement()
    {
        if (elements == null || elements.length == 0)
        {
            return null;
        }
        return elements[0];
    }

    static Element getLastElement()
    {
        if (elements == null || elements.length == 0)
        {
            return null;
        }
        return elements[elements.length - 1];
    }

    static void calcIntervals(Entry e)
    {
        if (intervals == null || intervals.length != elements.length)
        {
            intervals = new int[elements.length];
        }
        for (int i = 1; i < elements.length; i++)
        {
            intervals[i] = intervals[i-1];
            intervals[i] += e.interval(elements[i], elements[i-1]);
        }
    }

    static int positionY(int y)
    {
        return 220 - (int)(200L * (long)(y - bottom) / (long)(top - bottom));
    }

    static int getInterval(int left, int right)
    {
        return intervals[right] - intervals[left];
    }

    static void calcUnit()
    {
        int maxY = maxElement.y;
        int minY = minElement.y;
        if (maxY - minY < 38)
        {
            int d = 38 - (maxY - minY);
            maxY += d - d / 2;
            minY -= d / 2;
        }
        int p = 1;
        int maxR = 0;
        unit = 1;
        top = maxY;
        bottom = minY;
        for (int i = 0; i < 7; i++)
        {
            for (int k = 1; k < 10; k++)
            {
                int s = k * p;
                int t = (maxY / s) * s + s;
                int b = (minY / s) * s - s;
                int r = (t - b) / s;
                if (r <= 40 && r > maxR)
                {
                    maxR = r;
                    unit = s;
                    top = t;
                    bottom = b;
                }
            }
            p *= 10;
        }
        if (maxR < 40)
        {
            int d = 40 - maxR;
            top += (d - d / 2) * unit;
            bottom -= (d / 2) * unit;
        }
        // System.out.println("max" + maxY);
        // System.out.println("min" + minY);
        // System.out.println("unit" + unit);
        // System.out.println("top" + top);
        // System.out.println("bottom" + bottom);
        // System.out.println("maxR" + maxR);
    }

    static int getAverageY()
    {
        return getAverageY(0, elements.length - 1);
    }

    static int getAverageY(int left, int right)
    {
        return (int)((averageY[right + 1] - averageY[left]) / (long)(right - left + 1));
    }

    static void deleteElement(int id)
    {
        try
        {
            dataRS.deleteRecord(id);
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex.toString());
        }
    }

    static void loadElements()
    {
        RecordEnumeration re = null;
        try
        {
            int n = dataRS.getNumRecords();

            if (elements == null || elements.length != n)
            {
                elements = new Element[n];
                averageY = new long[n+1];
            }

            minElement = null;
            maxElement = null;

            re = dataRS.enumerateRecords(null, xAxisOrder, false);
            int i = 0;
            while (re.hasNextElement())
            {
                Element e = elements[i];
                if (e == null)
                {
                    e = new Element();
                    elements[i] = e;
                }
                e.id = re.nextRecordId();
                byte[] data = dataRS.getRecord(e.id);
                ByteArrayInputStream bais = new ByteArrayInputStream(data);
                DataInputStream dis = new DataInputStream(bais);
                e.readFrom(dis);
                i++;
                if (minElement == null || e.y < minElement.y)
                {
                    minElement = e;
                }
                if (maxElement == null || e.y > maxElement.y)
                {
                    maxElement = e;
                }
                averageY[i] = averageY[i-1] + (long)e.y;
            }
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex.toString());
        }
        finally
        {
            if (re != null)
            {
                re.destroy();
            }
        }
    }

    static void deleteEntry(int id)
    {
        try
        {
            closeData();
            RecordStore.deleteRecordStore(
                "graph.data." + Integer.toString(id)
            );
        }
        catch (RecordStoreNotFoundException _)
        {
            // do nothing
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex.toString());
        }

        try
        {
            mainListRS.deleteRecord(id);
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex.toString());
        }

    }

    static void loadEntries()
    {
        RecordEnumeration re = null;
        try
        {
            int n = mainListRS.getNumRecords();

            if (entries == null || entries.length != n)
            {
                entries = new Entry[n];
            }

            re = mainListRS.enumerateRecords(null, lastAccessOrder, false);
            int i = 0;
            while (re.hasNextElement())
            {
                Entry e = entries[i];
                if (e == null)
                {
                    e = new Entry();
                    entries[i] = e;
                }
                e.id = re.nextRecordId();
                byte[] data = mainListRS.getRecord(e.id);
                ByteArrayInputStream bais = new ByteArrayInputStream(data);
                DataInputStream dis = new DataInputStream(bais);
                e.readFrom(dis);
                i++;
            }
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex.toString());
        }
        finally
        {
            if (re != null)
            {
                re.destroy();
            }
        }
    }

    private static RecordStore close(RecordStore rs)
    {
        try
        {
            if (rs != null)
            {
                rs.closeRecordStore();
            }
        }
        catch (Exception _)
        {
        }
        return null;
    }

    static void calcScaleY(Entry e)
    {
        int y = top;
        for (int i = 0; i < 9; i++)
        {
            scaleY[i] = e.scaleY(y);
            y -= unit * 5;
        }
    }

    static void calcScaleX(Entry e, int p, boolean leftEnd)
    {
        int x = elements[p].x;
        switch (e.xAxisType)
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
            {
                int s = x - (leftEnd ? 1 : 39);
                for (int i = 0; i < 5; i++)
                {
                    scaleX[i] = Integer.toString(s);
                    s += 10;
                }
            }
            break;
        case Entry.DATE_YMDHM:
            {
                int minute = (Element.getMinute(x)
                                - (leftEnd ? 1 : 39)
                                + 60
                            ) % 60;
                for (int i = 0; i < 5; i++)
                {
                    scaleX[i] = Integer.toString(minute);
                    minute = (minute + 10) % 60;
                }
            }
            break;
        case Entry.DATE_YMDH:
            {
                int hour = (Element.getHour(x)
                                - (leftEnd ? 1 : 39)
                                + 48
                            ) % 24;
                for (int i = 0; i < 5; i++)
                {
                    scaleX[i] = Integer.toString(hour);
                    hour = (hour + 10) % 24;
                }
            }
            break;
        case Entry.DATE_YMD:
            {
                int year = Element.getYear(x);
                int month = Element.getMonth(x);
                int day = Element.getDay(x) - (leftEnd ? 1 : 39);
                while (day <= 0)
                {
                    month--;
                    if (month == 0)
                    {
                        year--;
                        month = 12;
                    }
                    day += Entry.getDaysOfMonth(year, month);
                }
                int mdays = Entry.getDaysOfMonth(year, month);
                for (int i = 0; i < 5; i++)
                {
                    scaleX[i] = Integer.toString(day);
                    day += 10;
                    if (day > mdays)
                    {
                        day -= mdays;
                        month++;
                        if (month > 12)
                        {
                            year++;
                            month = 1;
                        }
                        mdays = Entry.getDaysOfMonth(year, month);
                    }
                }
            }
            break;
        case Entry.DATE_YM:
            {
                int month = ((Element.getMonth(x) - 1)
                                - (leftEnd ? 1 : 39)
                                + 60
                            ) % 12;
                for (int i = 0; i < 5; i++)
                {
                    scaleX[i] = Integer.toString(month + 1);
                    month = (month + 10) % 12;
                }
            }
            break;
        case Entry.DATE_Y:
            {
                int year = Element.getYear(x)
                         - (leftEnd ? 1 : 39);
                for (int i = 0; i < 5; i++)
                {
                    scaleX[i] = Integer.toString(
                        year % 100 + 100
                    ).substring(1);
                    year += 10;
                }
            }
            break;
        default:
            break;
        }
    }
}