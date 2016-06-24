package com.github.richie;

import java.sql.Connection;

public class RuntimeContext {
	public final Connection cnx;
	public final DbDialect dialect;

	public RuntimeContext(Connection cnx, DbDialect dialect) {
		this.cnx = cnx;
		this.dialect = dialect;
	}

}
