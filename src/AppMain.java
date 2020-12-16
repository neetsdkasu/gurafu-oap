import javax.microedition.lcdui.*;
import javax.microedition.lcdui.game.*;

final class AppMain extends GameCanvas
{
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
        appState = 0,
        sel = 0,
        view_top = 0;

    private static Entry
        curEntry = null;

    private static Element
        curElement = null;

    AppMain()
    {
        super(false);
        Storage.loadEntries();

        render();
    }

    void setNewTitle(String title)
    {
        appState = 1;
        curEntry = new Entry();
        curEntry.title = title;
        render();
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
        case 0:
            keyPressedAppState_0(keyCode);
            break;
        case 1:
            keyPressedAppState_1(keyCode);
            break;
        case 2:
            keyPressedAppState_2(keyCode);
            break;
        case 3:
            keyPressedAppState_3(keyCode);
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
        case 0:
            renderAppState_0(g);
            break;
        case 1:
            renderAppState_1(g);
            break;
        case 2:
            renderAppState_2(g);
            break;
        case 3:
            renderAppState_3(g);
            break;
        default:
            break;
        }

        flushGraphics();
    }

    void renderAppState_3(Graphics g)
    {
        g.setColor(0xFFFFFF);

        g.drawString(
            curEntry.title,
            20,
            10,
            Graphics.LEFT|Graphics.TOP
        );

        g.drawString(
            "ADD DATA",
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
        }

        if (sel == 1)
        {
            g.setColor(0x00FFFF);
            g.drawRect(20, 110, DISP_W-40, 50);
        }

        renderButton(g, "OK", sel == 2, 180);
        renderButton(g, "CANCEL", sel == 3, 200);

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
            int minute = Entry.getMinute(type, value);
            digits[size] = DIGITS[minute % 10];
            digits[size+1] = DIGITS[minute / 10];
            size = 2;
            g.drawString(":", DISP_W-40 - 1*18, y0+h, Graphics.HCENTER|Graphics.BOTTOM);
        case Entry.DATE_YMDH:
            int hour = Entry.getHour(type, value);
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
            int day = Entry.getDay(type, value);
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
            int month = Entry.getMonth(type, value);
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
            int year = Entry.getYear(type, value);
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

    void renderAppState_2(Graphics g)
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
            "SHOW DATA"
        };

        for (int i = 0; i < names.length; i++)
        {
            renderButton(g, names[i], sel == i, 120+i*20);
        }

        renderButton(g, "DELETE", sel == 3, 190);
        renderButton(g, "BACK", sel == 4, 220);

    }

    void renderAppState_1(Graphics g)
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

    void renderAppState_0(Graphics g)
    {
        final int h = SMALL_FONT.getHeight();

        renderButton(g, "NEW", sel == 0, 10);

        if (view_top > 0)
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

        if (Storage.existsEntry(view_top+10))
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
            int vt = view_top + i;
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

    private void keyPressedAppState_3(int keyCode)
    {
        if (keyCode == KEY_CLR)
        {
            keyCode = getKeyCode(FIRE);
            if (sel < 16)
            {
                sel = 3;
            }
        }
        switch (getGameAction(keyCode))
        {
        case DOWN:
            if (sel < 16)
            {
                sel = (sel + 1) % 4;
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
                sel = (sel + 3) % 4;
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
                curElement = null;
                appState = 2;
                sel = 0;
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

    private int changeDigit(int value, int pos, int changes, int ub)
    {
        final int[] DIG = new int[]{
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
                value = Entry.setMinute(
                    type,
                    value,
                    Math.min(59, changeDigit(
                        Entry.getMinute(type, value),
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
                value = Entry.setHour(
                    type,
                    value,
                    Math.min(23, changeDigit(
                        Entry.getHour(type, value),
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
                value = Entry.setDay(
                    type,
                    value,
                    Math.max(1, Math.min(31, changeDigit(
                            Entry.getDay(type, value),
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
                value = Entry.setMonth(
                    type,
                    value,
                    Math.max(1, Math.min(12, changeDigit(
                        Entry.getMonth(type, value),
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
                Entry.getYear(type, value) % 100,
                pos,
                changes,
                10
            );
            value = Entry.setYear(
                type,
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

    private void keyPressedAppState_2(int keyCode)
    {
        if (keyCode == KEY_CLR)
        {
            keyCode = getKeyCode(FIRE);
            sel = 4;
        }
        switch (getGameAction(keyCode))
        {
        case UP:
            sel = (sel + 4) % 5;
            render();
            break;
        case DOWN:
            sel = (sel + 1) % 5;
            render();
            break;
        case FIRE:
            switch (sel)
            {
            case 0: // ADD DATA
                curElement = curEntry.newElement();
                appState = 3;
                render();
                break;
            case 1: // SHOW GRAPH
                break;
            case 2: // SHOW DATA
                break;
            case 3: // DELETE
                break;
            case 4: // BACK
                Storage.closeData();
                curEntry = null;
                appState = 0;
                sel = 0;
                view_top = 0;
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

    private void keyPressedAppState_1(int keyCode)
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
                    curEntry.xAxisType = Entry.DATE_YMDHM;
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
                if (curEntry.xAxisType > Entry.DATE_YMDHM)
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
                keyPressedAppState_1(getKeyCode(RIGHT));
                break;
            case 2: // OK
                if (!Storage.saveEntry(curEntry))
                {
                    setTicker(new Ticker("storage is full"));
                }
                else
                {
                    appState = 0;
                    sel = 0;
                    view_top = 0;
                    curEntry = null;
                    Storage.loadEntries();
                    render();
                    return;
                }
                break;
            case 3: // CANCEL
                appState = 0;
                sel = 0;
                view_top = 0;
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

    private void keyPressedAppState_0(int keyCode)
    {
        switch (getGameAction(keyCode))
        {
        case DOWN:
            sel++;
            if (!Storage.existsEntry(sel-1))
            {
                sel = 0;
                view_top = 0;
            }
            if (sel > view_top + 10)
            {
                view_top = sel - 10;
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
                    view_top = Math.max(0, sel - 10);
                }
                else
                {
                    sel = 0;
                }
            }
            if (sel-1 < view_top)
            {
                view_top = Math.max(0, sel - 1);
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
                appState = 2;
                sel = 0;
                render();
            }
            break;
        default:
            break;
        }
    }
}