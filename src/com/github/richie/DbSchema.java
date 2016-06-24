package com.github.richie;

import java.util.Collection;

public abstract class DbSchema {
	private Collection<DbTable<?>> tables;

	public Collection<DbTable<?>> getTables() {
		return tables;
	}

	public void setTables(Collection<DbTable<?>> tables) {
		this.tables = tables;
	}
}
