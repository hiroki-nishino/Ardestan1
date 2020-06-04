package org.ardestan.gui;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.dnd.InvalidDnDOperationException;
import java.awt.image.BufferedImage;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.IntStream;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.JPanel;

/**
 * taken from https://github.com/aterai/java-swing-tips
 * MIT Licenses

The MIT License (MIT)

Copyright (c) 2015 TERAI Atsuhiro

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */
public class DnDTabbedPane extends JTabbedPane 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final int LINEWIDTH = 3;
	private static final int RWH = 20;
	private static final int BUTTON_SIZE = 30; // XXX 30 is magic number of scroll button size

	private final GhostGlassPane glassPane = new GhostGlassPane(this);
	protected int dragTabIndex = -1;

	// For Debug: >>>
	protected boolean hasGhost = true;
	protected boolean isPaintScrollArea = false;
	// <<<

	protected Rectangle rectBackward = new Rectangle();
	protected Rectangle rectForward = new Rectangle();

	private void clickArrowButton(String actionKey) {
		JButton scrollForwardButton = null;
		JButton scrollBackwardButton = null;
		for (Component c: getComponents()) {
			if (c instanceof JButton) {
				if (Objects.isNull(scrollForwardButton) && Objects.isNull(scrollBackwardButton)) {
					scrollForwardButton = (JButton) c;
				} else if (Objects.isNull(scrollBackwardButton)) {
					scrollBackwardButton = (JButton) c;
				}
			}
		}
		JButton button = "scrollTabsForwardAction".equals(actionKey) ? scrollForwardButton : scrollBackwardButton;
		Optional.ofNullable(button)
		.filter(JButton::isEnabled)
		.ifPresent(JButton::doClick);

	}

	public void autoScrollTest(Point glassPt) {
		Rectangle r = getTabAreaBounds();
		if (isTopBottomTabPlacement(getTabPlacement())) {
			rectBackward.setBounds(r.x, r.y, RWH, r.height);
			rectForward.setBounds(r.x + r.width - RWH - BUTTON_SIZE, r.y, RWH + BUTTON_SIZE, r.height);
		} else {
			rectBackward.setBounds(r.x, r.y, r.width, RWH);
			rectForward.setBounds(r.x, r.y + r.height - RWH - BUTTON_SIZE, r.width, RWH + BUTTON_SIZE);
		}
		rectBackward = SwingUtilities.convertRectangle(getParent(), rectBackward, glassPane);
		rectForward = SwingUtilities.convertRectangle(getParent(), rectForward, glassPane);
		if (rectBackward.contains(glassPt)) {
			clickArrowButton("scrollTabsBackwardAction");
		} else if (rectForward.contains(glassPt)) {
			clickArrowButton("scrollTabsForwardAction");
		}
	}

	protected DnDTabbedPane() {
		super();
		glassPane.setName("GlassPane");
		new DropTarget(glassPane, DnDConstants.ACTION_COPY_OR_MOVE, new TabDropTargetListener(), true);
		DragSource.getDefaultDragSource().createDefaultDragGestureRecognizer(
				this, DnDConstants.ACTION_COPY_OR_MOVE, new TabDragGestureListener());
	}

	protected int getTargetTabIndex(Point glassPt) {
		Point tabPt = SwingUtilities.convertPoint(glassPane, glassPt, this);
		Point d = isTopBottomTabPlacement(getTabPlacement()) ? new Point(1, 0) : new Point(0, 1);
		return IntStream.range(0, getTabCount()).filter(i -> {
			Rectangle r = getBoundsAt(i);
			r.translate(-r.width * d.x / 2, -r.height * d.y / 2);
			return r.contains(tabPt);
		}).findFirst().orElseGet(() -> {
			int count = getTabCount();
			Rectangle r = getBoundsAt(count - 1);
			r.translate(r.width * d.x / 2, r.height * d.y / 2);
			return r.contains(tabPt) ? count : -1;
		});
	}

	protected void convertTab(int prev, int next) {
		if (next < 0 || prev == next) {
			// This check is needed if tab content is null.
			return;
		}
		final Component cmp = getComponentAt(prev);
		final Component tab = getTabComponentAt(prev);
		final String title = getTitleAt(prev);
		final Icon icon = getIconAt(prev);
		final String tip = getToolTipTextAt(prev);
		final boolean isEnabled = isEnabledAt(prev);
		int tgtindex = prev > next ? next : next - 1;
		remove(prev);
		insertTab(title, icon, cmp, tip, tgtindex);
		setEnabledAt(tgtindex, isEnabled);
		// When you drag'n'drop a disabled tab, it finishes enabled and selected.
		// pointed out by dlorde
		if (isEnabled) {
			setSelectedIndex(tgtindex);
		}
		// I have a component in all tabs (jlabel with an X to close the tab) and when i move a tab the component disappear.
		// pointed out by Daniel Dario Morales Salas
		setTabComponentAt(tgtindex, tab);
	}

	protected void initTargetLine(int next) {
		boolean isLeftOrRightNeighbor = next < 0 || dragTabIndex == next || next - dragTabIndex == 1;
		if (isLeftOrRightNeighbor) {
			glassPane.setTargetRect(0, 0, 0, 0);
			return;
		}
		Optional.ofNullable(getBoundsAt(Math.max(0, next - 1))).ifPresent(boundsRect -> {
			final Rectangle r = SwingUtilities.convertRectangle(this, boundsRect, glassPane);
			int a = Math.min(next, 1); // a = (next == 0) ? 0 : 1;
			if (isTopBottomTabPlacement(getTabPlacement())) {
				glassPane.setTargetRect(r.x + r.width * a - LINEWIDTH / 2, r.y, LINEWIDTH, r.height);
			} else {
				glassPane.setTargetRect(r.x, r.y + r.height * a - LINEWIDTH / 2, r.width, LINEWIDTH);
			}
		});
	}

	protected void initGlassPane(Point tabPt) {
		getRootPane().setGlassPane(glassPane);
		if (hasGhost) {
			Component c = Optional.ofNullable(getTabComponentAt(dragTabIndex))
					.orElseGet(() -> new JLabel(getTitleAt(dragTabIndex)));
			Dimension d = c.getPreferredSize();
			BufferedImage image = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2 = image.createGraphics();
			SwingUtilities.paintComponent(g2, c, glassPane, 0, 0, d.width, d.height);
			g2.dispose();
			glassPane.setImage(image);
		}
		Point glassPt = SwingUtilities.convertPoint(this, tabPt, glassPane);
		glassPane.setPoint(glassPt);
		glassPane.setVisible(true);
	}

	protected Rectangle getTabAreaBounds() {
		Rectangle tabbedRect = getBounds();

		Rectangle compRect = Optional.ofNullable(getSelectedComponent())
				.map(Component::getBounds)
				.orElseGet(Rectangle::new);
		int tabPlacement = getTabPlacement();
		if (isTopBottomTabPlacement(tabPlacement)) {
			tabbedRect.height = tabbedRect.height - compRect.height;
			if (tabPlacement == BOTTOM) {
				tabbedRect.y += compRect.y + compRect.height;
			}
		} else {
			tabbedRect.width = tabbedRect.width - compRect.width;
			if (tabPlacement == RIGHT) {
				tabbedRect.x += compRect.x + compRect.width;
			}
		}
		tabbedRect.grow(2, 2);
		return tabbedRect;
	}

	public static boolean isTopBottomTabPlacement(int tabPlacement) {
		return tabPlacement == JTabbedPane.TOP || tabPlacement == JTabbedPane.BOTTOM;
	}
}

