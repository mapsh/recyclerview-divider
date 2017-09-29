package com.mapsh.recyclerview.divider;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Class that draws a divider between RecyclerView's elements
 */
public class RecyclerViewDivider extends RecyclerView.ItemDecoration {
    private static final String TAG = "RecyclerViewDivider";

    private static final int TYPE_SPACE = -1;
    private static final int TYPE_COLOR = 0;
    private static final int TYPE_DRAWABLE = 1;

    private final
    @Type
    int mType;
    private final VisibilityProvider mVisibilityFactory;
    private final DrawableProvider mDrawableFactory;
    private final TintProvider mTintFactory;
    private final SizeProvider mSizeFactory;
    private final MarginProvider mMarginFactory;

    /**
     * Set the {@link Builder} for this {@link RecyclerViewDivider}
     *
     * @param type              divider's type (one of {@link Type})
     * @param visibilityFactory instance of {@link VisibilityProvider} taken from {@link Builder}
     * @param drawableFactory   instance of {@link DrawableProvider} taken from {@link Builder}
     * @param tintFactory       instance of {@link TintProvider} taken from {@link Builder}
     * @param sizeFactory       instance of {@link SizeProvider} taken from {@link Builder}
     * @param marginFactory     instance of {@link MarginProvider} taken from {@link Builder}
     */
    private RecyclerViewDivider(@Type int type,
                                @NonNull VisibilityProvider visibilityFactory,
                                @NonNull DrawableProvider drawableFactory,
                                @Nullable TintProvider tintFactory,
                                @NonNull SizeProvider sizeFactory,
                                @NonNull MarginProvider marginFactory) {

        mType = type;
        mVisibilityFactory = visibilityFactory;
        mDrawableFactory = drawableFactory;
        mTintFactory = tintFactory;
        mSizeFactory = sizeFactory;
        mMarginFactory = marginFactory;
    }

    /**
     * Creates a new {@link Builder} for the current context
     *
     * @param context current context
     * @return a new {@link Builder} instance
     */
    public static Builder with(@NonNull Context context) {
        return new Builder(context);
    }

    /**
     * Add this divider to a RecyclerView
     *
     * @param recyclerView RecyclerView at which the divider will be added
     */
    public void addTo(@NonNull RecyclerView recyclerView) {
        removeFrom(recyclerView);
        recyclerView.addItemDecoration(this);
    }

