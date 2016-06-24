package com.github.richie.test_tool;

import java.util.Collection;

public class CompositeTestCase<TC extends TestCase> implements TestCase {
	private final Collection<TC> children;

	public CompositeTestCase(Collection<TC> children) {
		this.children = children;
	}

	@Override
	public void test() throws Exception {
		for (final TestCase tc : children) {
			tc.test();
		}
	}
}
