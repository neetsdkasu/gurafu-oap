import java.io.*;
import javax.microedition.rms.*;

final class Storage
{
    private Storage() {}

    private static RecordStore
        mainListRS = null;

    static Entry[] entries = null;

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
        };

    static boolean existsEntry(int index)
    {
        return index >= 0
            && entries != null
            && index < entries.length;
    }

    static void loadEntries()
    {
        RecordEnumeration re = null;
        try
        {
            int n = mainListRS.getNumRecords();

            entries = new Entry[n];

            re = mainListRS.enumerateRecords(null, lastAccessOrder, false);
            int i = 0;
            while (re.hasNextElement())
            {
                entries[i] = new Entry();
                entries[i].id = re.nextRecordId();
                byte[] data = mainListRS.getRecord(entries[i].id);
                ByteArrayInputStream bais = new ByteArrayInputStream(data);
                DataInputStream dis = new DataInputStream(bais);
                entries[i].readFrom(dis);
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
}