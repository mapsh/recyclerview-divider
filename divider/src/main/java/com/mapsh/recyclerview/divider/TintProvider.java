package com.mapsh.recyclerview.divider;

import android.support.annotation.ColorInt;

/**
 * Factory used to specify a custom logic to use different tint colors to tint divider's drawables.
 * <br>
 * You can add a custom {@link TintProvider} in your {@link RecyclerViewDivider.Builder} using
 * {@link RecyclerViewDivider.Builder#tintFactory(TintProvider)} method
 */
public abstract class TintProvider {

    /**
     * Creates a new {@link TintProvider} with equal tint color for all dividers's drawables
     *
     * @param tint tint color for dividers' drawables
     * @return factory with same values for each divider
     */
    public static TintProvider getGeneralFactory(@ColorInt int tint) {
        return new General(tint);
    }

    /**
     * Defines a custom tint color for each group of divider
     *
     * @param groupCount number of groups in a list.
     *                   The groupCount value is equal to the list size when the span count is 1 (e.g. LinearLayoutManager).
     * @param groupIndex position of the group. The value is between 0 and groupCount - 1.
     *                   The groupIndex is equal to the item position when the span count is 1 (e.g. LinearLayoutManager).
     * @return tint color for the divider's drawable in the current position
     */
    public abstract int tintForItem(int groupCount, int groupIndex);

    /**
     * General instance of a {@link TintProvider} used when the tint color is set with {@link RecyclerViewDivider.Builder#tint(int)}
     */
    private static class General extends TintProvider {
        @ColorInt
        private final int tint;

        General(@ColorInt int tint) {
            this.tint = tint;
        }

        @Override
        public int tintForItem(int groupCount, int groupIndex) {
            return tint;
        }
    }
}