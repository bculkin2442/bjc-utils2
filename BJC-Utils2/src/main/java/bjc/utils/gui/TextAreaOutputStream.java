package bjc.utils.gui;

import java.io.IOException;
import java.io.OutputStream;

import javax.swing.JTextArea;

/**
 * @author epr
 * @author Levente S\u00e1ntha (lsantha@users.sourceforge.net)
 */
public class TextAreaOutputStream extends OutputStream {

    private JTextArea textArea;

    public TextAreaOutputStream(JTextArea console) {
        this.textArea = console;
    }

    @Override
	public void write(int b) throws IOException {
        textArea.append("" + (char) b);
        if (b == '\n') {
            textArea.repaint();
        }
    }
}