import javax.microedition.lcdui.*;
import javax.microedition.midlet.*;

public final class GraphMIDlet extends MIDlet implements CommandListener
{
    private static MainDisp mainDisp = null;

    private static Command
        exitCommand = null;

    public GraphMIDlet()
    {
        mainDisp = new MainDisp();

        exitCommand = new Command("EXIT", Command.EXIT, 1);
        mainDisp.addCommand(exitCommand);

        mainDisp.setCommandListener(this);

        Display.getDisplay(this).setCurrent(mainDisp);
    }

    protected void destroyApp(boolean unconditional) throws MIDletStateChangeException
    {
    }

    protected void pauseApp()
    {
    }

    protected void startApp() throws MIDletStateChangeException
    {
    }

	public void commandAction(Command cmd, Displayable disp)
	{
        if (cmd == exitCommand)
        {
            notifyDestroyed();
        }
    }
}