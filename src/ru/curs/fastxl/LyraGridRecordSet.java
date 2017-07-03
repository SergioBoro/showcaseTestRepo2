package ru.curs.fastxl;

import ru.curs.celesta.CelestaException;
import ru.curs.celesta.dbutils.BasicCursor;
import ru.curs.celesta.score.*;

public class LyraGridRecordSet implements GridRecordSet {
	private final BasicCursor c;
	private final ColumnMeta[] m;
	private final String[] names;
	private Object[] buf;

	private boolean firstRecord = true;

	public LyraGridRecordSet(BasicCursor cursor) throws CelestaException {
		c = cursor._getBufferCopy(cursor.callContext());
		c.copyFiltersFrom(cursor);
		c.copyOrderFrom(cursor);
		m = cursor.meta().getColumns().values().toArray(new ColumnMeta[0]);
		names = cursor.meta().getColumns().keySet().toArray(new String[0]);
		c.tryFirst();
		buf = c._currentValues();
	}

	@Override
	public boolean next() throws EFastXLRuntime {
		if (firstRecord) {
			firstRecord = false;
			return true;
		}
		try {
			boolean result = c.next();
			buf = c._currentValues();
			return result;
		} catch (CelestaException e) {
			throw new EFastXLRuntime(e);
		}
	}

	@Override
	public boolean isInteger(int i) throws EFastXLRuntime {
		return m[i - 1].getCelestaType() == IntegerColumn.CELESTA_TYPE;
	}

	@Override
	public boolean isFloat(int i) throws EFastXLRuntime {
		return m[i - 1].getCelestaType() == FloatingColumn.CELESTA_TYPE;
	}

	@Override
	public String getColumnName(int i) throws EFastXLRuntime {
		return names[i - 1];
	}

	@Override
	public int getColumnCount() throws EFastXLRuntime {
		return names.length;
	}

	@Override
	public double getDouble(int i) throws EFastXLRuntime {
		Object v = buf[i - 1];
		if (v == null)
			return 0;
		else if (v instanceof Double)
			return (Double) v;
		else
			return Double.parseDouble(v.toString());
	}

	@Override
	public int getInt(int i) throws EFastXLRuntime {
		Object v = buf[i - 1];
		if (v == null)
			return 0;
		else if (v instanceof Integer)
			return (Integer) v;
		else
			return Integer.parseInt(v.toString());
	}

	@Override
	public String getString(int i) throws EFastXLRuntime {
		Object v = buf[i - 1];
		if (v == null)
			return "";
		else
			return v.toString();
	}

}