class TabTransferable implements Transferable {
	private static final String NAME = "test";
	private static final DataFlavor FLAVOR = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType, NAME);
	private final Component tabbedPane;

	protected TabTransferable(Component tabbedPane) {
		this.tabbedPane = tabbedPane;
	}

	@Override public Object getTransferData(DataFlavor flavor) {
		return tabbedPane;
	}

	@Override public DataFlavor[] getTransferDataFlavors() {
		return new DataFlavor[] {FLAVOR};
	}

	@Override public boolean isDataFlavorSupported(DataFlavor flavor) {
		return flavor.getHumanPresentableName().equals(NAME);
	}
}

class TabDragSourceListener implements DragSourceListener {
	@Override public void dragEnter(DragSourceDragEvent e) {
		e.getDragSourceContext().setCursor(DragSource.DefaultMoveDrop);
	}

	@Override public void dragExit(DragSourceEvent e) {
		e.getDragSourceContext().setCursor(DragSource.DefaultMoveNoDrop);
	}

	@Override public void dragOver(DragSourceDragEvent e) {
	}

	@Override public void dragDropEnd(DragSourceDropEvent e) {
	}

	@Override public void dropActionChanged(DragSourceDragEvent e) {
	}
}

class TabDragGestureListener implements DragGestureListener {
	@Override public void dragGestureRecognized(DragGestureEvent e) {
		Optional.ofNullable(e.getComponent())
		.filter(c -> c instanceof DnDTabbedPane).map(c -> (DnDTabbedPane) c)
		.filter(tabbedPane -> tabbedPane.getTabCount() > 1)
		.ifPresent(tabbedPane -> {
			Point tabPt = e.getDragOrigin();
			tabbedPane.dragTabIndex = tabbedPane.indexAtLocation(tabPt.x, tabPt.y);
			if (tabbedPane.dragTabIndex >= 0 && tabbedPane.isEnabledAt(tabbedPane.dragTabIndex)) {
				tabbedPane.initGlassPane(tabPt);
				try {
					e.startDrag(DragSource.DefaultMoveDrop, new TabTransferable(tabbedPane), new TabDragSourceListener());
				} catch (InvalidDnDOperationException ex) {
					throw new IllegalStateException(ex);
				}
			}
		});
	}
}

