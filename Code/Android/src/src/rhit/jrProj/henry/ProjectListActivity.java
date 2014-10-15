package rhit.jrProj.henry;

import java.util.ArrayList;

import rhit.jrProj.henry.firebase.Milestone;
import rhit.jrProj.henry.firebase.Project;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

/**
 * An activity representing a list of Projects. This activity has different
 * presentations for handset and tablet-size devices. On handsets, the activity
 * presents a list of items, which when touched, lead to a
 * {@link ProjectDetailActivity} representing item details. On tablets, the
 * activity presents the list of items and item details side-by-side using two
 * vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link ProjectListFragment} and the item details (if present) is a
 * {@link ProjectDetailFragment}.
 * <p>
 * This activity also implements the required
 * {@link ProjectListFragment.Callbacks} interface to listen for item
 * selections.
 */
public class ProjectListActivity extends Activity implements
		ProjectListFragment.Callbacks {

	/**
	 * Whether or not the activity is in two-pane mode, i.e. running on a tablet
	 * device.
	 */
	private boolean mTwoPane;
	/**
	 * The list of projects
	 */
	private ArrayList<Project> projects;

	/**
	 * A selected Project
	 */
	private Project projectItem;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.projects = this.getIntent()
				.getParcelableArrayListExtra("Projects");

		boolean tabletSize = (getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
		if (!tabletSize) {
			setContentView(R.layout.activity_project_list);
		} else {
			setContentView(R.layout.activity_project_twopane);
		}
		if (findViewById(R.id.project_detail_container) != null) {
			this.mTwoPane = true;
			((ProjectListFragment) getFragmentManager().findFragmentById(
					R.id.project_list)).setActivateOnItemClick(true);
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
	 * 
	 * Get a list of Projects.
	 * 
	 * @return
	 */
	public ArrayList<Project> getProjects() {
		return projects;
	}

	/**
	 * Open the MilestoneList Activity for the selected milestone
	 * @param view
	 */
	public void openMilestoneView(View view) {
		Intent intent = new Intent(this, MilestoneListActivity.class);
		ArrayList<Milestone> milestones = this.projectItem.getMilestones();
		intent.putParcelableArrayListExtra("Milestones", milestones);
		this.startActivity(intent);
	}

	/**
	 * Callback method from {@link ProjectListFragment.Callbacks} indicating
	 * that the item with the given ID was selected.
	 */
	public void onItemSelected(Project p) {
		this.projectItem = p;
		if (this.mTwoPane) {
			// In two-pane mode, show the detail view in this activity by
			// adding or replacing the detail fragment using a
			// fragment transaction.
			Bundle arguments = new Bundle();
			// arguments.putString(ProjectDetailFragment.ARG_ITEM_ID, id);
			arguments.putParcelable("Project", p);
			ProjectDetailFragment fragment = new ProjectDetailFragment();
			fragment.setArguments(arguments);
			getFragmentManager().beginTransaction()
					.replace(R.id.project_detail_container, fragment).commit();

		} else {
			// In single-pane mode, simply start the detail activity
			// for the selected item ID.
			Intent detailIntent = new Intent(this, ProjectDetailActivity.class);
			// detailIntent.putExtra(ProjectDetailFragment.ARG_ITEM_ID, id);
			detailIntent.putExtra("Project", p);
			startActivity(detailIntent);
		}

	}
}
