package com.github.richie.test_tool;

import com.github.richie.DbTable;
import com.github.richie.DbTableCollection;

public class TestDbTableCollection implements TestCase {
	private final DbTableCollection tables;

	public TestDbTableCollection(DbTableCollection tables) {
		this.tables = tables;
	}

	@Override
	public void test() throws Exception {
		for (final DbTable<?> table : tables.getTables()) {
			new TestDbTable<>(table).test();
		}
	}

}
