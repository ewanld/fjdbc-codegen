package com.github.richie;

import com.github.fjdbc.ConnectionProvider;

public class RuntimeContext {
	public final ConnectionProvider cnxProvider;
	public final DbDialect dialect;

	public RuntimeContext(ConnectionProvider cnxProvider, DbDialect dialect) {
		this.cnxProvider = cnxProvider;
		this.dialect = dialect;
	}

}
