package com.imooc.multychoice;

import java.util.ArrayList;
import java.util.Iterator;

import android.app.Activity;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.haarman.listviewanimations.itemmanipulation.AnimateDismissAdapter;
import com.haarman.listviewanimations.itemmanipulation.OnDismissCallback;
import com.imooc.multychoice.R;

public class MainActivity extends Activity {

	protected static final String TAG = "TAG";
	private ListView mListView;
	private MultyAdapter mAdapter;
	// 是否处于ActionMode模式
	private boolean isInActionMode;
	private boolean isInDeleteMode = false;
	private AnimateDismissAdapter<Model> mAnimateDismissAdapter;
	private ArrayList<Integer> mCheckedPositions;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mListView = (ListView) findViewById(R.id.lv);
		mAdapter = new MultyAdapter();
		mCheckedPositions = new ArrayList<Integer>();

		mAnimateDismissAdapter = new AnimateDismissAdapter<MainActivity.Model>(
				mAdapter, new MyDismissCallBack());
		mAnimateDismissAdapter.setAbsListView(mListView);
		mListView.setAdapter(mAnimateDismissAdapter);

		mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);

		mListView.setMultiChoiceModeListener(new MultiChoiceModeListener() {

			@Override
			public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
				return false;
			}

			@Override
			public void onDestroyActionMode(ActionMode mode) {

				// 在退出ActionMode的时候调用，如果处于删除状态，就删除选中的数据，
				// 否则，重置所有选中的状态
				if (!isInDeleteMode) {
					for (Model model : mAdapter.models) {
						model.setChecked(false);
					}
					mCheckedPositions.clear();
				}

				isInActionMode = false;

			}

			@Override
			public boolean onCreateActionMode(ActionMode mode, Menu menu) {
				// 在进入ActionMode的时候调用
				MenuInflater inflater = mode.getMenuInflater();
				inflater.inflate(R.menu.menu_delete, menu);
				mode.setTitle("Delete");
				isInActionMode = true;
				isInDeleteMode = false;

				return true;
			}

			@Override
			public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

				// 当listview中的item被点击的时候调用
				if (item.getItemId() == R.id.action_delete) {
					mAnimateDismissAdapter.animateDismiss(mCheckedPositions);
					isInDeleteMode = true;
					mode.finish();
					return true;
				}

				return false;
			}

			@Override
			public void onItemCheckedStateChanged(ActionMode mode,
					int position, long id, boolean checked) {

				// 当item的选中状态被选中的时候调用
				mAdapter.models.get(position).setChecked(checked);
				mAdapter.notifyDataSetChanged();
				mode.setSubtitle(mListView.getCheckedItemCount()
						+ " item selected");

				if (mCheckedPositions.contains(position) && !checked) {
					mCheckedPositions.remove(Integer.valueOf(position));
				} else {
					mCheckedPositions.add(position);
				}

			}
		});

	}

	private class MultyAdapter extends BaseAdapter {

		private ArrayList<Model> models;

		public MultyAdapter() {

			models = new ArrayList<Model>();
			for (int i = 0; i < 20; i++) {
				models.add(new Model("I'm " + i));
			}

		}

		@Override
		public int getCount() {
			return models.size();
		}

		@Override
		public Model getItem(int position) {
			return models.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder viewHolder;

			Model model = mAdapter.models.get(position);

			if (convertView == null) {

				convertView = getLayoutInflater().inflate(
						R.layout.item_multy_choice, parent, false);

				viewHolder = new ViewHolder();

				viewHolder.tv = (TextView) convertView.findViewById(R.id.tv);
				viewHolder.chb = (CheckBox) convertView.findViewById(R.id.chb);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			viewHolder.tv.setText(model.getTitle());
			viewHolder.chb.setChecked(model.isChecked());
			viewHolder.chb.setVisibility(isInActionMode ? View.VISIBLE
					: View.GONE);

			return convertView;
		}
	}

	private static class ViewHolder {
		TextView tv;
		CheckBox chb;
	}

	/**
	 * 测试Model
	 * 
	 * @author zhaokaiqiang
	 * 
	 */
	private class Model {

		private String title;

		private boolean isChecked;

		public Model(String title) {
			this.title = title;
			isChecked = false;
		}

		public String getTitle() {
			return title;
		}

		public boolean isChecked() {
			return isChecked;
		}

		public void setChecked(boolean isChecked) {
			this.isChecked = isChecked;
		}

	}

	private class MyDismissCallBack implements OnDismissCallback {

		@Override
		public void onDismiss(AbsListView arg0, int[] arg1) {

			mCheckedPositions.clear();

			Iterator<Model> iterator = mAdapter.models.iterator();
			while (iterator.hasNext()) {
				if (iterator.next().isChecked()) {
					// 删除选中的元素
					iterator.remove();
				}
			}
			mAdapter.notifyDataSetChanged();
		}
	}

}
