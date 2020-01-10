/*
 * Copyright 2017-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.vault.util;

import java.util.ArrayList;
import java.util.List;

import lombok.EqualsAndHashCode;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Value object representing Version consisting of major, minor and bugfix part.
 *
 * @author Mark Paluch
 */
@EqualsAndHashCode
public class Version implements Comparable<Version> {

	private static final String VERSION_PARSE_ERROR = "Invalid version string! Could not parse segment %s within %s.";

	private final int major;

	private final int minor;

	private final int bugfix;

	private final int build;

	/**
	 * Creates a new {@link Version} from the given integer values. At least one value has
	 * to be given but a maximum of 4.
	 *
	 * @param parts must not be {@literal null} or empty.
	 */
	private Version(int... parts) {

		Assert.notNull(parts);
		Assert.isTrue(parts.length > 0 && parts.length < 5);

		this.major = parts[0];
		this.minor = parts.length > 1 ? parts[1] : 0;
		this.bugfix = parts.length > 2 ? parts[2] : 0;
		this.build = parts.length > 3 ? parts[3] : 0;

		Assert.isTrue(major >= 0, "Major version must be greater or equal zero!");
		Assert.isTrue(minor >= 0, "Minor version must be greater or equal zero!");
		Assert.isTrue(bugfix >= 0, "Bugfix version must be greater or equal zero!");
		Assert.isTrue(build >= 0, "Build version must be greater or equal zero!");
	}

	/**
	 * Parses the given string representation of a version into a {@link Version} object.
	 *
	 * @param version must not be {@literal null} or empty.
	 * @return
	 */
	public static Version parse(String version) {

		Assert.hasText(version);

		String[] parts = version.trim().split("\\.");
		int[] intParts = new int[parts.length];

		for (int i = 0; i < parts.length; i++) {

			String input = i == parts.length - 1 ? parts[i].replaceAll("\\D.*", "")
					: parts[i];

			if (StringUtils.hasText(input)) {
				try {
					intParts[i] = Integer.parseInt(input);
				}
				catch (IllegalArgumentException o_O) {
					throw new IllegalArgumentException(String.format(VERSION_PARSE_ERROR,
							input, version), o_O);
				}
			}
		}

		return new Version(intParts);
	}

	/**
	 * Returns whether the current {@link Version} is greater (newer) than the given one.
	 *
	 * @param version
	 * @return
	 */
	public boolean isGreaterThan(Version version) {
		return compareTo(version) > 0;
	}

	/**
	 * Returns whether the current {@link Version} is greater (newer) or the same as the
	 * given one.
	 *
	 * @param version
	 * @return
	 */
	public boolean isGreaterThanOrEqualTo(Version version) {
		return compareTo(version) >= 0;
	}

	/**
	 * Returns whether the current {@link Version} is the same as the given one.
	 *
	 * @param version
	 * @return
	 */
	public boolean is(Version version) {
		return equals(version);
	}

	/**
	 * Returns whether the current {@link Version} is less (older) than the given one.
	 *
	 * @param version
	 * @return
	 */
	public boolean isLessThan(Version version) {
		return compareTo(version) < 0;
	}

	/**
	 * Returns whether the current {@link Version} is less (older) or equal to the current
	 * one.
	 *
	 * @param version
	 * @return
	 */
	public boolean isLessThanOrEqualTo(Version version) {
		return compareTo(version) <= 0;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Version that) {

		if (that == null) {
			return 1;
		}

		if (major != that.major) {
			return major - that.major;
		}

		if (minor != that.minor) {
			return minor - that.minor;
		}

		if (bugfix != that.bugfix) {
			return bugfix - that.bugfix;
		}

		if (build != that.build) {
			return build - that.build;
		}

		return 0;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		List<Integer> digits = new ArrayList<Integer>();
		digits.add(major);
		digits.add(minor);

		if (build != 0 || bugfix != 0) {
			digits.add(bugfix);
		}

		if (build != 0) {
			digits.add(build);
		}

		return StringUtils.collectionToDelimitedString(digits, ".");
	}
}
