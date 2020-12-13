import javax.microedition.lcdui.*;
import javax.microedition.lcdui.game.*;

final class AppMain extends GameCanvas
{
    static final int
        DISP_W = 240, DISP_H = 268;

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
        newEntry = null;

    AppMain()
    {
        super(false);
        Storage.loadEntries();

        render();
    }

    void setNewTitle(String title)
    {
        appState = 1;
        newEntry = new Entry();
        newEntry.title = title;
        render();
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
        default:
            break;
        }

        flushGraphics();
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
            newEntry.title,
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

        String xd = Entry.getTypeDescription(newEntry.xAxisType);

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

        String yd = Entry.getTypeDescription(newEntry.yAxisType);

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

        int h = SMALL_FONT.getHeight();

        g.setColor(sel == 2 ? 0xFFFF00 : 0xFFFFFF);
        g.fillRect(20, 150, DISP_W-40, h);
        g.setColor(0x000000);
        g.drawString(
            "OK",
            DISP_W/2,
            150+h,
            Graphics.HCENTER|Graphics.BOTTOM
        );
        g.setColor(sel == 2 ? 0xFF0000 : 0x777777);
        g.drawRect(20, 150, DISP_W-40, h);

        g.setColor(sel == 3 ? 0xFFFF00 : 0xFFFFFF);
        g.fillRect(20, 170, DISP_W-40, h);
        g.setColor(0x000000);
        g.drawString(
            "CANCEL",
            DISP_W/2,
            170+h,
            Graphics.HCENTER|Graphics.BOTTOM
        );
        g.setColor(sel == 3 ? 0xFF0000 : 0x777777);
        g.drawRect(20, 170, DISP_W-40, h);

    }

    void renderAppState_0(Graphics g)
    {
        int h = SMALL_FONT.getHeight();

        g.setColor(sel == 0 ? 0xFFFF00 : 0xFFFFFF);
        g.fillRect(20, 10, DISP_W-40, h);
        g.setColor(0x000000);
        g.drawString(
            "NEW",
            DISP_W/2,
            10+h,
            Graphics.HCENTER|Graphics.BOTTOM
        );
        g.setColor(sel == 0 ? 0xFF0000 : 0x777777);
        g.drawRect(20, 10, DISP_W-40, h);

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
        }
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
        switch (appState)
        {
        case 0:
            keyPressedAppState_0(keyCode);
            break;
        case 1:
            keyPressedAppState_1(keyCode);
            break;
        default:
            break;
        }
    }

    private void keyPressedAppState_1(int keyCode)
    {
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
                newEntry.xAxisType--;
                if (newEntry.xAxisType < 0)
                {
                    newEntry.xAxisType = Entry.DATE_YMDHM;
                }
                render();
            }
            else if (sel == 1)
            {
                newEntry.yAxisType--;
                if (newEntry.yAxisType < 0)
                {
                    newEntry.yAxisType = Entry.POINT_8;
                }
                render();
            }
            break;
        case RIGHT:
            if (sel == 0)
            {
                newEntry.xAxisType++;
                if (newEntry.xAxisType > Entry.DATE_YMDHM)
                {
                    newEntry.xAxisType = Entry.POINT_0;
                }
                render();
            }
            else if (sel == 1)
            {
                newEntry.yAxisType++;
                if (newEntry.yAxisType >  Entry.POINT_8)
                {
                    newEntry.yAxisType = Entry.POINT_0;
                }
                render();
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
        case FIRE:
            if (sel == 0)
            {
                GraphMIDlet.showTitleTextBox();
            }
            break;
        default:
            break;
        }
    }
}