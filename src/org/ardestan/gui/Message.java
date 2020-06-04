package org.ardestan.gui;


import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.LinkedList;

import javax.swing.JEditorPane;
import javax.swing.text.BadLocationException;

/**
 * @author hnishino
 *
 */
public class Message extends Thread implements Runnable
{
	private static Message singleton = null;

	JEditorPane messageArea = null;
	
	private LinkedList<String> queue = null;
	
	/**
	 * @param messageArea
	 */
	public static synchronized void initialize(JEditorPane messageArea)
	{
		if (singleton == null) {
			singleton = new Message(messageArea);
		}
		
		return;
	}

	/**
	 * @param textarea
	 */
	private Message(JEditorPane messageArea)
	{
		queue = new LinkedList<String>();
		this.messageArea = messageArea;
		this.start();
	}
	
	/**
	 * @param text
	 */
	public static void print(String text)
	{
		singleton.insertToQueue(text);
	}
	
	
	/**
	 * @param text
	 */
	public static void println(String text)
	{
		print(text + "\n");
	}
	
	/**
	 * @param s
	 */
	public void insertToQueue(String s)
	{
		synchronized (queue) {
			queue.addLast(s);
			queue.notifyAll();
		}
	}
	

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run()
	{
		
		while(true) {
			synchronized(queue) {
				try {
					queue.wait();
					StringBuffer buf = new StringBuffer();
					while(!queue.isEmpty()) {
						String s = queue.getFirst();
						queue.removeFirst();
						buf.append(s);
					}
					this.append(buf.toString());
				}
				catch(InterruptedException e) {
					break;
				}
			}
		}
		
	}
	
	/**
	 * @param s
	 */
	private void append(String s)
	{
		
		int lastIndexOfCR = s.lastIndexOf(13);
		if (lastIndexOfCR != -1) {
			s = s.substring(lastIndexOfCR + 1);
			
			String currentText = singleton.messageArea.getText();
			int lastIndexOfLF = currentText.lastIndexOf(10);
			if (lastIndexOfLF != -1) {
				currentText = currentText.substring(0, lastIndexOfLF + 1);
			}
			else {
				currentText = "";
			}
			messageArea.setText(currentText);
		}
		
		try {
			messageArea.getDocument().insertString(messageArea.getDocument().getLength(), s, null);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		messageArea.setCaretPosition(messageArea.getDocument().getLength());
	}

	/**
	 * @param throwable
	 */
	public static void print(Throwable t)
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try(PrintWriter pw = new PrintWriter(bos))
		{
			t.printStackTrace(pw);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		String s = new String(bos.toByteArray());
		print(s);
	}
	
	/**
	 * @param t
	 */
	public static void println(Throwable t)
	{
		print(t);
		print("\n");
	}
	
	/**
	 * @param text
	 */
	public static void println()
	{
		print("\n");
	}
	
}
