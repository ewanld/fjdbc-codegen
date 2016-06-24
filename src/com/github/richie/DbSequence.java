package com.github.richie;

import java.math.BigDecimal;

import com.github.richie.SqlExpr.SqlExprRaw;

public class DbSequence<DTO> {
	public final SqlExpr<BigDecimal, DTO> nextVal;
	public final SqlExpr<BigDecimal, DTO> currVal;

	public DbSequence(String sequenceName) {
		nextVal = new SqlExprRaw<>(sequenceName + ".nextval", BigDecimal.class);
		currVal = new SqlExprRaw<>(sequenceName + ".currval", BigDecimal.class);
	}

}