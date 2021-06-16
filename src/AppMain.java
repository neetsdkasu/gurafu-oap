import javax.microedition.lcdui.*;
import javax.microedition.lcdui.game.*;

final class AppMain extends GameCanvas
{
    static final int
        STATE_MAIN_MENU = 0,
        STATE_NEW_DATASET = 1,
        STATE_DATASET_MENU = 2,
        STATE_ADD_DATA = 3,
        STATE_SHOW_GRAPH = 4;

    static final int
        DISP_W = 240,
        DISP_H = 268,
        KEY_CLR = -8;

    static final Font
        SMALL_FONT = Font.getFont(
            Font.FACE_SYSTEM,
            Font.STYLE_PLAIN,
            GraphMIDlet.WTK ? Font.SIZE_MEDIUM : Font.SIZE_SMALL
        );

    private static int
        appState = STATE_MAIN_MENU,
        sel = 0,
        viewTop = 0,
        leftEnd = 0,
        rightEnd = 0,
        avgAllY = 0,
        avgViewY = 0;

    private static Entry
        curEntry = null;

    private static Element
        curElement = null;

    private static String
        valueX = "",
        valueY = "",
        valueMaxY = "",
        valueMinY = "",
        valueAvgAllY = "",
        valueAvgViewY = "";

    private static String[][]
        values = null;

    private static boolean
        narrowView = false;

    AppMain()
    {
        super(false);
        Storage.loadEntries();

        render();
    }

    void setNewTitle(String title)
    {
        appState = STATE_NEW_DATASET;
        curEntry = new Entry();
        curEntry.title = title;
        render();
    }

