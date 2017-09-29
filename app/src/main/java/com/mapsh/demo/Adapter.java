package com.mapsh.demo;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

/**
 * Created by mapsh on 2017/9/29.
 */

public class Adapter extends BaseQuickAdapter<String, BaseViewHolder> {
    public Adapter(@Nullable List<String> data) {
        super(R.layout.item, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        helper.setText(R.id.text,item);
    }
}