    /**
     * Remove this divider from a RecyclerView
     *
     * @param recyclerView RecyclerView from which the divider will be removed
     */
    public void removeFrom(@NonNull RecyclerView recyclerView) {
        recyclerView.removeItemDecoration(this);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        final RecyclerView.Adapter adapter = parent.getAdapter();
        final int listSize;

        // if the divider isn't a simple space, it will be drawn
        if (mType == TYPE_SPACE || adapter == null || (listSize = adapter.getItemCount()) == 0)
            return;

        int left;
        int top;
        int right;
        int bottom;

        final int orientation = RecyclerViewDividerUtils.getOrientation(parent);
        final int spanCount = RecyclerViewDividerUtils.getSpanCount(parent);
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            int itemPosition = parent.getChildAdapterPosition(child);
            final int groupIndex = RecyclerViewDividerUtils.getGroupIndex(parent, itemPosition);
            final int groupCount = RecyclerViewDividerUtils.getGroupCount(parent, listSize);

            Drawable divider = mDrawableFactory.drawableForItem(groupCount, groupIndex);
            @VisibilityProvider.Show
            int showDivider = mVisibilityFactory.displayDividerForItem(groupCount, groupIndex);

            if (divider == null || showDivider == VisibilityProvider.SHOW_NONE) continue;

            final int spanSize = RecyclerViewDividerUtils.getSpanSize(parent, itemPosition);

            int lineAccumulatedSpan = RecyclerViewDividerUtils.getAccumulatedSpanInLine(parent, spanSize, itemPosition, groupIndex);

            final int margin = mMarginFactory.marginSizeForItem(groupCount, groupIndex);
            int size = mSizeFactory.sizeForItem(divider, orientation, groupCount, groupIndex);
            if (mTintFactory != null) {
                final int tint = mTintFactory.tintForItem(groupCount, groupIndex);
                Drawable wrappedDrawable = DrawableCompat.wrap(divider);
                DrawableCompat.setTint(wrappedDrawable, tint);
                divider = wrappedDrawable;
            }

            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            int halfSize = size < 2 ? size : size / 2;

            size = showDivider == VisibilityProvider.SHOW_ITEMS_ONLY ? 0 : size;
            halfSize = showDivider == VisibilityProvider.SHOW_GROUP_ONLY ? 0 : halfSize;

            final int childBottom = child.getBottom();
            final int childTop = child.getTop();
            final int childRight = child.getRight();
            final int childLeft = child.getLeft();

            // if the last element in the span doesn't complete the span count, its size will be full, not the half
            // halfSize * 2 is used instead of size to handle the case Show.ITEMS_ONLY in which size will be == 0
            final int lastElementInSpanSize = itemPosition == listSize - 1 ? halfSize * 2 : halfSize;

            final boolean useCellMargin = margin == 0;

            int marginToAddBefore, marginToAddAfter;
            marginToAddBefore = marginToAddAfter = 0;

            if (orientation == RecyclerView.VERTICAL) {
                if (spanCount > 1 && spanSize < spanCount) {
                    top = childTop + margin;
                    // size is added to draw filling point between horizontal and vertical dividers
                    bottom = childBottom - margin;

                    if (useCellMargin) {
                        if (groupIndex > 0) {
                            top -= params.topMargin;
                        }
                        if (groupIndex < groupCount - 1 || size > 0) {
                            bottom += params.bottomMargin;
                        }
                        bottom += size;
                    }

                    if (lineAccumulatedSpan == spanSize) {
                        // first element in the group
                        left = childRight + margin + params.rightMargin;
                        right = left + lastElementInSpanSize;

                        setBoundsAndDraw(divider, c, left, top, right, bottom);

                        if (useCellMargin) {
                            marginToAddAfter = params.rightMargin;
                        }
                    } else if (lineAccumulatedSpan == spanCount) {
                        // last element in the group
                        right = childLeft - margin - params.leftMargin;
                        left = right - halfSize;

                        setBoundsAndDraw(divider, c, left, top, right, bottom);

                        if (useCellMargin) {
                            marginToAddBefore = params.leftMargin;
                        }
                    } else {
                        // element in the middle
                        // left half divider
                        right = childLeft - margin - params.leftMargin;
                        left = right - halfSize;

                        setBoundsAndDraw(divider, c, left, top, right, bottom);

                        // right half divider
                        left = childRight + margin + params.rightMargin;
                        right = left + lastElementInSpanSize;

                        setBoundsAndDraw(divider, c, left, top, right, bottom);

                        if (useCellMargin) {
                            marginToAddAfter = params.rightMargin;
                            marginToAddBefore = params.leftMargin;
                        }
                    }
                }

                // draw bottom divider
                top = childBottom + params.bottomMargin;
                bottom = top + size;
                left = childLeft + margin - marginToAddBefore;
                right = childRight - margin + marginToAddAfter;

                setBoundsAndDraw(divider, c, left, top, right, bottom);

            } else {
                if (spanCount > 1 && spanSize < spanCount) {
                    left = childLeft + margin;
                    // size is added to draw filling point between horizontal and vertical dividers
                    right = childRight - margin;
                    if (useCellMargin) {
                        if (groupIndex > 0) {
                            left -= params.leftMargin;
                        }
                        if (groupIndex < groupCount - 1 || size > 0) {
                            right += params.rightMargin;
                        }
                        right += size;
                    }

                    if (lineAccumulatedSpan == spanSize) {
                        // first element in the group
                        top = childBottom + margin + params.bottomMargin;
                        bottom = top + lastElementInSpanSize;

                        setBoundsAndDraw(divider, c, left, top, right, bottom);

                        if (useCellMargin) {
                            marginToAddAfter = params.bottomMargin;
                        }
                    } else if (lineAccumulatedSpan == spanCount) {
                        // last element in the group
                        bottom = childTop - margin - params.topMargin;
                        top = bottom - halfSize;

                        setBoundsAndDraw(divider, c, left, top, right, bottom);

                        if (useCellMargin) {
                            marginToAddBefore = params.topMargin;
                        }
                    } else {
                        // element in the middle
                        // top half divider
                        bottom = childTop - margin - params.topMargin;
                        top = bottom - halfSize;

                        divider.setBounds(left, top, right, bottom);
                        divider.draw(c);

                        // bottom half divider
                        top = childBottom + margin + params.bottomMargin;
                        bottom = top + lastElementInSpanSize;

                        setBoundsAndDraw(divider, c, left, top, right, bottom);

                        if (useCellMargin) {
                            marginToAddAfter = params.bottomMargin;
                            marginToAddBefore = params.topMargin;
                        }
                    }
                }

                // draw right divider
                bottom = childBottom - margin + marginToAddAfter;
                top = childTop + margin - marginToAddBefore;
                left = childRight + params.rightMargin;
                right = left + size;

                setBoundsAndDraw(divider, c, left, top, right, bottom);
            }
        }
    }

    /**
     * Set the Drawable's bounds and draw it on a Canvas
     *
     * @param drawable Drawable to draw
     * @param canvas   Canvas used to show the Drawable
     * @param left     left position in px
     * @param top      top position in px
     * @param right    right position in px
     * @param bottom   bottom position in px
     */
    private void setBoundsAndDraw(@NonNull Drawable drawable, @NonNull Canvas canvas, int left, int top, int right, int bottom) {
        drawable.setBounds(left, top, right, bottom);
        drawable.draw(canvas);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        final int listSize = parent.getAdapter().getItemCount();
        if (listSize <= 0)
            return;

        int itemPosition = parent.getChildAdapterPosition(view);
        final int groupIndex = RecyclerViewDividerUtils.getGroupIndex(parent, itemPosition);
        final int groupCount = RecyclerViewDividerUtils.getGroupCount(parent, listSize);

        @VisibilityProvider.Show int showDivider = mVisibilityFactory.displayDividerForItem(groupCount, groupIndex);
        if (showDivider == VisibilityProvider.SHOW_NONE)
            return;

        final int orientation = RecyclerViewDividerUtils.getOrientation(parent);
        final int spanCount = RecyclerViewDividerUtils.getSpanCount(parent);
        final int spanSize = RecyclerViewDividerUtils.getSpanSize(parent, itemPosition);

        int lineAccumulatedSpan = RecyclerViewDividerUtils.getAccumulatedSpanInLine(parent, spanSize, itemPosition, groupIndex);

        final Drawable divider = mDrawableFactory.drawableForItem(groupCount, groupIndex);
        int size = mSizeFactory.sizeForItem(divider, orientation, groupCount, groupIndex);
        int marginSize = mMarginFactory.marginSizeForItem(groupCount, groupIndex);

        int halfSize = size / 2 + marginSize;

        size = showDivider == VisibilityProvider.SHOW_ITEMS_ONLY ? 0 : size;
        halfSize = showDivider == VisibilityProvider.SHOW_GROUP_ONLY ? 0 : halfSize;

        if (orientation == RecyclerView.VERTICAL) {
            if (spanCount == 1 || spanSize == spanCount) {
                // LinearLayoutManager or GridLayoutManager with 1 column
                outRect.set(0, 0, 0, size);
            } else if (lineAccumulatedSpan == spanSize) {
                // first element in the group
                outRect.set(0, 0, halfSize, size);
            } else if (lineAccumulatedSpan == spanCount) {
                // last element in the group
                outRect.set(halfSize, 0, 0, size);
            } else {
                // element in the middle
                outRect.set(halfSize, 0, halfSize, size);
            }
        } else {
            if (spanCount == 1 || spanSize == spanCount) {
                // LinearLayoutManager or GridLayoutManager with 1 row
                outRect.set(0, 0, size, 0);
            } else if (lineAccumulatedSpan == spanSize) {
                // first element in the group
                outRect.set(0, 0, size, halfSize);
            } else if (lineAccumulatedSpan == spanCount) {
                // last element in the group
                outRect.set(0, halfSize, size, 0);
            } else {
                // element in the middle
                outRect.set(0, halfSize, size, halfSize);
            }
        }
    }

    /**
     * {@link Builder} class for {@link RecyclerViewDivider}.
     * <br>
     * This class can set these custom properties:
     * <ul>
     * <li><b>Color:</b> {@link #color(int)}</li>
     * <li><b>Drawable:</b> {@link #drawable(Drawable)}</li>
     * <li><b>Tint of the drawable:</b> {@link #tint(int)}</li>
     * <li><b>Size:</b> {@link #size(int)}</li>
     * <li><b>Margins:</b> {@link #marginSize(int)}</li>
     * </ul>
     * <br>
     * And use these custom factories:
     * <ul>
     * <li><b>{@link VisibilityProvider}:</b> {@link #visibilityFactory(VisibilityProvider)}</li>
     * <li><b>{@link DrawableProvider}:</b> {@link #drawableFactory(DrawableProvider)}</li>
     * <li><b>{@link TintProvider}:</b> {@link #tintFactory(TintProvider)}</li>
     * <li><b>{@link SizeProvider}:</b> {@link #sizeFactory(SizeProvider)}</li>
     * <li><b>{@link MarginProvider}:</b> {@link #marginFactory(MarginProvider)}</li>
     * </ul>
     */
    public static class Builder {
        private static final int INT_DEF = -1;

        private final Context context;

        @ColorInt
        private Integer color;
        private Drawable drawable;
        private Integer tint;
        private int size;
        private int marginSize;
        private boolean hideLastDivider;

        private VisibilityProvider visibilityFactory;
        private DrawableProvider drawableFactory;
        private TintProvider tintFactory;
        private SizeProvider sizeFactory;
        private MarginProvider marginFactory;

        @Type
        private int type;

        /**
         * Initialize this {@link Builder} with a context.
         * The Context object will be stored in a WeakReference to avoid memory leak
         *
         * @param context current context
         */
        @SuppressWarnings("WeakerAccess")
        public Builder(@NonNull Context context) {
            this.context = context;
            size = INT_DEF;
            marginSize = INT_DEF;
            type = TYPE_COLOR;
        }

        /**
         * Set the type of the divider as a space
         *
         * @return {@link Builder} instance
         */
        public Builder asSpace() {
            type = TYPE_SPACE;
            return this;
        }

        /**
         * Set the color of all dividers. This method can't be used with {@link #drawable(Drawable)} or {@link #tint(int)}
         * <br>
         * To set a custom color for each divider use {@link #drawableFactory(DrawableProvider)} instead
         *
         * @param color resolved color for this divider, not a resource
         * @return {@link Builder} instance
         */
        public Builder color(@ColorInt int color) {
            this.color = color;
            type = TYPE_COLOR;
            return this;
        }

        /**
         * Set the drawable of all dividers. This method can't be used with {@link #color(int)}.
         * If you want to color the drawable, you have to use {@link #tint(int)} instead.
         * <br>
         * To set a custom drawable for each divider use {@link #drawableFactory(DrawableProvider)} instead.
         * <br>
         * Warning: if the span count is major than one and the drawable can't be mirrored, the drawable will not be shown correctly.
         *
         * @param drawable custom drawable for this divider
         * @return {@link Builder} instance
         */
        public Builder drawable(@NonNull Drawable drawable) {
            this.drawable = drawable;
            type = TYPE_DRAWABLE;
            return this;
        }

        /**
         * Set the tint color of all dividers' drawables.
         * If you want to create a plain divider with a single color, {@link #color(int)} is recommended.
         * <br>
         * To set a custom tint color for each divider's drawable use {@link #tintFactory(TintProvider)} instead
         *
         * @param color color that will be used as drawable's tint
         * @return {@link Builder} instance
         */
        public Builder tint(@ColorInt int color) {
            tint = color;
            return this;
        }

        /**
         * Set the size of all dividers. The divider's final size will depend on RecyclerView's orientation:
         * <ul>
         * <li><b>RecyclerView.VERTICAL:</b> the height will be equal to the size and the width will be equal to the sum of container's width and the margin size</li>
         * <li><b>RecyclerView.HORIZONTAL:</b> the width will be equal to the size and the height will be equal to the sum of container's height and the margin size</li>
         * </ul>
         * <br>
         * To set a custom size for each divider use {@link #sizeFactory(SizeProvider)} instead.
         *
         * @param size size in pixels for this divider
         * @return {@link Builder} instance
         */
        public Builder size(int size) {
            this.size = size;
            return this;
        }

        /**
         * Set the margin size for all dividers. They will depend on RecyclerView's orientation:
         * <ul>
         * <li><b>RecyclerView.VERTICAL:</b> margins will be added equally to the left and to the right</li>
         * <li><b>RecyclerView.HORIZONTAL:</b> margins will be added equally to the top and to the bottom</li>
         * </ul>
         * <br>
         * To set a custom margin size for each divider use {@link #sizeFactory(SizeProvider)} instead.
         *
         * @param marginSize margins' size in pixels for this divider
         * @return {@link Builder} instance
         */
        public Builder marginSize(int marginSize) {
            this.marginSize = marginSize;
            return this;
        }

        /**
         * Hide the divider after the last group of items.
         * <br>
         * Warning: when the spanCount is major than 1 (e.g. LinearLayoutManager), only the divider after the last group will be hidden, the items' dividers, instead, will be shown.
         * <br>
         * If you want to specify a more flexible behaviour, use {@link #visibilityFactory(VisibilityProvider)} instead.
         *
         * @return {@link Builder} instance
         */
        public Builder hideLastDivider() {
            this.hideLastDivider = true;
            return this;
        }

        /**
         * Set the divider's custom {@link VisibilityProvider}
         * <br>
         * If you want to hide only the last divider use {@link #hideLastDivider()} instead.
         *
         * @param visibilityFactory custom {@link VisibilityProvider} to set
         * @return {@link Builder} instance
         */
        public Builder visibilityFactory(@Nullable VisibilityProvider visibilityFactory) {
            this.visibilityFactory = visibilityFactory;
            return this;
        }

        /**
         * Set the divider's custom {@link DrawableProvider}
         * <br>
         * Warning: if the span count is major than one and the drawable can't be mirrored, the drawable will not be shown correctly.
         *
         * @param drawableFactory custom {@link DrawableProvider} to set
         * @return {@link Builder} instance
         */
        public Builder drawableFactory(@Nullable DrawableProvider drawableFactory) {
            this.drawableFactory = drawableFactory;
            return this;
        }

        /**
         * Set the divider's custom {@link TintProvider}
         *
         * @param tintFactory custom {@link TintProvider} to set
         * @return {@link Builder} instance
         */
        public Builder tintFactory(@Nullable TintProvider tintFactory) {
            this.tintFactory = tintFactory;
            return this;
        }

        /**
         * Set the divider's custom {@link SizeProvider}
         *
         * @param sizeFactory custom {@link SizeProvider} to set
         * @return {@link Builder} instance
         */
        public Builder sizeFactory(@Nullable SizeProvider sizeFactory) {
            this.sizeFactory = sizeFactory;
            return this;
        }


        /**
         * Set the divider's custom {@link MarginProvider}
         *
         * @param marginFactory custom {@link MarginProvider} to set
         * @return {@link Builder} instance
         */
        public Builder marginFactory(@Nullable MarginProvider marginFactory) {
            this.marginFactory = marginFactory;
            return this;
        }

        /**
         * Creates a new {@link RecyclerViewDivider} with given configurations and initializes all values.
         * There are three common cases in the choice of factories:
         * <ul>
         * <li><b>Property not set</b>: the default factory will be used</li>
         * <li><b>Property set for all divider</b>: the general factory will be used</li>
         * <li><b>Property set differently for each divider</b>: the custom factory will be used</li>
         * </ul>
         *
         * @return a new {@link RecyclerViewDivider} with these {@link Builder} configurations
         */
        @SuppressLint("SwitchIntDef")
        public RecyclerViewDivider build() {
            Log.d(TAG, "building the divider");

            /* -------------------- VISIBILITY FACTORY -------------------- */

            if (visibilityFactory == null) {
                if (hideLastDivider) {
                    visibilityFactory = VisibilityProvider.getLastItemInvisibleFactory();
                } else {
                    visibilityFactory = VisibilityProvider.getDefault();
                }
            }

            /* -------------------- SIZE FACTORY -------------------- */

            if (sizeFactory == null) {
                if (size == INT_DEF) {
                    sizeFactory = SizeProvider.getDefault(context);
                } else {
                    sizeFactory = SizeProvider.getGeneralFactory(size);
                }
            }

            /* -------------------- DRAWABLE FACTORY -------------------- */

            if (drawableFactory == null) {
                Drawable currDrawable = null;
                // all drawing properties will be set if RecyclerViewDivider is used as a divider, not as a space
                switch (type) {
                    case TYPE_COLOR:
                        if (color != null) {
                            currDrawable = RecyclerViewDividerUtils.colorToDrawable(color);
                        }
                        break;

                    case TYPE_DRAWABLE:
                        if (drawable != null) {
                            Log.d(TAG, "if your span count is major than 1 and the drawable can't be mirrored, it won't be shown correctly");
                            currDrawable = drawable;
                        }
                        break;
                }
                if (currDrawable == null) {
                    drawableFactory = DrawableProvider.getDefault(context);
                } else {
                    drawableFactory = DrawableProvider.getGeneralFactory(currDrawable);
                }
            }

            /* -------------------- TINT FACTORY -------------------- */

            if (tintFactory == null) {
                if (tint != null) {
                    tintFactory = TintProvider.getGeneralFactory(tint);
                }
            }

            /* -------------------- MARGIN FACTORY -------------------- */

            if (marginFactory == null) {
                if (marginSize == INT_DEF) {
                    marginFactory = MarginProvider.getDefault(context);
                } else {
                    marginFactory = MarginProvider.getGeneralFactory(marginSize);
                }
            }

            // creates divider for this mBuilder
            return new RecyclerViewDivider(type, visibilityFactory, drawableFactory, tintFactory, sizeFactory, marginFactory);
        }
    }

    /**
     * Source annotation used to define different dividers' types.
     * <ul>
     * <li><b>TYPE_SPACE</b>: divider used only as a space</li>
     * <li><b>TYPE_COLOR</b>: plain divider with one color</li>
     * <li><b>TYPE_DRAWABLE</b>: divider with a drawable resource</li>
     * </ul>
     */
    @IntDef({TYPE_SPACE, TYPE_COLOR, TYPE_DRAWABLE})
    @Retention(RetentionPolicy.SOURCE)
    private @interface Type {
        // empty annotation body
    }
}