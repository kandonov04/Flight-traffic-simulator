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

public class QuitDialog extends Dialog {

	private Button ok = new Button("Yes");
	private Button cancel = new Button("No");
	private boolean confirmed = false;

	public QuitDialog(Frame owner) {
		super(owner);
		
		setTitle("Exit");
		setResizable(false);
		setModalityType(ModalityType.APPLICATION_MODAL);
		
		setBackground(BACKGROUND);
		setBounds(owner.getX() + (owner.getWidth() - DIALOG_WIDTH) / 2, owner.getY() + (owner.getHeight() - DIALOG_HEIGHT) / 2, DIALOG_WIDTH, DIALOG_HEIGHT);

		Panel buttons = new Panel();
		buttons.setBackground(BACKGROUND);

		// Instead of MainWindow.this.dispose()
		// The dialog no longer knows about the main window, so it can be reused in multiple places
		ok.addActionListener((e) -> {
			confirmed = true;
			dispose();
		});

		cancel.addActionListener((e) -> {
			dispose();
		});

		Panel textPanel = new Panel();
		textPanel.setBackground(BACKGROUND);
		Label text = new Label("Do you want to exit the simulator?", Label.CENTER);
		text.setForeground(BUTTON_TEXT);
		textPanel.add(text);
		add(textPanel, BorderLayout.CENTER);

		styleButton(ok);
		styleButton(cancel);
		buttons.add(ok);
		buttons.add(cancel);
		add(buttons, BorderLayout.SOUTH);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				dispose();
			}
		});
	}

	// Shows the dialog and waits for an answer
	public boolean ask() {
		// Show the dialog and block here until the user answers
		setVisible(true);
		// Execution reaches here only after dispose()
		return confirmed;
	}
}