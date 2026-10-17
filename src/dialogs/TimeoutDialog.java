package dialogs;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Label;
import java.awt.Panel;

import static gui.Style.*;

// Dialog shown during the last 5 seconds before the program shuts down due to inactivity
public class TimeoutDialog extends Dialog {

	private Label message = new Label("", Label.CENTER);

	public TimeoutDialog(Frame owner, Runnable onContinue) {
		super(owner);

		setTitle("Inactivity");
		setResizable(false);
		setModalityType(ModalityType.MODELESS);

		setBackground(BACKGROUND);
		setBounds(owner.getX() + (owner.getWidth() - DIALOG_WIDTH) / 2, owner.getY() + (owner.getHeight() - DIALOG_HEIGHT) / 2, DIALOG_WIDTH, DIALOG_HEIGHT);

		message.setForeground(BUTTON_TEXT);
		Panel textPanel = new Panel();
		textPanel.setBackground(BACKGROUND);
		textPanel.add(message);
		add(textPanel, BorderLayout.CENTER);

		Button stay = new Button("Continue");
		styleButton(stay);
		stay.addActionListener((e) -> {
			onContinue.run();
		});

		Panel buttons = new Panel();
		buttons.setBackground(BACKGROUND);
		buttons.add(stay);
		add(buttons, BorderLayout.SOUTH);
	}

	// Updates the displayed number of remaining seconds
	public void setSeconds(int seconds) {
		message.setText("The program will close in " + seconds + " s. Click Continue to keep working.");
	}
}