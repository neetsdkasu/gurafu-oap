import javax.microedition.lcdui.*;
import javax.microedition.midlet.*;

public final class GraphMIDlet extends MIDlet implements CommandListener
{
    static final boolean WTK =
        String.valueOf(System.getProperty("microedition.platform"))
            .startsWith("Sun");

    private static GraphMIDlet
        midlet = null;

    private static AppMain
        appMain = null;

    private static TextBox
        titleTextBox = null,
        exportTextBox = null;

    private static Command
        exitCommand = null;

    public GraphMIDlet()
    {
        midlet = this;
        Storage.init();

        appMain = new AppMain();

        exitCommand = new Command("EXIT", Command.EXIT, 1);
        appMain.addCommand(exitCommand);

        appMain.setCommandListener(this);

        Display.getDisplay(this).setCurrent(appMain);
    }

    static void showTitleTextBox()
    {
        if (titleTextBox == null)
        {
            titleTextBox = new TextBox("input new title", "", 50, TextField.ANY);
            titleTextBox.addCommand(new Command("OK", Command.OK, 1));
            titleTextBox.addCommand(new Command("CANCEL", Command.CANCEL, 1));
            titleTextBox.setCommandListener(midlet);
        }
        else
        {
            titleTextBox.setString("");
        }
        Display.getDisplay(midlet).setCurrent(titleTextBox);
    }

    static void showExportTextBox(String text)
    {
        if (exportTextBox == null)
        {
            exportTextBox = new TextBox("export", text, 5000, TextField.ANY);
            exportTextBox.addCommand(new Command("CLOSE", Command.BACK, 1));
            exportTextBox.setCommandListener(midlet);
        }
        else
        {
            exportTextBox.setString(text);
        }
        Display.getDisplay(midlet).setCurrent(exportTextBox);
    }

    private static void release()
    {
        Storage.release();
    }

    protected void destroyApp(boolean unconditional) throws MIDletStateChangeException
    {
        release();
    }

    protected void pauseApp()
    {
    }

    protected void startApp() throws MIDletStateChangeException
    {
    }

	public void commandAction(Command cmd, Displayable disp)
	{
        if (cmd == null || disp == null)
        {
            return;
        }
        if (cmd == exitCommand)
        {
            release();
            notifyDestroyed();
        }
        if (disp == titleTextBox)
        {
            if (cmd.getCommandType() == Command.OK)
            {
                String title = titleTextBox.getString();
                if (title == null || (title = title.trim()).length() == 0)
                {
                    titleTextBox.setTicker(new Ticker("be not empty"));
                    return;
                }
                appMain.setNewTitle(title);
            }
            titleTextBox.setTicker(null);
            Display.getDisplay(this).setCurrent(appMain);
        }
        else if (disp == exportTextBox)
        {
            exportTextBox.setString("");
            Display.getDisplay(this).setCurrent(appMain);
        }
    }
}