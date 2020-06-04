package org.ardestan.gui.dialog.board;

import java.awt.FlowLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;

public class BoardManagerMessageWindow extends JFrame implements Runnable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected boolean 	running = false;
	protected String[] 	spin = { "|", "/", "-", "\\"};
	
	protected JLabel label = null;
	
	protected String messageBody;
	/**
	 * 
	 */
	public BoardManagerMessageWindow(String messageBody)
	{
		this.messageBody = messageBody;
		label = new JLabel();
		this.setLayout(new FlowLayout());
		this.add(label);
		this.setAlwaysOnTop(true);
		this.setUndecorated(true);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run()
	{
		int idx = 0;
		running = true;
		while(running) {
			try {
				Thread.sleep(500);
				String t = messageBody + spin[idx];
				label.setText(t);
				this.repaint();
				idx = (idx + 1) % spin.length;
			}
			catch(InterruptedException e) {
				break;
			}
		}
		
		return;
	}
	
	/* (non-Javadoc)
	 * @see java.awt.Window#setVisible(boolean)
	 */
	public void setVisible(boolean visible)
	{
		if (visible) {
			new Thread(this).start();
			super.setVisible(true);
			return;
		}
		
		this.running = false;
		super.setVisible(false);
		return;
	}
	
}
