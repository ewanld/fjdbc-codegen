package com.github.richie;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import com.github.fjdbc.util.IntSequence;

/**
 * @param <DTO>
 *            The DTO class associated with this condition.
 */
public abstract class Condition<DTO> extends SqlFragment {
	public ConditionAnd<DTO> and(Condition<DTO> other) {
		return new ConditionAnd<>(Arrays.asList(this, other));
	}

	public ConditionOr<DTO> or(Condition<DTO> other) {
		return new ConditionOr<>(Arrays.asList(this, other));
	}

	/**
	 * Return this condition, inverted.<br>
	 * In SQL, renders as: {@code NOT(<this_condition>) }
	 */
	public ConditionNot<DTO> invertCondition() {
		return new ConditionNot<>(this);
	}

	public static abstract class ConditionComposite<DTO> extends Condition<DTO> {
		protected final Collection<? extends Condition<DTO>> conditions;

		public ConditionComposite(Collection<Condition<DTO>> conditions) {
			this.conditions = conditions;
		}

		@Override
		public void bind(PreparedStatement st, IntSequence parameterIndex) throws SQLException {
			for (final Condition<DTO> c : conditions) {
				c.bind(st, parameterIndex);
			}
		}
	}

	public static class ConditionAnd<DTO> extends ConditionComposite<DTO> {
		public ConditionAnd(Collection<Condition<DTO>> conditions) {
			super(conditions);
		}

		@Override
		public String toSql() {
			final String criteria_sql = conditions.stream().map(SqlFragment::toSql)
					.collect(Collectors.joining(") and ("));
			final String res = "(" + criteria_sql + ")";
			return res;
		}
	}

	public static class ConditionOr<DTO> extends ConditionComposite<DTO> {

		public ConditionOr(Collection<Condition<DTO>> conditions) {
			super(conditions);
		}

		@Override
		public String toSql() {
			final String criteria_sql = conditions.stream().map(SqlFragment::toSql)
					.collect(Collectors.joining(") or ("));
			final String res = "(" + criteria_sql + ")";
			return res;
		}
	}

	public static class ConditionNot<DTO> extends Condition<DTO> {
		private final Condition<DTO> inner;

		public ConditionNot(Condition<DTO> inner) {
			this.inner = inner;
		}

		@Override
		public String toSql() {
			return String.format("NOT (%s)", inner.toSql());
		}

		@Override
		public void bind(PreparedStatement st, IntSequence parameterIndex) throws SQLException {
			inner.bind(st, parameterIndex);
		}
	}
}