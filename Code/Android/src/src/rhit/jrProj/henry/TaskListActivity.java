package rhit.jrProj.henry;

import java.util.ArrayList;

import rhit.jrProj.henry.firebase.Milestone;
import rhit.jrProj.henry.firebase.Task;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;
import android.widget.LinearLayout;

/**
 * An activity representing a list of Items. This activity has different
 * presentations for handset and tablet-size devices. On handsets, the activity
 * presents a list of items, which when touched, lead to a
 * {@link ItemDetailActivity} representing item details. On tablets, the
 * activity presents the list of items and item details side-by-side using two
 * vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link ItemListFragment} and the item details (if present) is a
 * {@link ItemDetailFragment}.
 * <p>
 * This activity also implements the required {@link ItemListFragment.Callbacks}
 * interface to listen for item selections.
 */
public class TaskListActivity extends FragmentActivity implements
		TaskListFragment.Callbacks {

	/**
	 * Whether or not the activity is in two-pane mode, i.e. running on a tablet
	 * device.
	 */
	private boolean mTwoPane;
	private ArrayList<Task> tasks;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.tasks = this.getIntent().getParcelableArrayListExtra("Tasks");

		boolean tabletSize = (getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
		if (!tabletSize) {
			setContentView(R.layout.activity_task_list);
		} else {
			setContentView(R.layout.activity_task_twopane);
		}
		getActionBar().setDisplayHomeAsUpEnabled(true);
		if (findViewById(R.id.task_detail_container) != null) {
			this.mTwoPane = true;
			((TaskListFragment) getFragmentManager().findFragmentById(
					R.id.task_list)).setActivateOnItemClick(true);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == android.R.id.home) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Returns the list of tasks
	 * 
	 * @return
	 */
	public ArrayList<Task> getTasks() {
		return this.tasks;
	}

	/**
	 * Callback method from {@link ItemListFragment.Callbacks} indicating that
	 * the item with the given ID was selected.
	 */
	public void onItemSelected(Task t) {
		if (this.mTwoPane) {
			Bundle arguments = new Bundle();
			arguments.putParcelable("Task", t);
			TaskDetailFragment fragment = new TaskDetailFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.task_detail_container, fragment).commit();

		} else {
			Intent detailIntent = new Intent(this, TaskDetailActivity.class);
			detailIntent.putExtra("Task", t);
			startActivity(detailIntent);
		}
	}
}