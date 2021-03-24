package com.gms.web.admin.common.view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class ExcelDownloadDatas<E> extends ArrayList<E> {
	private static final long serialVersionUID = -3540238516552791708L;

	public static final String NO_COLUNM = "_NO";
	
	public enum FormatType {
		DataFormat,
		DecimalFormat,
		RatioFormat
	}
	
	public enum Align {
		Left,
		Center,
		Right
	}
	
	public static class ColumnInfo {
		/* data field property name */
		String propertyName;
		
		/* column Title */
		String colunmName;
		
		/* column width(1/1000 Cm) */
		Integer columnWidth = null;
		
		/* column align */
		Align colunmAlign = null;
		
		FormatType formatType = null;
		
		String sourceFormat = null;

		String displayFormat = null;
		
		String cdGrpId = null;
		
		public ColumnInfo(String propertyName, String colunmName) {
			this(propertyName, colunmName, null, null, null, null, null);
		}
		
		public ColumnInfo(String propertyName, String colunmName, Integer columnWidth) {
			this(propertyName, colunmName, null, null, null, columnWidth, null);
		}
		
		public ColumnInfo(String propertyName, String colunmName, Integer columnWidth, Align colunmAlign) {
			this(propertyName, colunmName, null, null, null, columnWidth, colunmAlign);
		}
		
		public ColumnInfo(String propertyName, String colunmName, Align colunmAlign) {
			this(propertyName, colunmName, null, null, null, null, colunmAlign);
		}
		
		public ColumnInfo(String propertyName, String colunmName, FormatType formatType, String sourceFormat, String displayFormat) {
			this(propertyName, colunmName, formatType, sourceFormat, displayFormat, null, null);
		}
		
		public ColumnInfo(String propertyName, String colunmName, FormatType formatType, String sourceFormat, String displayFormat, Integer columnWidth, Align colunmAlign) {
			this.propertyName = propertyName;
			this.colunmName = colunmName;
			this.formatType = formatType;
			this.sourceFormat = sourceFormat;
			this.displayFormat = displayFormat;
			this.columnWidth = columnWidth;
			this.colunmAlign = colunmAlign;
		}
		
		public ColumnInfo setCdGrpId(String cdGrpId) {
			this.cdGrpId = cdGrpId;
			return this;
		}
		
		public ColumnInfo setFormatType(FormatType formatType) {
			this.formatType = formatType;
			return this;
		}
		
		public ColumnInfo setColumnWidth(Integer width) {
			this.columnWidth = width;
			return this;
		}
		
		public ColumnInfo setColumnAlign(Align align) {
			this.colunmAlign = align;
			return this;
		}
		
		public ColumnInfo setDisplayFormat(String displayFormat) {
			this.displayFormat = displayFormat;
			return this;
		}
	}
	
	public List<ColumnInfo> columns = null;
	
	private String sheetName = null;
	
	private String excelFileName = null;
	
	public ExcelDownloadDatas() {
		super();
	}
	
	public ExcelDownloadDatas(Collection<E> c) {
		super(c);
	}
	
	
	public void addColumnInfo(ColumnInfo columnInfo) {
		if(columns == null) {
			columns = new ArrayList<ColumnInfo>();
		}
		columns.add(columnInfo);
	}

	public String getSheetName() {
		return sheetName;
	}

	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}

	public String getExcelFileName() {
		return excelFileName;
	}

	public void setExcelFileName(String excelFileName) {
		this.excelFileName = excelFileName;
	}
	
	
}