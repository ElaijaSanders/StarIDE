package guiControls;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.plaf.basic.BasicScrollBarUI;

@SuppressWarnings("serial")
public class SFXScrollPane extends JScrollPane {
	public SFXScrollPane(Component view) {
		super(view);
	}
	@Override public JScrollBar createHorizontalScrollBar() {
		JScrollBar hsb = super.createHorizontalScrollBar();
		hsb.setUI(new SFXScrollBarUI());
		return hsb;
	} @Override public JScrollBar createVerticalScrollBar() {
		JScrollBar vsb = super.createVerticalScrollBar();
		vsb.setUI(new SFXScrollBarUI());
		return vsb;
	}
	static class SFXScrollBarUI extends BasicScrollBarUI {
		/*
		 * Copyright (c) 2021 yaBobJonez
		 * 
		 * You may NOT reproduce and use this class anywhere, except Star IDE; sell, modify, or distribute.
		 * 
		 * DO NOT REMOVE OR CHANGE THIS NOTICE.
		 */
		@Override
		protected JButton createDecreaseButton(int orientation) {
			return new BasicArrowButton(orientation){
				@Override public void paint(Graphics g) {
					if(this.direction == NORTH) this.paintNorth(g);
					else this.paintWest(g);
				} private void paintWest(Graphics g){
					Graphics2D d = (Graphics2D)g;
					d.setPaint(new LinearGradientPaint(0, 0, 0, this.getHeight(), new float[]{0f, 0.5f, 1f},
						new Color[]{Color.decode("#d5d5d5"), Color.decode("#e8e8e8"), Color.decode("#d5d5d5")}));
					d.fillRect(0, 0, this.getWidth(), this.getHeight());
					d.setColor(Color.decode("#cacaca"));
					d.drawRect(0, 0, this.getWidth(), this.getHeight()-1);
					int size = getHeight() / 4;
					this.paintTriangle(g, (getWidth()-size)/2, (getHeight()-size)/2, size, this.direction, this.isEnabled());
				} private void paintNorth(Graphics g){
					Graphics2D d = (Graphics2D)g;
					d.setPaint(new LinearGradientPaint(0, 0, this.getWidth(), 0, new float[]{0f, 0.5f, 1f},
						new Color[]{Color.decode("#d5d5d5"), Color.decode("#e8e8e8"), Color.decode("#d5d5d5")}));
					d.fillRect(0, 0, this.getWidth(), this.getHeight());
					d.setColor(Color.decode("#cacaca"));
					d.drawRect(0, 0, this.getWidth()-1, this.getHeight());
					int size = getHeight() / 4;
					this.paintTriangle(g, (getWidth()-size)/2, (getHeight()-size)/2, size, this.direction, this.isEnabled());
				} @Override public void paintTriangle(Graphics g, int x, int y, int size, int direction, boolean isEnabled){
					if(direction == NORTH) this.paintArrowNorth(g, x, y, size, isEnabled);
					else this.paintArrowWest(g, x, y, size, isEnabled);
				} private void paintArrowWest(Graphics g, int x, int y, int size, boolean isEnabled){
					int tipX = x - 1;
					int tipY = y + size / 2;
					int baseX = x + size - 1;
					int baseY1 = tipY - size;
					int baseY2 = tipY + size;
					Path2D arrow = new Path2D.Double();
					arrow.moveTo(baseX, baseY1);
					arrow.lineTo(tipX, tipY);
					arrow.lineTo(baseX, baseY2);
					Graphics2D d = (Graphics2D)g;
					d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
					d.setColor(Color.decode("#626365"));
					d.draw(arrow);
				} private void paintArrowNorth(Graphics g, int x, int y, int size, boolean isEnabled){
					int tipX = x + size / 2;
					int tipY = y;
					int baseX1 = tipX - size;
					int baseX2 = tipY + size + 2;
					int baseY = y + size - 1;
					Path2D arrow = new Path2D.Double();
					arrow.moveTo(baseX1, baseY);
					arrow.lineTo(tipX, tipY);
					arrow.lineTo(baseX2, baseY);
					Graphics2D d = (Graphics2D)g;
					d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
					d.setColor(Color.decode("#626365"));
					d.draw(arrow);
				}
			};
		}
		@Override
		protected JButton createIncreaseButton(int orientation) {
			return new BasicArrowButton(orientation){
				@Override public void paintTriangle(Graphics g, int x, int y, int size, int direction, boolean isEnabled){
					if(direction == SOUTH) this.paintArrowSouth(g, x, y, size, isEnabled);
					else this.paintArrowEast(g, x, y, size, isEnabled);
				} private void paintArrowSouth(Graphics g, int x, int y, int size, boolean isEnabled){
					int tipX = x + size / 2;
					int tipY = y + size - 1;
					int baseX1 = tipX - size;
					int baseX2 = tipY + size - 1;
					int baseY = y;
					Path2D arrow = new Path2D.Double();
					arrow.moveTo(baseX1, baseY);
					arrow.lineTo(tipX, tipY);
					arrow.lineTo(baseX2, baseY);
					Graphics2D d = (Graphics2D)g;
					d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
					d.setColor(Color.decode("#626365"));
					d.draw(arrow);
				} private void paintArrowEast(Graphics g, int x, int y, int size, boolean isEnabled){
					int tipX = x + size - 1;
					int tipY = y + size / 2;
					int baseX = x - 1;
					int baseY1 = tipY - size;
					int baseY2 = tipY + size;
					Path2D arrow = new Path2D.Double();
					arrow.moveTo(baseX, baseY1);
					arrow.lineTo(tipX, tipY);
					arrow.lineTo(baseX, baseY2);
					Graphics2D d = (Graphics2D)g;
					d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
					d.setColor(Color.decode("#626365"));
					d.draw(arrow);
				} @Override public void paint(Graphics g) {
					if(this.direction == SOUTH) this.paintSouth(g);
					else this.paintEast(g);
				} private void paintSouth(Graphics g){
					Graphics2D d = (Graphics2D)g;
					d.setPaint(new LinearGradientPaint(0, 0, this.getWidth(), 0, new float[]{0f, 0.5f, 1f},
						new Color[]{Color.decode("#d5d5d5"), Color.decode("#ebebeb"), Color.decode("#d5d5d5")}));
					d.fillRect(0, 0, this.getWidth(), this.getHeight());
					d.setColor(Color.decode("#cacaca"));
					d.drawRect(0, -1, this.getWidth()-1, this.getHeight());
					int size = getHeight() / 4;
					this.paintTriangle(g, (getWidth()-size)/2, (getHeight()-size)/2, size, this.direction, this.isEnabled());
				} private void paintEast(Graphics g){
					Graphics2D d = (Graphics2D)g;
					d.setPaint(new LinearGradientPaint(0, 0, 0, this.getHeight(), new float[]{0f, 0.5f, 1f},
						new Color[]{Color.decode("#d5d5d5"), Color.decode("#ebebeb"), Color.decode("#d5d5d5")}));
					d.fillRect(0, 0, this.getWidth(), this.getHeight());
					d.setColor(Color.decode("#cacaca"));
					d.drawRect(-1, 0, this.getWidth(), this.getHeight()-1);
					int size = getHeight() / 4;
					this.paintTriangle(g, (getWidth()-size)/2, (getHeight()-size)/2, size, this.direction, this.isEnabled());
				}
			};
		}
		@Override
		protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
			if(this.scrollbar.getOrientation() == VERTICAL) this.paintThumbVertical(g, c, thumbBounds);
			else this.paintThumbHorizontal(g, c, thumbBounds);
		} private void paintThumbVertical(Graphics g, JComponent c, Rectangle thumbBounds){
			Graphics2D d = (Graphics2D)g;
			d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			d.setPaint(new LinearGradientPaint(thumbBounds.x+3, 0, thumbBounds.width-3, 0, new float[]{0f, 0.7f},
				new Color[]{Color.decode("#dcdcdc"), Color.decode("#f9f9f9")}));
			d.fillRoundRect(thumbBounds.x+2, thumbBounds.y, thumbBounds.width-5, thumbBounds.height, 5, 5);
			d.setColor(Color.decode("#b3b3b3"));
			d.drawRoundRect(thumbBounds.x+2, thumbBounds.y, thumbBounds.width-5, thumbBounds.height-1, 5, 5);
			d.setColor(Color.decode("#cacaca"));
			//d.drawLine(thumbBounds.x, c.getHeight()-1, thumbBounds.width + thumbBounds.x, c.getHeight()-1);
		} private void paintThumbHorizontal(Graphics g, JComponent c, Rectangle thumbBounds){
			Graphics2D d = (Graphics2D)g;
			d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			d.setPaint(new LinearGradientPaint(0, thumbBounds.y+3, 0, thumbBounds.height-3, new float[]{0f, 0.7f},
				new Color[]{Color.decode("#dcdcdc"), Color.decode("#f9f9f9")}));
			d.fillRoundRect(thumbBounds.x, thumbBounds.y+1, thumbBounds.width, thumbBounds.height-4, 5, 5);
			d.setColor(Color.decode("#b3b3b3"));
			d.drawRoundRect(thumbBounds.x, thumbBounds.y+1, thumbBounds.width-1, thumbBounds.height-4, 5, 5);
			d.setColor(Color.decode("#cacaca"));
			d.drawLine(thumbBounds.x, c.getHeight()-1, thumbBounds.width + thumbBounds.x, c.getHeight()-1);
		}
		@Override
		protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
			if(this.scrollbar.getOrientation() == VERTICAL) this.paintTrackVertical(g, c, trackBounds);
			else this.paintTrackHorizonatal(g, c, trackBounds);
		} private void paintTrackVertical(Graphics g, JComponent c, Rectangle trackBounds){
			Graphics2D d = (Graphics2D)g;
			d.setPaint(new LinearGradientPaint(0, 0, trackBounds.width, 0, new float[]{0f, 0.5f, 1f},
				new Color[]{Color.decode("#d5d5d5"), Color.decode("#ebebeb"), Color.decode("#d5d5d5")}));
			d.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
			d.setColor(Color.decode("#cacaca"));
			d.drawRect(trackBounds.x, trackBounds.y-1, trackBounds.width-1, trackBounds.height+1);
		} private void paintTrackHorizonatal(Graphics g, JComponent c, Rectangle trackBounds){
			Graphics2D d = (Graphics2D)g;
			d.setPaint(new LinearGradientPaint(0, 0, 0, trackBounds.height, new float[]{0f, 0.5f, 1f},
				new Color[]{Color.decode("#d5d5d5"), Color.decode("#ebebeb"), Color.decode("#d5d5d5")}));
			d.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
			d.setColor(Color.decode("#cacaca"));
			d.drawRect(trackBounds.x-1, trackBounds.y, trackBounds.width+1, trackBounds.height-1);
		}
	}
}