class TabDropTargetListener implements DropTargetListener {
	private static final Point HIDDEN_POINT = new Point(0, -1000);

	private static Optional<GhostGlassPane> getGhostGlassPane(Component c) {
		return Optional.ofNullable(c).filter(GhostGlassPane.class::isInstance).map(GhostGlassPane.class::cast);
	}

	@Override public void dragEnter(DropTargetDragEvent e) {
		getGhostGlassPane(e.getDropTargetContext().getComponent()).ifPresent(glassPane -> {
			Transferable t = e.getTransferable();
			DataFlavor[] f = e.getCurrentDataFlavors();
			if (t.isDataFlavorSupported(f[0])) { // && tabbedPane.dragTabIndex >= 0) {
				e.acceptDrag(e.getDropAction());
			} else {
				e.rejectDrag();
			}
		});
	}

	@Override public void dragExit(DropTargetEvent e) {
		getGhostGlassPane(e.getDropTargetContext().getComponent()).ifPresent(glassPane -> {
			// XXX: glassPane.setVisible(false);
			glassPane.setPoint(HIDDEN_POINT);
			glassPane.setTargetRect(0, 0, 0, 0);
			glassPane.repaint();
		});
	}

	@Override public void dropActionChanged(DropTargetDragEvent e) {
		/* not needed */
	}

	@Override public void dragOver(DropTargetDragEvent e) {
		Component c = e.getDropTargetContext().getComponent();
		getGhostGlassPane(c).ifPresent(glassPane -> {
			Point glassPt = e.getLocation();

			DnDTabbedPane tabbedPane = glassPane.tabbedPane;
			tabbedPane.initTargetLine(tabbedPane.getTargetTabIndex(glassPt));
			tabbedPane.autoScrollTest(glassPt);

			glassPane.setPoint(glassPt);
			glassPane.repaint();
		});
	}

	@Override public void drop(DropTargetDropEvent e) {
		Component c = e.getDropTargetContext().getComponent();
		getGhostGlassPane(c).ifPresent(glassPane -> {
			DnDTabbedPane tabbedPane = glassPane.tabbedPane;
			Transferable t = e.getTransferable();
			DataFlavor[] f = t.getTransferDataFlavors();
			int prev = tabbedPane.dragTabIndex;
			int next = tabbedPane.getTargetTabIndex(e.getLocation());
			if (t.isDataFlavorSupported(f[0]) && prev != next) {
				tabbedPane.convertTab(prev, next);
				e.dropComplete(true);
			} else {
				e.dropComplete(false);
			}
			glassPane.setVisible(false);
			// tabbedPane.dragTabIndex = -1;
			if (tabbedPane instanceof TabbedCodeEditorPane) {
				TabbedCodeEditorPane cepane = (TabbedCodeEditorPane)tabbedPane;
				Component cpnt = cepane.getSelectedComponent();
				if (cpnt instanceof JPanel) {					
					JPanel p = (JPanel)cpnt;
					cepane.updateLabel(p);
				}
			}
		});
	}
}

class GhostGlassPane extends JComponent {
	private static final long serialVersionUID = 1L;
	private static final AlphaComposite ALPHA = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .5f);
	public final DnDTabbedPane tabbedPane;
	private final Rectangle lineRect = new Rectangle();
	private final Color lineColor = new Color(0, 100, 255);
	private final Point location = new Point();
	private transient Optional<BufferedImage> draggingGhostOp;

	protected GhostGlassPane(DnDTabbedPane tabbedPane) {
		super();
		this.tabbedPane = tabbedPane;
		setOpaque(false);
		// [JDK-6700748] Cursor flickering during D&D when using CellRendererPane with validation - Java Bug System
		// https://bugs.openjdk.java.net/browse/JDK-6700748
		// setCursor(null);
	}

	public void setTargetRect(int x, int y, int width, int height) {
		lineRect.setBounds(x, y, width, height);
	}

	public void setImage(BufferedImage draggingGhost) {
		this.draggingGhostOp = Optional.ofNullable(draggingGhost);
	}

	public void setPoint(Point pt) {
		this.location.setLocation(pt);
	}

	@Override public void setVisible(boolean v) {
		super.setVisible(v);
		if (!v) {
			setTargetRect(0, 0, 0, 0);
			setImage(null);
		}
	}

	@Override protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g.create();
		g2.setComposite(ALPHA);
		if (tabbedPane.isPaintScrollArea && tabbedPane.getTabLayoutPolicy() == JTabbedPane.SCROLL_TAB_LAYOUT) {
			g2.setPaint(Color.RED);
			g2.fill(tabbedPane.rectBackward);
			g2.fill(tabbedPane.rectForward);
		}
		draggingGhostOp.ifPresent(img -> {
			double xx = location.getX() - img.getWidth(this) / 2d;
			double yy = location.getY() - img.getHeight(this) / 2d;
			g2.drawImage(img, (int) xx, (int) yy, null);
		});
		g2.setPaint(lineColor);
		g2.fill(lineRect);
		g2.dispose();
	}
}
