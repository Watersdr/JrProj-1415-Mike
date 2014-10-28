package rhit.jrProj.henry;

import java.util.ArrayList;

import rhit.jrProj.henry.firebase.Milestone;
import rhit.jrProj.henry.firebase.Project;
import rhit.jrProj.henry.firebase.Task;
import rhit.jrProj.henry.firebase.User;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.RecoverySystem.ProgressListener;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;

public class MainActivity extends Activity implements
		ProjectListFragment.Callbacks, MilestoneListFragment.Callbacks {
	/**
	 * TODO something here
	 */
	public final static String firebaseLoc = "https://henry-test.firebaseio.com/";
	/**
	 * The two pane determining boolean
	 */
	private boolean mTwoPane;
	/**
	 * The project that has been selected from the list
	 */
	private Project selectedProject;
	/**
	 * Created user after login
	 */
	private User user;
	/**
	 * The milestone selected by the user
	 */
	private Milestone selectedMilestone;

	/**
	 * Determines what page to fill in when the application starts
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Firebase.setAndroidContext(this);

		Firebase ref = new Firebase(firebaseLoc);

		AuthData authData = ref.getAuth();
		if (authData != null) {
			// Replaced with fragment TODO delete this
			// Intent intent = new Intent(this, ProjectListActivity.class);
			// intent.putExtra("user", firebaseLoc + "users/" +
			// authData.getUid());
			// this.startActivity(intent);
			this.user = new User(firebaseLoc + "users/" + authData.getUid());
			createProjectList();
		} else if (this.getIntent().getStringExtra("user") != null) {
			// If logged in get the user's project list
			this.user = new User(this.getIntent().getStringExtra("user"));
			createProjectList();
		} else {
			// Starts the LoginActivity if the user has not been logged in just
			// yet
			Intent intent = new Intent(this, LoginActivity.class);
			this.startActivity(intent);
			this.finish();
		}

	}

	/**
	 * Determines if this activity should operate in two pane mode and creates
	 * the fragment to display a list of projects.
	 */
	private void createProjectList() {
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

	/**
	 * TODO
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.project, menu);

		return true;
	}

	/**
	 * TODO
	 */
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
	 * @param view
	 */
	public void openTaskView(View view) {
		Intent intent = new Intent(this, TaskListActivity.class);
		ArrayList<Task> tasks = this.selectedMilestone.getTasks();
		intent.putParcelableArrayListExtra("Tasks", tasks);
		this.startActivity(intent);
	}

	/**
	 * Open the MilestoneList Activity for the selected milestone
	 * 
	 * @param view
	 */
	public void openMilestoneView(View view) {
		Intent intent = new Intent(this, MilestoneListActivity.class);
		ArrayList<Milestone> milestones = this.selectedProject.getMilestones();
		intent.putParcelableArrayListExtra("Milestones", milestones);
		this.startActivity(intent);
	}

	/**
	 * Callback method from {@link ProjectListFragment.Callbacks} indicating
	 * that the item with the given ID was selected.
	 */
	public void onItemSelected(Project p) {
		this.selectedProject = p;
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
			Bundle arguments = new Bundle();
			// arguments.putString(ProjectDetailFragment.ARG_ITEM_ID, id);
			arguments.putParcelable("Project", p);
			ProjectDetailFragment fragment = new ProjectDetailFragment();
			fragment.setArguments(arguments);
			getFragmentManager().beginTransaction()
					.replace(R.id.project_list, fragment).commit();
		}

	}

	/**
	 * Callback method from {@link MilestoneListFragment.Callbacks} indicating
	 * that the item with the given ID was selected.
	 */
	public void onItemSelected(Milestone m) {
		this.selectedMilestone = m;
		if (this.mTwoPane) {
			Bundle arguments = new Bundle();
			arguments.putParcelable("Milestone", m);
			MilestoneDetailFragment fragment = new MilestoneDetailFragment();
			fragment.setArguments(arguments);
			getFragmentManager().beginTransaction()
					.replace(R.id.milestone_detail_container, fragment)
					.commit();

		} else {
			Intent detailIntent = new Intent(this,
					MilestoneDetailActivity.class);
			detailIntent.putExtra("Milestone", m);
			startActivity(detailIntent);
		}
	}

	/**
	 * Logout the user.
	 */
	public void logOut(MenuItem item) {

		Intent login = new Intent(this, LoginActivity.class);
		this.startActivity(login);
		this.finish();

		Firebase ref = new Firebase(firebaseLoc);
		ref.unauth();
	}

	/**
	 * The method that is called when the "Login" button is pressed.
	 * 
	 * @param view
	 */
	public void openLoginDialog(View view) {
		Intent intent = new Intent(this, LoginActivity.class);
		this.startActivity(intent);

	}

	/**
	 * Returns the user's list of projects
	 * 
	 * @return
	 */
	public ArrayList<Project> getProjects() {
		return this.user.getProjects();
	}

	/**
	 * Returns the Milestones for the selected Project
	 * 
	 * @return
	 */
	public ArrayList<Milestone> getMilestones() {
		return this.selectedProject.getMilestones();
	}

	/**
	 * Returns the current user
	 * 
	 * @return
	 */
	public User getUser() {
		return this.user;
	}
}
