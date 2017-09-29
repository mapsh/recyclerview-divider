package com.mapsh.recyclerview.divider;

import android.content.Context;
import android.support.annotation.NonNull;

/**
 * Factory used to specify a custom logic to set different margins to the divider.
 * <br>
 * Custom margins will be set equally right/left with horizontal divider and top/bottom with vertical divider.
 * <br>
 * You can add a custom {@link MarginFactory} in your {@link RecyclerViewDivider.Builder} using
 * {@link RecyclerViewDivider.Builder#marginFactory(MarginFactory)} method
 */
public abstract class MarginFactory {

    private static MarginFactory defaultFactory;

    /**
     * Creates a singleton instance of a default {@link MarginFactory} to avoid multiple instance of the same class
     *
     * @param context current context
     * @return factory with default values
     */
    public static synchronized MarginFactory getDefault(@NonNull Context context) {
        if (defaultFactory == null) {
            defaultFactory = new Default(context);
        }
        return defaultFactory;
    }

    /**
     * Creates a new {@link MarginFactory} with equal margin size for all dividers
     *
     * @param marginSize margins' size of the dividers
     * @return factory with same values for each divider
     */
    public static MarginFactory getGeneralFactory(int marginSize) {
        return new General(marginSize);
    }

    /**
     * Defines a custom margin size for each group of divider
     *
     * @param groupCount number of groups in a list.
     *                   The groupCount value is equal to the list size when the span count is 1 (e.g. LinearLayoutManager).
     * @param groupIndex position of the group. The value is between 0 and groupCount - 1.
     *                   The groupIndex is equal to the item position when the span count is 1 (e.g. LinearLayoutManager).
     * @return right/left margin with horizontal divider or top/bottom margin with vertical divider
     */
    public abstract int marginSizeForItem(int groupCount, int groupIndex);

    /**
     * Default instance of a {@link MarginFactory}
     */
    private static class Default extends MarginFactory {
        private final int defaultMarginSize;

        Default(Context context) {
            defaultMarginSize = context.getResources().getDimensionPixelSize(R.dimen.recycler_view_divider_margin_size);
        }

        @Override
        public int marginSizeForItem(int groupCount, int groupIndex) {
            return defaultMarginSize;
        }
    }

    /**
     * General instance of a {@link MarginFactory} used when the margin's size is set with {@link RecyclerViewDivider.Builder#marginSize(int)}
     */
    private static class General extends MarginFactory {
        private final int marginSize;

        General(int marginSize) {
            this.marginSize = marginSize;
        }

        @Override
        public int marginSizeForItem(int groupCount, int groupIndex) {
            return marginSize;
        }
    }
}