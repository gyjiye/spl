package com.scudata.ide.common.swing;

import java.awt.Color;
import java.awt.Component;
import java.math.BigDecimal;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellRenderer;

import com.scudata.common.StringUtils;
import com.scudata.dm.Record;
import com.scudata.dm.Sequence;
import com.scudata.ide.common.ConfigOptions;
import com.scudata.ide.common.GC;
import com.scudata.ide.common.GM;
import com.scudata.util.Variant;

/**
 * JTable�ĵ�Ԫ����Ⱦ��
 *
 */
public class AllPurposeRenderer implements TableCellRenderer {
	/**
	 * ֧���»��ߵ��ı��ؼ�
	 */
	private JLabelUnderLine textField = new JLabelUnderLine();
	/**
	 * �Ƿ��������
	 */
	private boolean hasIndex = false;

	/**
	 * ��ʾ��ʽ
	 */
	private String format;

	/**
	 * NULL����ʾֵ
	 */
	public static String DISP_NULL = "(null)";

	/**
	 * ���캯��
	 */
	public AllPurposeRenderer() {
		this(false);
	}

	/**
	 * ���캯��
	 * 
	 * @param hasIndex �Ƿ��������
	 */
	public AllPurposeRenderer(boolean hasIndex) {
		this.hasIndex = hasIndex;
	}

	/**
	 * ���캯��
	 * 
	 * @param format ��ʾ��ʽ
	 */
	public AllPurposeRenderer(String format) {
		this(format, false);
	}

	/**
	 * ���캯��
	 * 
	 * @param format   ��ʾ��ʽ
	 * @param hasIndex �Ƿ��������
	 */
	public AllPurposeRenderer(String format, boolean hasIndex) {
		this.format = format;
		this.hasIndex = hasIndex;
		textField.setBorder(null);
	}

	/**
	 * ȡ��ʾ�ؼ�
	 */
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		if (isSelected) {
			textField.setForeground(table.getSelectionForeground());
			// ������������Ͻǵĸ��б���ɫ
			if (ConfigOptions.getCellColor() != null) {
				textField.setBackground(ConfigOptions.getCellColor());
			} else { // δ�Զ���������ɫ������ϵͳĬ����ɫ
				textField.setBackground(table.getSelectionBackground());
			}
			textField.setOpaque(true);
		} else {
			textField.setBackground(table.getBackground());
			textField.setForeground(table.getForeground());
		}
		textField.setValue(value);
		if (isRefVal(value)) {
			textField.setForeground(Color.CYAN.darker());
		}
		boolean isNumber = value != null && value instanceof Number;
		boolean isDate = value != null && value instanceof Date;
		if (isNumber) {
			textField.setHorizontalAlignment(JLabel.RIGHT);
		} else {
			textField.setHorizontalAlignment(JLabel.LEFT);
		}

		String strText = null;
		try {
			// �������������Ĳ�format
			Pattern p = Pattern.compile("[#\\.0]");
			Matcher m = p.matcher(format);
			boolean numFormat = m.find();
			if ((numFormat && isNumber) || (isDate && !numFormat)) { // �кϷ���ʽ���ø�ʽ��ʾ
				strText = Variant.format(value, format);
			} else {
				strText = GM.renderValueText(value);
			}
		} catch (Exception e) {
			if (value != null) {
				strText = GM.renderValueText(value);
			}
		}
		if (StringUtils.isValidString(strText)) { // ������ʱ��һ���ո�
			if (isNumber) {
				strText += GC.STR_INDENT;
			} else {
				strText = GC.STR_INDENT + strText;
			}
		}
		if (value != null) {
			if (value instanceof BigDecimal) {
				textField.setForeground(ConfigOptions.COLOR_DECIMAL);
			} else if (value instanceof Double) {
				textField.setForeground(ConfigOptions.COLOR_DOUBLE);
			} else if (value instanceof Integer) {
				if (!hasIndex || column > 0)
					textField.setForeground(ConfigOptions.COLOR_INTEGER);
			}
			textField.setText(strText);
		} else {
			textField.setText(DISP_NULL);
			textField.setHorizontalAlignment(JTextField.CENTER);
			textField.setForeground(ConfigOptions.COLOR_NULL);
		}
		return textField;
	}

	private boolean isRefVal(Object val) {
		if (val == null) {
			return false;
		}
		return val instanceof Record || val instanceof Sequence;
	}
}