package com.github.richie;

import java.sql.Connection;
import java.util.Collection;

public class DbTableCollection {
	private Collection<DbTable<?>> tables;

	public Collection<DbTable<?>> getTables() {
		return tables;
	}

	public void setTables(Collection<DbTable<?>> tables) {
		this.tables = tables;
	}
}