    private void export()
    {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < Storage.elements.length; i++)
        {
            Element e = Storage.elements[i];
            sb.append(curEntry.valueXString(e))
                .append(',')
                .append(curEntry.valueYString(e))
                .append('\n');
        }
        GraphMIDlet.showExportTextBox(sb.toString());
    }

    protected void keyRepeated(int keyCode)
    {
        if (getGameAction(keyCode) != FIRE)
        {
            keyPressed(keyCode);
        }
    }

    protected void keyPressed(int keyCode)
    {
        if (getTicker() != null)
        {
            setTicker(null);
        }
        switch (appState)
        {
        case STATE_MAIN_MENU:
            keyPressedOnMainMenu(keyCode);
            break;
        case STATE_NEW_DATASET:
            keyPressedOnNewDataset(keyCode);
            break;
        case STATE_DATASET_MENU:
            keyPressedOnDatasetMenu(keyCode);
            break;
        case STATE_ADD_DATA:
            keyPressedOnAddData(keyCode);
            break;
        case STATE_SHOW_GRAPH:
            keyPressedOnShowGraph(keyCode);
            break;
        case 5:
            keyPressedAppState_5(keyCode);
            break;
        case 6:
            keyPressedOnAddData(keyCode);
            break;
        case 7:
        case 8:
            keyPressedAppState_7(keyCode);
            break;
        default:
            break;
        }
    }

    private void render()
    {
        Graphics g = getGraphics();

        g.setColor(0x000000);
        g.fillRect(0, 0, DISP_W, DISP_H);

        g.setFont(SMALL_FONT);

        switch (appState)
        {
        case STATE_MAIN_MENU:
            renderForMainMenu(g);
            break;
        case STATE_NEW_DATASET:
            renderForNewDataset(g);
            break;
        case STATE_DATASET_MENU:
            renderForDatasetMenu(g);
            break;
        case STATE_ADD_DATA:
            renderForAddData(g);
            break;
        case STATE_SHOW_GRAPH:
            renderForShowGraph(g);
            break;
        case 5:
            renderAppState_5(g);
            break;
        case 6:
            renderForAddData(g);
            break;
        case 7:
        case 8:
            renderAppState_7(g);
            break;
        default:
            break;
        }

        flushGraphics();
    }

    void renderAppState_7(Graphics g)
    {
        g.setColor(0xFFFFFF);

        g.drawString(
            curEntry.title,
            20,
            0,
            Graphics.LEFT|Graphics.TOP
        );

        final int h = SMALL_FONT.getHeight();

        if (appState == 7)
        {
            g.drawString(
                "X-axis",
                15,
                2*h,
                Graphics.LEFT|Graphics.BOTTOM
            );
            g.drawString(
                "Y-axis",
                DISP_W/2+5,
                2*h,
                Graphics.LEFT|Graphics.BOTTOM
            );

            g.setColor(0xFFFFFF);
            g.fillRect(10, 2*h, DISP_W - 20, h);
            g.setColor(0x777777);
            g.drawRect(10, 2*h, DISP_W - 20, h);
            g.setColor(0x000000);
            g.drawString(
                valueX,
                DISP_W/2-5,
                3*h,
                Graphics.RIGHT|Graphics.BOTTOM
            );
            g.drawString(
                valueY,
                DISP_W - 15,
                3*h,
                Graphics.RIGHT|Graphics.BOTTOM
            );
            g.drawLine(DISP_W/2, 2*h, DISP_W/2, 3*h);
        }

        renderButton(g, "DELETE", sel == 0, 5*h);
        renderButton(g, "CANCEL", sel == 1, 7*h);
    }

    void renderAppState_5(Graphics g)
    {
        g.setColor(0xFFFFFF);

        g.drawString(
            curEntry.title,
            20,
            0,
            Graphics.LEFT|Graphics.TOP
        );

        final int h = SMALL_FONT.getHeight();

        if (viewTop < Storage.elements.length-1)
        {
            g.setColor(0xFFFFFF);
            g.fillTriangle(
                DISP_W-5,
                2*h,
                DISP_W,
                2*h+h,
                DISP_W-10,
                2*h+h
            );
        }

        if (viewTop-values.length >= 0)
        {
            g.setColor(0xFFFFFF);
            g.fillTriangle(
                DISP_W-5,
                (values.length+2)*h,
                DISP_W-10,
                (values.length+2)*h-h,
                DISP_W,
                (values.length+2)*h-h
            );
        }

        {
            int v = Math.max(0, Storage.elements.length - viewTop - 1);
            int r = Math.max(1, Storage.elements.length - values.length);
            int hh = (values.length - 2) * h;
            int y = (int)((long)hh * (long)v / (long)r);
            g.setColor(0xFFFFFF);
            g.drawLine(DISP_W-10, y+3*h, DISP_W, y+3*h);
        }

        g.setColor(0xFFFFFF);
        g.drawString(
            "X-axis",
            15,
            2*h,
            Graphics.LEFT|Graphics.BOTTOM
        );
        g.drawString(
            "Y-axis",
            DISP_W/2+5,
            2*h,
            Graphics.LEFT|Graphics.BOTTOM
        );

        int len = Math.min(values.length, Storage.elements.length);

        for (int i = 0; i < len; i++)
        {
            g.setColor((viewTop - i == sel) ? 0xFFFF00 : 0xFFFFFF);
            g.fillRect(10, (i+2)*h, DISP_W - 20, h);
            g.setColor(0x777777);
            g.drawRect(10, (i+2)*h, DISP_W - 20, h);
            g.setColor(0x000000);
            g.drawString(
                values[i][0],
                DISP_W/2-5,
                (i+3)*h,
                Graphics.RIGHT|Graphics.BOTTOM
            );
            g.drawString(
                values[i][1],
                DISP_W - 15,
                (i+3)*h,
                Graphics.RIGHT|Graphics.BOTTOM
            );
        }
        g.setColor(0x000000);
        g.drawLine(DISP_W/2, 2*h, DISP_W/2, (len+2)*h);

        if (sel < Storage.elements.length)
        {
            g.setColor(0xFF0000);
            g.drawRect(10, (viewTop-sel+2)*h, DISP_W - 20, h);
        }

        renderButton(
            g,
            "BACK",
            sel == Storage.elements.length,
            13*h
        );

    }

    // STATE_SHOW_GRAPH
    void renderForShowGraph(Graphics g)
    {
        g.setColor(0xFFFFFF);

        g.drawString(
            curEntry.title,
            20,
            0,
            Graphics.LEFT|Graphics.TOP
        );

        g.fillRect(20, 20, 200, 200);

        for (int i = 0; i < 41; i++)
        {
            g.setColor(
                i % 10 == 0
                ? 0x777777
                : i % 5 == 0
                ? 0x999999
                : 0xBBBBBB
            );
            g.drawLine(20, i*5+20, 220, i*5+20);
            g.drawLine(i*5+20, 20, i*5+20, 220);
        }

        g.setColor(0x0000FF);
        g.drawLine(
            20,
            Storage.positionY(Storage.maxElement.y),
            220,
            Storage.positionY(Storage.maxElement.y)
        );
        g.setColor(0x7777FF);
        g.drawString(
            valueMaxY,
            DISP_W,
            Storage.positionY(Storage.maxElement.y) - SMALL_FONT.getHeight()/2,
            Graphics.RIGHT|Graphics.TOP
        );
        g.setColor(0x0000FF);
        g.drawLine(
            20,
            Storage.positionY(Storage.minElement.y),
            220,
            Storage.positionY(Storage.minElement.y)
        );
        g.setColor(0x7777FF);
        g.drawString(
            valueMinY,
            DISP_W,
            Storage.positionY(Storage.minElement.y) - SMALL_FONT.getHeight()/2,
            Graphics.RIGHT|Graphics.TOP
        );

        g.setColor(0x007700);
        g.drawLine(
            20,
            Storage.positionY(avgViewY),
            220,
            Storage.positionY(avgViewY)
        );
        g.setColor(0x00FF00);
        g.drawString(
            valueAvgViewY,
            DISP_W,
            Storage.positionY(avgViewY) - SMALL_FONT.getHeight()/2,
            Graphics.RIGHT|Graphics.TOP
        );

        g.setColor(0x770077);
        g.drawLine(
            20,
            Storage.positionY(avgAllY),
            220,
            Storage.positionY(avgAllY)
        );
        g.setColor(0xFF00FF);
        g.drawString(
            valueAvgAllY,
            DISP_W,
            Storage.positionY(avgAllY) - SMALL_FONT.getHeight()/2,
            Graphics.RIGHT|Graphics.TOP
        );

        g.setColor(0x666666);
        for (int i = 0; i < 9; i++)
        {
            g.drawString(
                Storage.scaleY[i],
                0,
                i*25+20 - SMALL_FONT.getHeight()/2,
                Graphics.LEFT|Graphics.TOP
            );
        }
        for (int i = 0; i < 5; i++)
        {
            g.drawString(
                Storage.scaleX[i],
                i*50+20,
                220,
                Graphics.HCENTER|Graphics.TOP
            );
        }

        int top = Storage.top;
        int bottom = Storage.bottom;

        g.setColor(0x000000);

        if (viewTop == 0)
        {
            int x = 215;
            for (int i = rightEnd; i >= leftEnd; i--)
            {
                Element e = Storage.elements[i];
                int y = Storage.positionY(e.y);
                g.fillRect(
                    x - 1,
                    y - 1,
                    3,
                    3
                );
                if (i + 1 < Storage.elements.length)
                {
                    g.drawLine(
                        x,
                        y,
                        x + Storage.getInterval(i, i+1)*5,
                        Storage.positionY(Storage.elements[i+1].y)
                    );
                }
                if (x < 20)
                {
                    break;
                }
                if (i > 0)
                {
                    x -= Storage.getInterval(i-1, i)*5;
                }
            }
        }
        else
        {
            int x = 25;
            for (int i = leftEnd; i <= rightEnd; i++)
            {
                Element e = Storage.elements[i];
                int y = Storage.positionY(e.y);
                g.fillRect(
                    x - 1,
                    y - 1,
                    3,
                    3
                );
                if (i > 0)
                {
                    g.drawLine(
                        x,
                        y,
                        x - Storage.getInterval(i-1, i)*5,
                        Storage.positionY(Storage.elements[i-1].y)
                    );
                }
                if (x > 220)
                {
                    break;
                }
                if (i+1 < Storage.elements.length)
                {
                    x += Storage.getInterval(i, i+1)*5;
                }
            }
        }

        if ((sel & 1) == 1)
        {
            if (viewTop == 0)
            {
                int xp = 215 - Storage.getInterval(sel >> 1, rightEnd)*5;
                int yp = Storage.positionY(curElement.y);
                g.setColor(0xFF0000);
                g.drawRect(xp - 3, yp - 3, 6, 6);
            }
            else
            {
                int xp = 25 + Storage.getInterval(leftEnd, sel >> 1)*5;
                int yp = Storage.positionY(curElement.y);
                g.setColor(0xFF0000);
                g.drawRect(xp - 3, yp - 3, 6, 6);
            }
        }

        g.setColor(0x777777);
        g.drawString(
            "X-value",
            5,
            220 + SMALL_FONT.getHeight(),
            Graphics.LEFT|Graphics.TOP
        );
        g.drawString(
            "Y-value",
            DISP_W - DISP_W/2 + 5,
            220 + SMALL_FONT.getHeight(),
            Graphics.LEFT|Graphics.TOP
        );

        g.setColor(0xFFFFFF);
        g.drawString(
            valueX,
            DISP_W / 2 - 5,
            220 + SMALL_FONT.getHeight(),
            Graphics.RIGHT|Graphics.TOP
        );
        g.drawString(
            valueY,
            DISP_W - 5,
            220 + SMALL_FONT.getHeight(),
            Graphics.RIGHT|Graphics.TOP
        );

        renderButton(g, "BACK", (sel & 1) == 0, DISP_H - SMALL_FONT.getHeight() - 1);
    }

    // STATE_ADD_DATA
    void renderForAddData(Graphics g)
    {
        g.setColor(0xFFFFFF);

        g.drawString(
            curEntry.title,
            20,
            10,
            Graphics.LEFT|Graphics.TOP
        );

        g.drawString(
            appState == STATE_ADD_DATA ? "ADD DATA" : "MODIFY DATA",
            20,
            30,
            Graphics.LEFT|Graphics.TOP
        );

        g.drawString(
            "x-axis",
            30,
            50,
            Graphics.LEFT|Graphics.TOP
        );

        renderEditElement(
            g,
            curEntry.xAxisType,
            curElement.x,
            70
        );

        if (sel >= 16 && sel < 32)
        {
            g.setColor(0x00FFFF);
            g.drawRect(
                DISP_W-40 - (sel&15)*18+2,
                70,
                14,
                SMALL_FONT.getHeight()
            );
            g.setColor(0x002222);
            g.drawRect(20, 50, DISP_W-40, 50);
        }

        if (sel == 0)
        {
            g.setColor(0x00FFFF);
            g.drawRect(20, 50, DISP_W-40, 50);
        }

        g.setColor(0xFFFFFF);
        g.drawString(
            "y-axis",
            30,
            110,
            Graphics.LEFT|Graphics.TOP
        );

        renderEditElement(
            g,
            curEntry.yAxisType,
            curElement.y,
            130
        );

        if (sel >= 32)
        {
            g.setColor(0x00FFFF);
            g.drawRect(
                DISP_W-40 - (sel&15)*18+2,
                130,
                14,
                SMALL_FONT.getHeight()
            );
            g.setColor(0x002222);
            g.drawRect(20, 110, DISP_W-40, 50);
        }

        if (sel == 1)
        {
            g.setColor(0x00FFFF);
            g.drawRect(20, 110, DISP_W-40, 50);
        }

        renderButton(g, "OK", sel == 2, 180);
        renderButton(g, "CANCEL", sel == 3, 200);
        if (appState == 6)
        {
            renderButton(g, "DELETE", sel == 4, 230);
        }

    }

    void renderEditElement(Graphics g, int type, int value, int y0)
    {
        final int h = SMALL_FONT.getHeight();
        final String[] DIGITS = new String[]{
            "0", "1", "2", "3", "4",
            "5", "6", "7", "8", "9"
        };
        final String[] digits = new String[10];
        int size = 0;

        g.setColor(0xFFFFFF);
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
        case Entry.COUNTER:
            size = 8;
            if (type != Entry.COUNTER)
            {
                size = 9;
                if (value < 0)
                {
                    value = -value;
                    digits[8] = "-";
                }
                else
                {
                    digits[8] = "+";
                }
                g.setColor(0xFFFFFF);
                g.drawString(
                    ".",
                    DISP_W-40 - (type-1)*18,
                    y0+h,
                    Graphics.HCENTER|Graphics.BOTTOM
                );
            }
            for (int i = 0; i < 8; i++)
            {
                digits[i] = DIGITS[value % 10];
                value /= 10;
            }
            break;
        case Entry.DATE_YMDHM:
            int minute = Element.getMinute(value);
            digits[size] = DIGITS[minute % 10];
            digits[size+1] = DIGITS[minute / 10];
            size = 2;
            g.drawString(":", DISP_W-40 - 1*18, y0+h, Graphics.HCENTER|Graphics.BOTTOM);
        case Entry.DATE_YMDH:
            int hour = Element.getHour(value);
            digits[size] = DIGITS[hour % 10];
            digits[size+1] = DIGITS[hour / 10];
            size += 2;
            g.drawString(
                "'",
                DISP_W-40 - (1+2*(Entry.DATE_YMDH-type))*18,
                y0+h,
                Graphics.HCENTER|Graphics.BOTTOM
            );
        case Entry.DATE_YMD:
            int day = Element.getDay(value);
            digits[size] = DIGITS[day % 10];
            digits[size+1] = DIGITS[day / 10];
            size += 2;
            g.drawString(
                "-",
                DISP_W-40 - (1+2*(Entry.DATE_YMD-type))*18,
                y0+h,
                Graphics.HCENTER|Graphics.BOTTOM
            );
        case Entry.DATE_YM:
            int month = Element.getMonth(value);
            digits[size] = DIGITS[month % 10];
            digits[size+1] = DIGITS[month / 10];
            size += 2;
            g.drawString(
                "-",
                DISP_W-40 - (1+2*(Entry.DATE_YM-type))*18,
                y0+h,
                Graphics.HCENTER|Graphics.BOTTOM
            );
        case Entry.DATE_Y:
            int year = Element.getYear(value);
            digits[size] = DIGITS[year % 10];
            digits[size+1] = DIGITS[(year % 100) / 10];
            size += 2;
            g.setColor(0x222222);
            g.drawString(
                Integer.toString(year),
                DISP_W-40 - (2*(Entry.DATE_Y-type))*18,
                y0+h,
                Graphics.HCENTER|Graphics.TOP
            );
            break;
        default:
            break;
        }

        for (int i = 0; i < size; i++)
        {
            g.setColor(0xFFFFFF);
            g.drawString(
                digits[i],
                DISP_W-40 - i*18+9,
                y0+h,
                Graphics.HCENTER|Graphics.BOTTOM
            );
            g.setColor(0x333333);
            g.drawRect(
                DISP_W-40 - i*18+2,
                y0,
                14,
                h
            );
        }
    }

    // STATE_DATASET_MENU
    void renderForDatasetMenu(Graphics g)
    {
        g.setColor(0xFFFFFF);

        g.drawString(
            curEntry.title,
            20,
            10,
            Graphics.LEFT|Graphics.TOP
        );

        g.drawString(
            "x-axis",
            30,
            30,
            Graphics.LEFT|Graphics.TOP
        );

        String xd = Entry.getTypeDescription(curEntry.xAxisType);

        g.drawString(
            xd,
            40,
            50,
            Graphics.LEFT|Graphics.TOP
        );

        g.drawString(
            "y-axis",
            30,
            70,
            Graphics.LEFT|Graphics.TOP
        );

        String yd = Entry.getTypeDescription(curEntry.yAxisType);

        g.drawString(
            yd,
            40,
            90,
            Graphics.LEFT|Graphics.TOP
        );

        final String[] names = new String[]{
            "ADD DATA",
            "SHOW GRAPH",
            "SHOW DATA",
            "EXPORT",
            "DELETE",
            "BACK"
        };

        for (int i = 0; i < names.length; i++)
        {
            renderButton(g, names[i], sel == i, 120+i*20);
        }

    }

    // STATE_NEW_DATASET
    void renderForNewDataset(Graphics g)
    {
        g.setColor(0xFFFFFF);

        g.drawString(
            "SETTING",
            20,
            10,
            Graphics.LEFT|Graphics.TOP
        );

        g.drawString(
            curEntry.title,
            20,
            30,
            Graphics.LEFT|Graphics.TOP
        );

        String xt = sel == 0
                  ? "x-axis   <<  >>"
                  : "x-axis";
        g.drawString(
            xt,
            30,
            50,
            Graphics.LEFT|Graphics.TOP
        );

        String xd = Entry.getTypeDescription(curEntry.xAxisType);

        g.drawString(
            xd,
            40,
            70,
            Graphics.LEFT|Graphics.TOP
        );

        if (sel == 0)
        {
            g.setColor(0x00FFFF);
            g.drawRect(20, 50, DISP_W-40, 40);
            g.setColor(0xFFFFFF);
        }

        String yt = sel == 1
                  ? "y-axis   <<  >>"
                  : "y-axis";
        g.drawString(
            yt,
            30,
            90,
            Graphics.LEFT|Graphics.TOP
        );

        String yd = Entry.getTypeDescription(curEntry.yAxisType);

        g.drawString(
            yd,
            40,
            110,
            Graphics.LEFT|Graphics.TOP
        );

        if (sel == 1)
        {
            g.setColor(0x00FFFF);
            g.drawRect(20, 90, DISP_W-40, 40);
        }

        renderButton(g, "OK", sel == 2, 150);
        renderButton(g, "CANCEL", sel == 3, 170);

    }

    // STATE_MAIN_MENU
    void renderForMainMenu(Graphics g)
    {
        final int h = SMALL_FONT.getHeight();

        renderButton(g, "NEW", sel == 0, 10);

        if (viewTop > 0)
        {
            g.setColor(0xFFFFFF);
            g.fillTriangle(
                DISP_W-14,
                20+h,
                DISP_W-18,
                20+h+h,
                DISP_W-10,
                20+h+h
            );
        }

        if (Storage.existsEntry(viewTop+10))
        {
            g.setColor(0xFFFFFF);
            g.fillTriangle(
                DISP_W-14,
                20+h+h*10,
                DISP_W-18,
                20+h*10,
                DISP_W-10,
                20+h*10
            );
        }

        for (int i = 0; i < 10; i++)
        {
            int vt = viewTop + i;
            int y = 20+h + i*h;
            g.setColor(
                sel-1 == vt
                ? 0xFFFF00
                : Storage.existsEntry(vt)
                ? 0xFFFFFF
                : 0xAAAAAA
            );
            g.fillRect(20, y, DISP_W-40, h);
            g.setColor(sel-1 == vt ? 0xFF0000 : 0x777777);
            g.drawRect(20, y, DISP_W-40, h);
            if (Storage.existsEntry(vt))
            {
                g.setColor(0x000000);
                g.drawString(
                    Storage.entries[vt].title,
                    DISP_W/2,
                    y+h,
                    Graphics.HCENTER|Graphics.BOTTOM
                );
            }
        }
    }

    private void renderButton(Graphics g, String text, boolean selected, int y)
    {
        int h = SMALL_FONT.getHeight();
        g.setColor(selected ? 0xFFFF00 : 0xFFFFFF);
        g.fillRect(20, y, DISP_W-40, h);
        g.setColor(0x000000);
        g.drawString(
            text,
            DISP_W/2,
            y+h,
            Graphics.HCENTER|Graphics.BOTTOM
        );
        g.setColor(selected ? 0xFF0000 : 0x777777);
        g.drawRect(20, y, DISP_W-40, h);
    }

    private static void setRightView(int newRightEnd, boolean force)
    {
        if (force || !narrowView)
        {
            viewTop = 0;
            rightEnd = newRightEnd;
            leftEnd = rightEnd;
            while (leftEnd > 0 && Storage.getInterval(leftEnd, rightEnd) < 39)
            {
                leftEnd--;
            }
            Storage.calcScaleX(curEntry, rightEnd, false);
            avgViewY = Storage.getAverageY(
                leftEnd + (narrowView ? 0 : 1),
                rightEnd
            );
            valueAvgViewY = Entry.valueString(curEntry.yAxisType, avgViewY);
        }
        sel = (newRightEnd << 1) | 1;
    }

    private static void setLeftView(int newLeftEnd, boolean force)
    {
        if (force || !narrowView)
        {
            viewTop = 1;
            leftEnd = newLeftEnd;
            rightEnd = leftEnd;
            while (rightEnd+1 < Storage.elements.length && Storage.getInterval(leftEnd, rightEnd) < 39)
            {
                rightEnd++;
            }
            Storage.calcScaleX(curEntry, leftEnd, true);
            avgViewY = Storage.getAverageY(
                leftEnd,
                rightEnd - (narrowView ? 0 : 1)
            );
            valueAvgViewY = Entry.valueString(curEntry.yAxisType, avgViewY);
        }
        sel = (newLeftEnd << 1) | 1;
    }

    private void keyPressedAppState_7(int keyCode)
    {
        if (keyCode == KEY_CLR)
        {
            sel = 1;
            keyCode = getKeyCode(FIRE);
        }
        switch (getGameAction(keyCode))
        {
        case UP:
        case DOWN:
            sel = 1 - sel;
            render();
            break;
        case FIRE:
            switch (sel)
            {
            case 0:
                if (appState == 7)
                {
                    Storage.deleteElement(curElement.id);
                    curElement = null;
                    Storage.loadElements();
                    if (Storage.getLastElement() == null)
                    {
                        sel = 0;
                        viewTop = 0;
                        appState = STATE_DATASET_MENU;
                    }
                    else
                    {
                        viewTop = Math.min(viewTop, Storage.elements.length-1);
                        for (int i = 0; viewTop-i >= 0 && i < values.length; i++)
                        {
                            Element e = Storage.elements[viewTop-i];
                            values[i][0] = curEntry.valueXString(e);
                            values[i][1] = curEntry.valueYString(e);
                        }
                        sel = viewTop;
                        appState = 5;
                    }
                }
                else if (appState == 8)
                {
                    Storage.deleteEntry(curEntry.id);
                    curEntry = null;
                    appState = STATE_MAIN_MENU;
                    sel = 0;
                    viewTop = 0;
                    Storage.loadEntries();
                }
                render();
                setTicker(new Ticker("deleted"));
                valueX = "";
                valueY = "";
                break;
            case 1:
                sel = 0;
                appState = appState == 7 ? 6 : STATE_DATASET_MENU;
                render();
                valueX = "";
                valueY = "";
                break;
            default:
                break;
            }
            break;
        default:
            break;
        }
    }

    private void keyPressedAppState_5(int keyCode)
    {
        if (keyCode == KEY_CLR)
        {
            sel = Storage.elements.length;
            keyCode = getKeyCode(FIRE);
        }
        switch (getGameAction(keyCode))
        {
        case RIGHT:
            sel = Math.max(0, sel - (values.length - 1));
            viewTop = Math.min(sel + (values.length - 1), Storage.elements.length-1);
            for (int i = 0; viewTop-i >= 0 && i < values.length; i++)
            {
                Element e = Storage.elements[viewTop-i];
                values[i][0] = curEntry.valueXString(e);
                values[i][1] = curEntry.valueYString(e);
            }
            render();
            break;
        case DOWN:
            sel--;
            if (sel < 0)
            {
                sel = Storage.elements.length;
                viewTop = sel-1;
                for (int i = 0; viewTop-i >= 0 && i < values.length; i++)
                {
                    Element e = Storage.elements[viewTop-i];
                    values[i][0] = curEntry.valueXString(e);
                    values[i][1] = curEntry.valueYString(e);
                }
            }
            else if (sel <= viewTop-values.length)
            {
                viewTop = sel + values.length - 1;
                for (int i = 0; i+1< values.length; i++)
                {
                    values[i][0] = values[i+1][0];
                    values[i][1] = values[i+1][1];
                }
                values[values.length-1][0] =
                    curEntry.valueXString(Storage.elements[sel]);
                values[values.length-1][1] =
                    curEntry.valueYString(Storage.elements[sel]);
            }
            render();
            break;
        case LEFT:
            sel = Math.min(Storage.elements.length-1, sel + (values.length - 1));
            viewTop = Math.max(viewTop, sel);
            for (int i = 0; viewTop-i >= 0 && i < values.length; i++)
            {
                Element e = Storage.elements[viewTop-i];
                values[i][0] = curEntry.valueXString(e);
                values[i][1] = curEntry.valueYString(e);
            }
            render();
            break;
        case UP:
            sel++;
            if (sel > Storage.elements.length)
            {
                sel = 0;
                int len = Math.min(values.length, Storage.elements.length);
                viewTop = len-1;
                for (int i = 0; i < len; i++)
                {
                    Element e = Storage.elements[viewTop-i];
                    values[i][0] = curEntry.valueXString(e);
                    values[i][1] = curEntry.valueYString(e);
                }
            }
            else if (sel > viewTop && sel < Storage.elements.length)
            {
                viewTop = sel;
                for (int i = values.length-1; i > 0; i--)
                {
                    values[i][0] = values[i-1][0];
                    values[i][1] = values[i-1][1];
                }
                values[0][0] = curEntry.valueXString(Storage.elements[sel]);
                values[0][1] = curEntry.valueYString(Storage.elements[sel]);
            }
            render();
            break;
        case FIRE:
            if (sel == Storage.elements.length)
            {
                appState = STATE_DATASET_MENU;
                sel = 0;
                viewTop = 0;
                render();
            }
            else
            {
                appState = 6;
                curElement = Storage.elements[sel];
                sel = 0;
                render();
            }
            break;
        default:
            break;
        }
    }

    // STATE_SHOW_GRAPH
    private void keyPressedOnShowGraph(int keyCode)
    {
        switch (keyCode)
        {
        case KEY_NUM1:
            if (sel != 0)
            {
                if (Storage.getInterval(0, leftEnd) < 39)
                {
                    setLeftView(0, false);
                }
                else
                {
                    setRightView(leftEnd, false);
                }
                curElement = Storage.elements[sel >> 1];
                valueX = curEntry.valueXString(curElement);
                valueY = curEntry.valueYString(curElement);
                render();
                return;
            }
            break;
        case KEY_NUM3:
            if (sel != 0)
            {
                if (Storage.getInterval(rightEnd, Storage.elements.length-1) < 39)
                {
                    setRightView(Storage.elements.length - 1, false);
                }
                else
                {
                    setLeftView(rightEnd, false);
                }
                curElement = Storage.elements[sel >> 1];
                valueX = curEntry.valueXString(curElement);
                valueY = curEntry.valueYString(curElement);
                render();
                return;
            }
            break;
        case KEY_CLR:
            keyCode = getKeyCode(FIRE);
            sel = 0;
            break;
        default:
            break;
        }
        switch (getGameAction(keyCode))
        {
        case UP:
        case DOWN:
            if (sel == 0)
            {
                if (viewTop == 0)
                {
                    sel = (rightEnd << 1) | 1;
                }
                else
                {
                    sel = (leftEnd << 1) | 1;
                }
                curElement = Storage.elements[sel >> 1];
                valueX = curEntry.valueXString(curElement);
                valueY = curEntry.valueYString(curElement);
            }
            else
            {
                sel = 0;
            }
            render();
            break;
        case LEFT:
            if (sel != 0)
            {
                sel -= 2;
                if (sel < 0)
                {
                    setRightView(Storage.elements.length - 1, false);
                }
                else if ((sel >> 1) <= leftEnd - viewTop)
                {
                    setLeftView(sel >> 1, false);
                }
                curElement = Storage.elements[sel >> 1];
                valueX = curEntry.valueXString(curElement);
                valueY = curEntry.valueYString(curElement);
                render();
            }
            break;
        case RIGHT:
            if (sel != 0)
            {
                sel += 2;
                if ((sel >> 1) >= Storage.elements.length)
                {
                    setLeftView(0, false);
                }
                else if ((sel >> 1) > rightEnd - viewTop)
                {
                    setRightView(sel >> 1, false);
                }
                curElement = Storage.elements[sel >> 1];
                valueX = curEntry.valueXString(curElement);
                valueY = curEntry.valueYString(curElement);
                render();
            }
            break;
        case FIRE:
            if (sel == 0)
            {
                curElement = null;
                valueX = "";
                valueY = "";
                appState = STATE_DATASET_MENU;
                sel = 1;
                render();
            }
            break;
        }
   }

    // STATE_ADD_DATA
    private void keyPressedOnAddData(int keyCode)
    {
        switch (keyCode)
        {
        case KEY_NUM0:
        case KEY_NUM1:
        case KEY_NUM2:
        case KEY_NUM3:
        case KEY_NUM4:
        case KEY_NUM5:
        case KEY_NUM6:
        case KEY_NUM7:
        case KEY_NUM8:
        case KEY_NUM9:
            if (sel >= 16)
            {
                setValueDigit(keyCode - KEY_NUM0);
                keyCode = getKeyCode(RIGHT);
            }
            break;
        case KEY_CLR:
            keyCode = getKeyCode(FIRE);
            if (sel < 16)
            {
                sel = 3;
            }
            break;
        default:
            break;
        }
        switch (getGameAction(keyCode))
        {
        case DOWN:
            if (sel < 16)
            {
                if (appState == STATE_ADD_DATA)
                {
                    sel = (sel + 1) % 4;
                }
                else
                {
                    sel = (sel + 1) % 5;
                }
            }
            else
            {
                editValue(-1);
            }
            render();
            break;
        case UP:
            if (sel < 16)
            {
                if (appState == STATE_ADD_DATA)
                {
                    sel = (sel + 3) % 4;
                }
                else
                {
                    sel = (sel + 4) % 5;
                }
            }
            else
            {
                editValue(1);
            }
            render();
            break;
        case LEFT:
            if (sel >= 16)
            {
                moveEditSelect(1);
                render();
            }
            break;
        case RIGHT:
            if (sel >= 16)
            {
                moveEditSelect(-1);
                render();
            }
            break;
        case FIRE:
            switch (sel)
            {
            case 0: // x-axis
                sel = 16;
                render();
                break;
            case 1: // y-axis
                sel = 32;
                render();
                break;
            case 2: // OK
                if (!Storage.saveData(curElement))
                {
                    setTicker(new Ticker("storage is full"));
                    break;
                }
                setTicker(new Ticker("saved"));
            case 3: // CANCEL
                if (appState == STATE_ADD_DATA)
                {
                    curElement = null;
                    appState = STATE_DATASET_MENU;
                    sel = 0;
                    render();
                }
                else if (appState == 6)
                {
                    curElement = null;
                    if (sel == 2)
                    {
                        Storage.loadElements();
                        for (int i = 0; viewTop-i >= 0 && i < values.length; i++)
                        {
                            Element e = Storage.elements[viewTop-i];
                            values[i][0] = curEntry.valueXString(e);
                            values[i][1] = curEntry.valueYString(e);
                        }
                    }
                    sel = viewTop;
                    appState = 5;
                    render();
                }
                break;
            case 4: // DELETE
                valueX = curEntry.valueXString(curElement);
                valueY = curEntry.valueYString(curElement);
                appState = 7;
                sel = 1;
                render();
                break;
            default:
                if (sel >= 16)
                {
                    sel = sel / 16 - 1;
                    render();
                }
                break;
            }
        default:
            break;
        }
    }

    private void setValueDigit(int digit)
    {
        int type = sel >= 32
                 ? curEntry.yAxisType
                 : curEntry.xAxisType;
        int value = sel >= 32
                 ? curElement.y
                 : curElement.x;
        int pos = sel & 15;
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
            if (pos == 8)
            {
                return;
            }
        case Entry.COUNTER:
            value = setDigit(value, pos, digit);
            break;
        case Entry.DATE_YMDHM:
            if (pos < 2)
            {
                value = Element.setMinute(
                    value,
                    Math.min(59, setDigit(
                        Element.getMinute(value),
                        pos,
                        digit
                    ))
                );
                break;
            }
            pos -= 2;
        case Entry.DATE_YMDH:
            if (pos < 2)
            {
                value = Element.setHour(
                    value,
                    Math.min(23, setDigit(
                        Element.getHour(value),
                        pos,
                        digit
                    ))
                );
                break;
            }
            pos -= 2;
        case Entry.DATE_YMD:
            if (pos < 2)
            {
                value = Element.setDay(
                    value,
                    Math.max(1, Math.min(31, setDigit(
                            Element.getDay(value),
                            pos,
                            digit
                    )))
                );
                break;
            }
            pos -= 2;
        case Entry.DATE_YM:
            if (pos < 2)
            {
                value = Element.setMonth(
                    value,
                    Math.max(1, Math.min(12, setDigit(
                        Element.getMonth(value),
                        pos,
                        digit
                    )))
                );
                break;
            }
            pos -= 2;
        case Entry.DATE_Y:
            int y = setDigit(
                Element.getYear(value) % 100,
                pos,
                digit
            );
            value = Element.setYear(
                value,
                y < 45 ? (2000+y) : (1900+y)
            );
            break;
        default:
            break;
        }
        if (sel >= 32)
        {
            curElement.y = value;
        }
        else
        {
            curElement.x = value;
        }
    }

    static final int[] DIG = new int[]{
        1,
        10,
        100,
        1000,
        10000,
        100000,
        1000000,
        10000000,
        100000000,
        1000000000
    };

    private int setDigit(int value, int pos, int digit)
    {
        boolean s = value < 0;
        value = Math.abs(value);
        int d = (value / DIG[pos]) % 10;
        value += (digit - d) * DIG[pos];
        return s ? -value : value;
    }

    private int changeDigit(int value, int pos, int changes, int ub)
    {
        boolean s = value < 0;
        value = Math.abs(value);
        int d = (value / DIG[pos]) % 10;
        int n = (d + changes + ub) % ub;
        value += (n - d) * DIG[pos];
        return s ? -value : value;
    }

    private void editValue(int changes)
    {
        int type = sel >= 32
                 ? curEntry.yAxisType
                 : curEntry.xAxisType;
        int value = sel >= 32
                 ? curElement.y
                 : curElement.x;
        int pos = sel & 15;
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
            if (pos == 8)
            {
                value = -value;
                break;
            }
        case Entry.COUNTER:
            value = changeDigit(value, pos, changes, 10);
            break;
        case Entry.DATE_YMDHM:
            if (pos < 2)
            {
                value = Element.setMinute(
                    value,
                    Math.min(59, changeDigit(
                        Element.getMinute(value),
                        pos,
                        changes,
                        10 - 4*pos
                    ))
                );
                break;
            }
            pos -= 2;
        case Entry.DATE_YMDH:
            if (pos < 2)
            {
                value = Element.setHour(
                    value,
                    Math.min(23, changeDigit(
                        Element.getHour(value),
                        pos,
                        changes,
                        10 - 7*pos
                    ))
                );
                break;
            }
            pos -= 2;
        case Entry.DATE_YMD:
            if (pos < 2)
            {
                value = Element.setDay(
                    value,
                    Math.max(1, Math.min(31, changeDigit(
                            Element.getDay(value),
                            pos,
                            changes,
                            10 - 6*pos
                    )))
                );
                break;
            }
            pos -= 2;
        case Entry.DATE_YM:
            if (pos < 2)
            {
                value = Element.setMonth(
                    value,
                    Math.max(1, Math.min(12, changeDigit(
                        Element.getMonth(value),
                        pos,
                        changes,
                        10 - 8*pos
                    )))
                );
                break;
            }
            pos -= 2;
        case Entry.DATE_Y:
            int y = changeDigit(
                Element.getYear(value) % 100,
                pos,
                changes,
                10
            );
            value = Element.setYear(
                value,
                y < 45 ? (2000+y) : (1900+y)
            );
            break;
        default:
            break;
        }
        if (sel >= 32)
        {
            curElement.y = value;
        }
        else
        {
            curElement.x = value;
        }
    }

    private void moveEditSelect(int move)
    {
        int type = sel >= 32
                 ? curEntry.yAxisType
                 : curEntry.xAxisType;
        int pos = sel & 15;
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
            sel ^= pos ^ ((pos + move + 9) % 9);
            break;
        case Entry.COUNTER:
            sel ^= pos ^ ((pos + move + 8) % 8);
            break;
        case Entry.DATE_YMDHM:
            sel ^= pos ^ ((pos + move + 10) % 10);
            break;
        case Entry.DATE_YMDH:
            sel ^= pos ^ ((pos + move + 8) % 8);
            break;
        case Entry.DATE_YMD:
            sel ^= pos ^ ((pos + move + 6) % 6);
            break;
        case Entry.DATE_YM:
            sel ^= pos ^ ((pos + move + 4) % 4);
            break;
        case Entry.DATE_Y:
            sel ^= pos ^ ((pos + move + 2) % 2);
            break;
        default:
            break;
        }
    }

    // STATE_DATASET_MENU
    private void keyPressedOnDatasetMenu(int keyCode)
    {
        if (keyCode == KEY_CLR)
        {
            keyCode = getKeyCode(FIRE);
            sel = 5;
        }
        switch (getGameAction(keyCode))
        {
        case UP:
            sel = (sel + 5) % 6;
            render();
            break;
        case DOWN:
            sel = (sel + 1) % 6;
            render();
            break;
        case FIRE:
            switch (sel)
            {
            case 0: // ADD DATA
                curElement = curEntry.newElement();
                appState = STATE_ADD_DATA;
                render();
                break;
            case 1: // SHOW GRAPH
                Storage.loadElements();
                if (Storage.getLastElement() == null)
                {
                    setTicker(new Ticker("no data"));
                    break;
                }
                Storage.calcIntervals(curEntry);
                Storage.calcUnit();
                Storage.calcScaleY(curEntry);
                narrowView = Storage.getInterval(0, Storage.elements.length - 1) < 39;
                if (narrowView)
                {
                    setLeftView(0, true);
                }
                else
                {
                    setRightView(Storage.elements.length - 1, true);
                }
                curElement = Storage.elements[sel >> 1];
                valueX = curEntry.valueXString(curElement);
                valueY = curEntry.valueYString(curElement);
                valueMaxY = curEntry.valueYString(Storage.maxElement);
                valueMinY = curEntry.valueYString(Storage.minElement);
                avgAllY = Storage.getAverageY();
                valueAvgAllY = Entry.valueString(curEntry.yAxisType, avgAllY);
                appState = STATE_SHOW_GRAPH;
                render();
                break;
            case 2: // SHOW DATA
                Storage.loadElements();
                if (Storage.getLastElement() == null)
                {
                    setTicker(new Ticker("no data"));
                    break;
                }
                // TODO
                viewTop = Storage.elements.length - 1;
                sel = viewTop;
                if (values == null)
                {
                    values = new String[10][2];
                }
                for (int i = 0; sel-i >= 0 && i < values.length; i++)
                {
                    Element e = Storage.elements[sel-i];
                    values[i][0] = curEntry.valueXString(e);
                    values[i][1] = curEntry.valueYString(e);
                }
                appState = 5;
                render();
                break;
            case 3: // EXPORT
                Storage.loadElements();
                if (Storage.getLastElement() == null)
                {
                    setTicker(new Ticker("no data"));
                    break;
                }
                // TODO
                export();
                break;
            case 4: // DELETE
                // TODO
                appState = 8;
                sel = 1;
                render();
                break;
            case 5: // BACK
                Storage.closeData();
                curEntry = null;
                appState = STATE_MAIN_MENU;
                sel = 0;
                viewTop = 0;
                Storage.loadEntries();
                render();
                break;
            default:
                break;
            }
            break;
        default:
            break;
        }
    }

    // STATE_NEW_DATASET
    private void keyPressedOnNewDataset(int keyCode)
    {
        if (keyCode == KEY_CLR)
        {
            keyCode = getKeyCode(FIRE);
            sel = 3;
        }
        switch (getGameAction(keyCode))
        {
        case UP:
            sel = (sel + 3) % 4;
            render();
            break;
        case DOWN:
            sel = (sel + 1) % 4;
            render();
            break;
        case LEFT:
            if (sel == 0)
            {
                curEntry.xAxisType--;
                if (curEntry.xAxisType < 0)
                {
                    curEntry.xAxisType = Entry.DATE_Y;
                }
                render();
            }
            else if (sel == 1)
            {
                curEntry.yAxisType--;
                if (curEntry.yAxisType < 0)
                {
                    curEntry.yAxisType = Entry.POINT_8;
                }
                render();
            }
            break;
        case RIGHT:
            if (sel == 0)
            {
                curEntry.xAxisType++;
                if (curEntry.xAxisType > Entry.DATE_Y)
                {
                    curEntry.xAxisType = Entry.POINT_0;
                }
                render();
            }
            else if (sel == 1)
            {
                curEntry.yAxisType++;
                if (curEntry.yAxisType > Entry.POINT_8)
                {
                    curEntry.yAxisType = Entry.POINT_0;
                }
                render();
            }
            break;
        case FIRE:
            switch (sel)
            {
            case 0: // x-axis
            case 1: // y-axis
                keyPressedOnNewDataset(getKeyCode(RIGHT));
                break;
            case 2: // OK
                if (!Storage.saveEntry(curEntry))
                {
                    setTicker(new Ticker("storage is full"));
                }
                else
                {
                    appState = STATE_MAIN_MENU;
                    sel = 0;
                    viewTop = 0;
                    curEntry = null;
                    Storage.loadEntries();
                    render();
                    return;
                }
                break;
            case 3: // CANCEL
                appState = STATE_MAIN_MENU;
                sel = 0;
                viewTop = 0;
                render();
                break;
            default:
                break;
            }
            break;
        default:
            break;
        }
    }

    // STATE_MAIN_MENU
    private void keyPressedOnMainMenu(int keyCode)
    {
        switch (getGameAction(keyCode))
        {
        case DOWN:
            sel++;
            if (!Storage.existsEntry(sel-1))
            {
                sel = 0;
                viewTop = 0;
            }
            if (sel > viewTop + 10)
            {
                viewTop = sel - 10;
            }
            render();
            break;
        case UP:
            sel--;
            if (sel < 0)
            {
                if (Storage.existsEntry(0))
                {
                    sel = Storage.entries.length;
                    viewTop = Math.max(0, sel - 10);
                }
                else
                {
                    sel = 0;
                }
            }
            if (sel-1 < viewTop)
            {
                viewTop = Math.max(0, sel - 1);
            }
            render();
            break;
        case FIRE:
            if (sel == 0)
            {
                GraphMIDlet.showTitleTextBox();
            }
            else
            {
                curEntry = Storage.entries[sel-1];
                Storage.saveEntry(curEntry);
                Storage.openData(curEntry);
                appState = STATE_DATASET_MENU;
                sel = 0;
                render();
            }
            break;
        default:
            break;
        }
    }
}