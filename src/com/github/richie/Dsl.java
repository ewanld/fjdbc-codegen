package com.github.richie;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.github.richie.Condition.ConditionAnd;
import com.github.richie.Condition.ConditionNot;
import com.github.richie.Condition.ConditionOr;
import com.github.richie.SqlExpr.SqlExprFunction1;
import com.github.richie.SqlExpr.SqlExprRaw;
import com.github.richie.SqlExpr.SqlExprRelational;

public class Dsl {

	public static <DTO> SqlExpr<String, DTO> lower(SqlExpr<String, DTO> value) {
		final SqlExprFunction1<String, String, DTO> res = new SqlExprFunction1<>("LOWER", value, String.class);
		res.setParent(value.getParent());
		return res;
	}

	public static <DTO> SqlExpr<String, DTO> upper(SqlExpr<String, DTO> value) {
		final SqlExprFunction1<String, String, DTO> res = new SqlExprFunction1<>("UPPER", value, String.class);
		res.setParent(value.getParent());
		return res;
	}

	public static <DTO> SqlExpr<Integer, DTO> length(SqlExpr<String, DTO> value) {
		final SqlExprFunction1<String, Integer, DTO> res = new SqlExprFunction1<>("length", value, Integer.class);
		res.setParent(value.getParent());
		return res;
	}

	public static <DTO, T extends Number> SqlExpr<T, DTO> abs(SqlExpr<T, DTO> value) {
		return function1("abs", value, value.type);
	}

	public static <DTO, T extends Number> SqlExpr<T, DTO> cos(SqlExpr<T, DTO> value) {
		return function1("cos", value, value.type);
	}

	public static <DTO, T extends Number> SqlExpr<T, DTO> acos(SqlExpr<T, DTO> value) {
		return function1("acos", value, value.type);
	}

	public static <DTO, T extends Number> SqlExpr<T, DTO> asin(SqlExpr<T, DTO> value) {
		return function1("asin", value, value.type);
	}

	public static <DTO, T extends Number> SqlExpr<T, DTO> atan(SqlExpr<T, DTO> value) {
		return function1("atan", value, value.type);
	}

	public static <DTO, T extends Number> SqlExpr<T, DTO> ceil(SqlExpr<T, DTO> value) {
		return function1("ceil", value, value.type);
	}

	public static <DTO, T extends Number> SqlExpr<T, DTO> exp(SqlExpr<T, DTO> value) {
		return function1("exp", value, value.type);
	}

	public static <DTO, T extends Number> SqlExpr<T, DTO> floor(SqlExpr<T, DTO> value) {
		return function1("exp", value, value.type);
	}

	/**
	 * Represent a SQL function call with a single input argument.
	 * 
	 * @param function
	 *            The function name
	 * @param arg1
	 *            the value of the input argument.
	 * @param outputType
	 *            The output type of the function.
	 */
	public static <T, U, DTO> SqlExpr<U, DTO> function1(String function, SqlExpr<T, DTO> arg1, Class<U> outputType) {
		final SqlExprFunction1<T, U, DTO> res = new SqlExprFunction1<T, U, DTO>(function, arg1, outputType);
		res.setParent(arg1.getParent());
		return res;
	}

	public static <T, DTO> SqlExprLiteral<T, DTO> lit(T value, Class<T> type) {
		return new SqlExprLiteral<T, DTO>(value, type);
	}

	public static <T, DTO> SqlExpr<T, DTO> raw(String sql, Class<T> type) {
		return new SqlExprRaw<>(sql, type);
	}

	public static <DTO, T> SqlExpr<T, DTO> NULL() {
		return new SqlExprRaw<>("NULL", null);
	}

	public static <DTO> SqlExpr<String, DTO> lit(String value) {
		return new SqlExprLiteral<>(value, String.class);
	}

	public static <DTO> SqlExpr<BigDecimal, DTO> lit(BigDecimal value) {
		return new SqlExprLiteral<>(value, BigDecimal.class);
	}

	public static <DTO> SqlExpr<Long, DTO> lit(Long value) {
		return new SqlExprLiteral<>(value, Long.class);
	}

	public static <DTO> SqlExpr<Integer, DTO> lit(Integer value) {
		return new SqlExprLiteral<>(value, Integer.class);
	}

	public static <DTO> SqlExpr<Timestamp, DTO> lit(Timestamp value) {
		return new SqlExprLiteral<>(value, Timestamp.class);
	}

	public static <DTO> SqlExpr<Date, DTO> sysdate() {
		return new SqlExprRaw<Date, DTO>("sysdate", java.sql.Date.class);
	}

	public static <DTO> SqlExpr<Timestamp, DTO> systimestamp() {
		return new SqlExprRaw<Timestamp, DTO>("systimestamp", java.sql.Timestamp.class);
	}

	@SafeVarargs
	public static <T extends Number, DTO> SqlExprRelational<T, DTO> add(SqlExpr<T, DTO>... arguments) {
		final List<SqlExpr<T, DTO>> arguments_list = Arrays.asList(arguments);
		return new SqlExprRelational<>(arguments[0].type, "+", arguments_list);
	}

	public static <T extends Number, DTO> SqlExprRelational<T, DTO> add(Collection<SqlExpr<T, DTO>> arguments) {
		return new SqlExprRelational<>(arguments.iterator().next().type, "+", arguments);
	}

	@SafeVarargs
	public static <T extends Number, DTO> SqlExprRelational<T, DTO> minus(SqlExpr<T, DTO>... arguments) {
		final List<SqlExpr<T, DTO>> arguments_list = Arrays.asList(arguments);
		return new SqlExprRelational<>(arguments[0].type, "-", arguments_list);
	}

	/**
	 * Return a condition that is true if all conditions are true.<br>
	 * In SQL, renders as: {@code ((<condition1>) AND (<condition2>) AND ...)}
	 */
	@SafeVarargs
	public static <DTO> ConditionAnd<DTO> and(Condition<DTO>... conditions) {
		return and(Arrays.asList(conditions));
	}

	/**
	 * Return a condition that is true if all conditions are true.<br>
	 * In SQL, renders as: {@code ((<condition1>) AND (<condition2>) AND ...)}
	 */
	public static <DTO> ConditionAnd<DTO> and(Collection<Condition<DTO>> conditions) {
		return new ConditionAnd<>(conditions);
	}

	@SafeVarargs
	public static <DTO> ConditionOr<DTO> or(Condition<DTO>... conditions) {
		return or(Arrays.asList(conditions));
	}

	public static <DTO> ConditionOr<DTO> or(Collection<Condition<DTO>> conditions) {
		return new ConditionOr<>(conditions);
	}

	public static <DTO> ConditionNot<DTO> not(Condition<DTO> condition) {
		return new ConditionNot<>(condition);
	}

}
