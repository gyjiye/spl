package com.scudata.ide.dfx.control;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.HashSet;

import javax.swing.JPanel;

import com.scudata.ide.common.GC;
import com.scudata.ide.common.GM;
import com.scudata.ide.dfx.GCDfx;

/**
 * ���׸����
 *
 */
public class RowHeaderPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	/** ����༭�ؼ� */
	private DfxControl control;
	/**
	 * �Ƿ���Ա༭
	 */
	private boolean editable = true;
	/**
	 * ���������
	 */
	CellSetParser parser;
	/**
	 * ͼ��ĳߴ�
	 */
	private final int ICON_SIZE = 12;

	/**
	 * ���׸���幹�캯��
	 * 
	 * @param control
	 *            ����༭�ؼ�
	 */
	public RowHeaderPanel(DfxControl control) {
		this(control, true);
	}

	/**
	 * ���׸���幹�캯��
	 * 
	 * @param control
	 *            ����༭�ؼ�
	 * @param editable
	 *            �Ƿ���Ա༭
	 */
	public RowHeaderPanel(DfxControl control, boolean editable) {
		this.control = control;
		this.editable = editable;
		this.parser = new CellSetParser(control.dfx);
		initCoords();
		int w = getW(control);
		setPreferredSize(new Dimension(w + 1, (int) getPreferredSize()
				.getHeight()));
	}

	/**
	 * ��ʼ��������
	 */
	public void initRowLocations() {
		int rows = control.dfx.getRowCount() + 1;
		initRowLocations(rows);
	}

	/**
	 * ��ʼ��������
	 * 
	 * @param rows
	 *            ����
	 */
	public void initRowLocations(int rows) {
		control.cellY = new int[rows];
		control.cellH = new int[rows];
	}

	/**
	 * ��ʼ������
	 */
	private void initCoords() {
		int rows = control.dfx.getRowCount() + 1;
		if (control.cellY == null || rows != control.cellY.length) {
			initRowLocations(rows);
		}
		for (int i = 1; i < rows; i++) {
			if (i == 1) {
				control.cellY[i] = 1;
			} else {
				control.cellY[i] = control.cellY[i - 1] + control.cellH[i - 1];
			}
			if (!parser.isRowVisible(i)) {
				control.cellH[i] = 0;
			} else {
				control.cellH[i] = (int) control.dfx.getRowCell(i).getHeight();
			}
		}
	}

	/**
	 * �������
	 * 
	 * @param g
	 *            ����
	 */
	public void paintComponent(Graphics g) {
		int w = getW(control);
		int levelWidth = getLevelWidth(control);
		int headWidth = w - levelWidth;
		g.clearRect(0, 0, w + 1, 999999);
		int rows = control.dfx.getRowCount() + 1;
		if (rows != control.cellY.length) {
			initRowLocations(rows);
		}
		Rectangle r = control.getViewport().getViewRect();
		HashSet<Integer> selectedRows = ControlUtils.listSelectedRows(control
				.getSelectedAreas());
		for (int i = 1; i < rows; i++) {
			if (i == 1) {
				control.cellY[i] = 1;
			} else {
				control.cellY[i] = control.cellY[i - 1] + control.cellH[i - 1];
			}
			if (!parser.isRowVisible(i)) {
				control.cellH[i] = 0;
				continue;
			} else {
				control.cellH[i] = (int) control.dfx.getRowCell(i).getHeight();
			}

			if (control.cellY[i] + control.cellH[i] <= r.y) {
				continue;
			}
			if (control.cellY[i] >= r.y + r.height) {
				break;
			}

			Color bkColor = Color.lightGray;
			String label = String.valueOf(i);
			byte flag = GC.SELECT_STATE_NONE;
			if (selectedRows.contains(new Integer(i))) {
				flag = GC.SELECT_STATE_CELL;
			}
			for (int k = 0; k < control.m_selectedRows.size(); k++) {
				if (i == ((Integer) control.m_selectedRows.get(k)).intValue()) {
					flag = GC.SELECT_STATE_ROW;
					break;
				}
			}
			int y = control.cellY[i];
			int h = control.cellH[i];
			if (i > 1) {
				y++;
				h--;
			}
			ControlUtils.drawHeader(g, 0, y, w, h, label, control.scale,
					bkColor, flag, editable);
			int subEnd = parser.getSubEnd(i);
			String imgPath = "";
			if (subEnd > i && i + 1 < rows) {
				imgPath += GC.IMAGES_PATH;
				if (parser.isSubExpand(i, subEnd))
					imgPath += "rowshrink.gif";
				else
					imgPath += "rowexpand.gif";
				Image img = GM.getImageIcon(imgPath).getImage();
				g.drawImage(img, headWidth + (levelWidth - ICON_SIZE) / 2, y
						+ (h - ICON_SIZE) / 2, ICON_SIZE, ICON_SIZE, null);
			}

			if (control.isBreakPointRow(i)) {
				Image image = GM.getMenuImageIcon(GCDfx.BREAKPOINTS).getImage();
				g.drawImage(image, 0, y + h / 2 - 8, 16, 16, null);
			}
		}
		setPreferredSize(new Dimension(w + 1, (int) getPreferredSize()
				.getHeight()));
		g.dispose();
	}

	/**
	 * ����ͼƬ
	 */
	final static BufferedImage BI = new BufferedImage(5, 5,
			BufferedImage.TYPE_INT_RGB);

	/**
	 * ÿһ��Ŀ���
	 */
	private static final int LEVEL_WIDTH = 14;

	/**
	 * ȡ���׸����Ŀ���
	 * 
	 * @param control
	 *            ����ؼ�
	 * @return
	 */
	public static int getW(DfxControl control) {
		return getHeaderW(control) + getLevelWidth(control);
	}

	/**
	 * ȡ���׸����Ŀ���
	 * 
	 * @param control
	 *            ����ؼ�
	 * @return
	 */
	public static int getHeaderW(DfxControl control) {
		Graphics g = BI.getGraphics();
		String label = String.valueOf(control.dfx.getRowCount());
		int w1 = g.getFontMetrics(g.getFont()).stringWidth(label) + 5;
		int w2 = (int) (GCDfx.DEFAULT_ROWHEADER_WIDTH * control.scale);
		return Math.max(w1, w2);
	}

	/**
	 * ȡ��Ŀ���
	 * 
	 * @param control
	 *            ����ؼ�
	 * @return
	 */
	private static int getLevelWidth(DfxControl control) {
		return (int) (LEVEL_WIDTH * control.scale);
	}

	/**
	 * ȡ���ߴ��С
	 */
	public Dimension getPreferredSize() {
		int height = 0;
		for (int row = 1; row <= control.dfx.getRowCount(); row++) {
			if (parser.isRowVisible(row)) {
				height += control.dfx.getRowCell(row).getHeight();
			}
		}
		int w = getW(control);

		return new Dimension(w + 1, height + 2);
	}

}