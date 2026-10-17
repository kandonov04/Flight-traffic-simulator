package dialogs;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import static gui.Style.*;

// Dialog that shows a message to the user - a notification or an error.
public class MessageDialog extends Dialog {

	private Button ok = new Button("OK");

	public MessageDialog(Frame owner, String title, String message) {
		super(owner);
		
		setTitle(title);
		setResizable(false);
		setModalityType(ModalityType.APPLICATION_MODAL);
		
		setBackground(BACKGROUND);
		setBounds(owner.getX() + (owner.getWidth() - DIALOG_WIDTH) / 2, owner.getY() + (owner.getHeight() - DIALOG_HEIGHT) / 2, DIALOG_WIDTH, DIALOG_HEIGHT);

		Panel buttons = new Panel();
		buttons.setBackground(BACKGROUND);

		ok.addActionListener((e) -> {
			dispose();
		});

		Panel textPanel = new Panel();
		textPanel.setBackground(BACKGROUND);
		Label text = new Label(message, Label.CENTER);
		text.setForeground(BUTTON_TEXT);
		textPanel.add(text);
		add(textPanel, BorderLayout.CENTER);

		styleButton(ok);
		buttons.add(ok);
		add(buttons, BorderLayout.SOUTH);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				dispose();
			}
		});

		setVisible(true);
	}
}