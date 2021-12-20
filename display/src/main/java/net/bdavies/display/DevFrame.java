package net.bdavies.display;

import javax.swing.*;

import lombok.Getter;
import net.bdavies.display.ui.StripPanel;

public class DevFrame extends JFrame
{
	@Getter
	private final StripPanel panel;

	public DevFrame(int pixelCount, String id) {
		super("DevFrame - Preview Only - " + id);
		panel = new StripPanel(pixelCount);
		add(panel);
		pack();
		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setVisible(true);
	}
}
