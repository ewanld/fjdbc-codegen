package com.github.richie.test_tool;

import java.util.List;

import com.github.richie.AggregateFunction;
import com.github.richie.DbTable;
import com.github.richie.Field;

public class TestDbTable<DTO> implements TestCase {

	private final DbTable<DTO> table;

	public TestDbTable(DbTable<DTO> table) {
		this.table = table;
	}

	@Override
	public void test() throws Exception {
		testSelect();
	}

	public void testSelect() {
		List<DTO> list = table.select().limit(10).toList();
		if (list == null) throw new NullPointerException();

		list = table.select().orderBy(table.getFields().iterator().next().asc()).toList();
		if (list == null) throw new NullPointerException();

		for (final Field<?, DTO> field : table.getFields()) {
			final List<?> list2 = field.select().limit(10).toList();
			if (list2 == null) throw new NullPointerException();

			final Object minValue = field.aggregate(AggregateFunction.MIN, null);
		}
	}
}
