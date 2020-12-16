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

}