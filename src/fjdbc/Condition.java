package fjdbc;

import java.util.Arrays;
import java.util.Collection;

import fjdbc.Dao.ConditionAnd;
import fjdbc.Dao.ConditionOr;

/**
 * @param <DTO>
 *            The DTO class associated with this condition.
 */
public abstract class Condition<DTO> extends SqlFragment {
	@SafeVarargs
	public static <DTO> ConditionAnd<DTO> and(Condition<DTO>... conditions) {
		return and(Arrays.asList(conditions));
	}

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

	public ConditionAnd<DTO> and(Condition<DTO> other) {
		return new ConditionAnd<>(Arrays.asList(this, other));
	}

	public ConditionOr<DTO> or(Condition<DTO> other) {
		return new ConditionOr<>(Arrays.asList(this, other));
	}

}