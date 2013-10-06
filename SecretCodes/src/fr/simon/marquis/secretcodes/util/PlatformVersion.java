package fr.simon.marquis.secretcodes.util;

import android.os.Build;

/**
 * Utility methods to check the Android OS version.
 */
public final class PlatformVersion {

	private PlatformVersion() {
		// No public constructor
	}

	/**
	 * @return Whether the current OS version is higher or equal to Froyo (API
	 *         8+).
	 */
	public static boolean isAtLeastFroyo() {
		return checkVersion(Build.VERSION_CODES.FROYO);
	}

	/**
	 * @return Whether the current OS version is higher or equal to Gingerbread
	 *         (API 9+).
	 */
	public static boolean isAtLeastGingerbread() {
		return checkVersion(Build.VERSION_CODES.GINGERBREAD);
	}

	/**
	 * @return Whether the current OS version is higher or equal to Gingerbread
	 *         MR1 (API 10+).
	 */
	public static boolean isAtLeastGingerbreadMR1() {
		return checkVersion(Build.VERSION_CODES.GINGERBREAD_MR1);
	}

	/**
	 * @return Whether the current OS version is higher or equal to Honeycomb
	 *         (API 11+).
	 */
	public static boolean isAtLeastHoneycomb() {
		return checkVersion(Build.VERSION_CODES.HONEYCOMB);
	}

	/**
	 * @return Whether the current OS version is higher or equal to Honeycomb
	 *         MR1 (API 12+).
	 */
	public static boolean isAtLeastHoneycombMR1() {
		return checkVersion(Build.VERSION_CODES.HONEYCOMB_MR1);
	}

	/**
	 * @return Whether the current OS version is higher or equal to Honeycomb
	 *         MR2 (API 13+).
	 */
	public static boolean isAtLeastHoneycombMR2() {
		return checkVersion(Build.VERSION_CODES.HONEYCOMB_MR2);
	}

	/**
	 * @return Whether the current OS version is higher or equal to Ice Cream
	 *         Sandwich (API 14+).
	 */
	public static boolean isAtLeastIceCreamSandwich() {
		return checkVersion(Build.VERSION_CODES.ICE_CREAM_SANDWICH);
	}

	/**
	 * @return Whether the current OS version is higher or equal to Ice Cream
	 *         Sandwich MR1 (API 15+).
	 */
	public static boolean isAtLeastIceCreamSandwichMR1() {
		return checkVersion(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1);
	}

	/**
	 * @return Whether the current OS version is higher or equal to Jelly Bean
	 *         (API 16+).
	 */
	public static boolean isAtLeastJellyBean() {
		return checkVersion(Build.VERSION_CODES.JELLY_BEAN);
	}

	/**
	 * @return Whether the current OS version is higher or equal to Jelly Bean
	 *         MR1 (API 17+).
	 */
	public static boolean isAtLeastJellyBeanMR1() {
		return checkVersion(Build.VERSION_CODES.JELLY_BEAN_MR1);
	}

	/**
	 * @return Whether the current OS version is higher or equal to Jelly Bean
	 *         MR2 (API 18+).
	 */
	public static boolean isAtLeastJellyBeanMR2() {
		return checkVersion(Build.VERSION_CODES.JELLY_BEAN_MR2);
	}

	/**
	 * Check whether the current OS version is higher or equal to the given
	 * minimum SDK version
	 * 
	 * @param minSdkVersion
	 *            The minimum SDK version to test against.
	 * @return Whether the current OS version is higher or equal to the given
	 *         minimum SDK version.
	 */
	private static boolean checkVersion(int minSdkVersion) {
		return Build.VERSION.SDK_INT >= minSdkVersion;
	}
}