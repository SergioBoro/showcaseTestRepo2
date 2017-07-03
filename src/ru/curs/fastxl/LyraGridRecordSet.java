package ru.curs.fastxl;

import java.util.Map;

import ru.curs.celesta.CelestaException;
import ru.curs.celesta.dbutils.BasicCursor;
import ru.curs.celesta.score.*;
import ru.curs.lyra.LyraFormField;

/**
 * LyraGridRecordSet.
 */
public class LyraGridRecordSet implements GridRecordSet {
	private final BasicCursor c;
	private final ColumnMeta[] m;
	private final String[] names;
	private Object[] buf;

	private boolean firstRecord = true;
	private final Map<String, LyraFormField> lyraFields;

	public LyraGridRecordSet(final BasicCursor cursor,
			final Map<String, LyraFormField> aLyraFields) throws CelestaException {
		c = cursor._getBufferCopy(cursor.callContext());
		c.copyFiltersFrom(cursor);
		c.copyOrderFrom(cursor);
		m = cursor.meta().getColumns().values().toArray(new ColumnMeta[0]);
		names = cursor.meta().getColumns().keySet().toArray(new String[0]);
		c.tryFirst();
		buf = c._currentValues();

		lyraFields = aLyraFields;
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
	public boolean isInteger(final int i) throws EFastXLRuntime {
		return m[i - 1].getCelestaType() == IntegerColumn.CELESTA_TYPE;
	}

	@Override
	public boolean isFloat(final int i) throws EFastXLRuntime {
		return m[i - 1].getCelestaType() == FloatingColumn.CELESTA_TYPE;
	}

	@Override
	public String getColumnName(final int i) throws EFastXLRuntime {
		return lyraFields.get(names[i - 1]).getCaption();
	}

	@Override
	public int getColumnCount() throws EFastXLRuntime {
		return names.length;
	}

	@Override
	public double getDouble(final int i) throws EFastXLRuntime {
		Object v = buf[i - 1];
		if (v == null) {
			return 0;
		} else if (v instanceof Double) {
			return (Double) v;
		} else {
			return Double.parseDouble(v.toString());
		}
	}

	@Override
	public int getInt(final int i) throws EFastXLRuntime {
		Object v = buf[i - 1];
		if (v == null) {
			return 0;
		} else if (v instanceof Integer) {
			return (Integer) v;
		} else {
			return Integer.parseInt(v.toString());
		}
	}

	@Override
	public String getString(final int i) throws EFastXLRuntime {
		Object v = buf[i - 1];
		if (v == null) {
			return "";
		} else {
			return v.toString();
		}
	}

}
