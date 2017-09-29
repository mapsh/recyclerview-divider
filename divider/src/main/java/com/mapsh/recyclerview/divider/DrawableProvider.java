package com.mapsh.recyclerview.divider;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;


/**
 * Factory used to specify a custom logic to use different drawables as divider.
 * <br>
 * You can add a custom {@link DrawableProvider} in your {@link RecyclerViewDivider.Builder} using
 * {@link RecyclerViewDivider.Builder#drawableFactory(DrawableProvider)} method
 */
public abstract class DrawableProvider {

    private static DrawableProvider defaultFactory;

    /**
     * Creates a singleton instance of a default {@link DrawableProvider} to avoid multiple instance of the same class
     *
     * @param context current context
     * @return factory with default values
     */
    public static synchronized DrawableProvider getDefault(@NonNull Context context) {
        if (defaultFactory == null) {
            defaultFactory = new Default(context);
        }
        return defaultFactory;
    }

    /**
     * Creates a new {@link DrawableProvider} with equal drawable resource for all dividers
     *
     * @param drawable resource for all dividers
     * @return factory with same values for each divider
     */
    public static DrawableProvider getGeneralFactory(@NonNull Drawable drawable) {
        return new General(drawable);
    }

    /**
     * Defines a custom Drawable for each group of divider
     *
     * @param groupCount number of groups in a list.
     *                   The groupCount value is equal to the list size when the span count is 1 (e.g. LinearLayoutManager).
     * @param groupIndex position of the group. The value is between 0 and groupCount - 1.
     *                   The groupIndex is equal to the item position when the span count is 1 (e.g. LinearLayoutManager).
     * @return Drawable resource for the divider int the current position
     */
    public abstract Drawable drawableForItem(int groupCount, int groupIndex);

    /**
     * Default instance of a {@link DrawableProvider}
     */
    private static class Default extends DrawableProvider {
        private final Drawable defaultDrawable;

        Default(@NonNull Context context) {
            defaultDrawable = RecyclerViewDividerUtils.colorToDrawable(ContextCompat.getColor(context, R.color.recyclerview_divider_color));
        }

        @Override
        public Drawable drawableForItem(int groupCount, int groupIndex) {
            return defaultDrawable;
        }
    }

    /**
     * General instance of a {@link DrawableProvider} used when the drawable is set with {@link RecyclerViewDivider.Builder#color(int)}
     * or with {@link RecyclerViewDivider.Builder#drawable(Drawable)}
     */
    private static class General extends DrawableProvider {
        private final Drawable drawable;

        General(@NonNull Drawable drawable) {
            this.drawable = drawable;
        }

        @Override
        public Drawable drawableForItem(int groupCount, int groupIndex) {
            return drawable;
        }
    }
}