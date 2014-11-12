package rhit.jrProj.henry;

import java.util.ArrayList;
import java.util.Stack;

import rhit.jrProj.henry.firebase.Enums;
import rhit.jrProj.henry.firebase.Map;
import rhit.jrProj.henry.firebase.Member;
import rhit.jrProj.henry.firebase.Milestone;
import rhit.jrProj.henry.firebase.Project;
import rhit.jrProj.henry.firebase.Task;
import rhit.jrProj.henry.firebase.User;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.ListFragment;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;

public class MainActivity extends Activity implements
		ProjectListFragment.Callbacks, MilestoneListFragment.Callbacks,
		TaskListFragment.Callbacks, TaskDetailFragment.Callbacks,
		MilestoneDetailFragment.Callbacks, ProjectDetailFragment.Callbacks {
	/**
	 * The Url to the firebase repository
	 */
	public final static String firebaseUrl = "https://henry-test.firebaseio.com/";
	/**
	 * If the application is in tablet mode or not.
	 */
	private boolean mTwoPane;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Firebase.setAndroidContext(this);
		ActionBar actionBar = getActionBar();
		this.mTwoPane = (getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
		actionBar.setBackgroundDrawable(new ColorDrawable(0x268bd2));
		Firebase ref = new Firebase(firebaseUrl);
		if (GlobalData.getData().fragmentStack == null) {
			GlobalData.getData().fragmentStack = new Stack<Fragment>();
		}
		AuthData authData = ref.getAuth();
		if (GlobalData.getData().user != null && this.mTwoPane) {
			// Rotated!
			setContentView(R.layout.activity_twopane);
			if (ListFragment.class
					.isAssignableFrom(GlobalData.getData().currentFragment
							.getClass())) {
				// List View
				getFragmentManager()
						.beginTransaction()
						.add(R.id.twopane_list,
								GlobalData.getData().currentFragment).commit();
			} else {
				// Detail View
					getFragmentManager()
							.beginTransaction()
							.add(R.id.twopane_list,
									GlobalData.getData().fragmentStack.peek())
							.commit();
//					GlobalData.getData().currentFragment = GlobalData.getData().fragmentStack.peek();
//					getFragmentManager()
//							.beginTransaction()
//							.add(R.id.twopane_detail_container,
//									GlobalData.getData().currentFragment).commit();
			}
			getActionBar().setDisplayHomeAsUpEnabled(
					GlobalData.getData().fragmentStack.size() > 1);
		} else if (authData != null) {

			GlobalData.getData().user = new User(firebaseUrl + "users/"
					+ authData.getUid());
			createProjectList();

		} else if (this.getIntent().getStringExtra("user") != null) {
			// If logged in get the user's project list
			GlobalData.getData().user = new User(this.getIntent()
					.getStringExtra("user"));
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
		Bundle args = new Bundle();
		args.putBoolean("TwoPane", this.mTwoPane);
		ProjectListFragment fragment = new ProjectListFragment();
		GlobalData.getData().fragmentStack.push(fragment);
		getFragmentManager().beginTransaction().add(fragment, "Project_List")
				.addToBackStack("Project_List");
		fragment.setArguments(args);
		GlobalData.getData().currentFragment = fragment;
		if (!this.mTwoPane) {
			setContentView(R.layout.activity_onepane);
			getFragmentManager().beginTransaction()
					.add(R.id.main_fragment_container, fragment).commit();
			this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		} else {
			setContentView(R.layout.activity_twopane);
			getFragmentManager().beginTransaction()
					.add(R.id.twopane_list, fragment).commit();
			Fragment fragmentID = getFragmentManager().findFragmentById(
					R.id.twopane_detail_container);
			if (fragmentID != null) {
				getFragmentManager().beginTransaction().remove(fragmentID)
						.commit();
			}
		}
	}

	/**
	 * Creates the menu for the project
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.project, menu);

		// This code hides the "Create Milestone" and "Create Task" options when
		// viewing projects.
		MenuItem createMilestone = menu.findItem(R.id.action_milestone);
		createMilestone.setVisible(false);
		createMilestone.setEnabled(false);

		MenuItem createTask = menu.findItem(R.id.action_task);
		createTask.setVisible(false);
		createTask.setEnabled(false);

		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		SubMenu submenu = menu.findItem(R.id.action_sorting).getSubMenu();
		MenuItem dateOldest = submenu.findItem(R.id.sortOldest);
		MenuItem dateNewest = submenu.findItem(R.id.sortNewest);
		MenuItem AZ = submenu.findItem(R.id.sortAZ);
		MenuItem ZA = submenu.findItem(R.id.sortZA);

		if (GlobalData.getData().currentFragment instanceof ProjectListFragment) {
			dateOldest.setVisible(false);
			dateOldest.setEnabled(false);
			dateNewest.setVisible(false);
			dateNewest.setEnabled(false);
			AZ.setVisible(true);
			AZ.setEnabled(true);
			ZA.setVisible(true);
			ZA.setEnabled(true);

		} else {
			MenuItem sorting = menu.findItem(R.id.action_sorting);
			sorting.setVisible(false);
			sorting.setEnabled(false);
			dateOldest.setVisible(false);
			dateOldest.setEnabled(false);
			dateNewest.setVisible(false);
			dateNewest.setEnabled(false);
			AZ.setVisible(false);
			AZ.setEnabled(false);
			ZA.setVisible(false);
			ZA.setEnabled(false);

		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == android.R.id.home
				&& GlobalData.getData().fragmentStack.size() > 1) {
			GlobalData.getData().fragmentStack.pop();
			Fragment beforeFragment = GlobalData.getData().fragmentStack.peek();
			getActionBar().setDisplayHomeAsUpEnabled(
					GlobalData.getData().fragmentStack.size() > 1);
			if (this.mTwoPane) {
				getFragmentManager().beginTransaction()
						.replace(R.id.twopane_list, beforeFragment).commit();
				Fragment fragmentID = getFragmentManager().findFragmentById(
						R.id.twopane_detail_container);
				if (fragmentID != null) {
					getFragmentManager().beginTransaction().remove(fragmentID)
							.commit();
				}

			} else {
				getFragmentManager().beginTransaction()
						.replace(R.id.main_fragment_container, beforeFragment)
						.commit();
			}
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * 
	 * @param view
	 */
	public void openTaskView(View view) {
		int container = this.mTwoPane ? R.id.twopane_list
				: R.id.main_fragment_container;
		Bundle args = new Bundle();
		args.putBoolean("TwoPane", this.mTwoPane);
		TaskListFragment fragment = new TaskListFragment();
		GlobalData.getData().fragmentStack.push(fragment);
		fragment.setArguments(args);
		GlobalData.getData().currentFragment = fragment;
		getFragmentManager().beginTransaction().replace(container, fragment)
				.commit();
		if (this.mTwoPane) {
			getFragmentManager()
					.beginTransaction()
					.remove(getFragmentManager().findFragmentById(
							R.id.twopane_detail_container)).commit();
		}
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	/**
	 * Open the MilestoneList Activity for the selected milestone
	 * 
	 * @param view
	 */
	public void openMilestoneView(View view) {
		int container = this.mTwoPane ? R.id.twopane_list
				: R.id.main_fragment_container;
		Bundle args = new Bundle();
		args.putBoolean("TwoPane", this.mTwoPane);
		MilestoneListFragment fragment = new MilestoneListFragment();
		GlobalData.getData().fragmentStack.push(fragment);
		getFragmentManager().beginTransaction().add(fragment, "Milestone_List")
				.addToBackStack("Milestone_List");
		fragment.setArguments(args);
		getFragmentManager().beginTransaction().replace(container, fragment)
				.commit();
		if (this.mTwoPane) {
			Fragment f = getFragmentManager().findFragmentById(
					R.id.twopane_detail_container);
			getFragmentManager()
					.beginTransaction()
					.remove(f).commit();
		}
		GlobalData.getData().currentFragment = fragment;
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	/**
	 * Open the MilestoneList Activity for the selected milestone
	 * 
	 * @param view
	 */
	public void openProjectView(View view) {
		int container = this.mTwoPane ? R.id.twopane_list
				: R.id.main_fragment_container;
		Bundle args = new Bundle();
		args.putBoolean("TwoPane", this.mTwoPane);
		ProjectListFragment fragment = new ProjectListFragment();
		GlobalData.getData().fragmentStack.push(fragment);
		getFragmentManager().beginTransaction().add(fragment, "Project_View")
				.addToBackStack("Project_View");
		fragment.setArguments(args);
		GlobalData.getData().currentFragment = fragment;
		getFragmentManager().beginTransaction().replace(container, fragment)
				.commit();
		if (this.mTwoPane) {
			getFragmentManager()
					.beginTransaction()
					.remove(getFragmentManager().findFragmentById(
							R.id.twopane_detail_container)).commit();
		}
		getActionBar().setDisplayHomeAsUpEnabled(false);
	}

	/**
	 * Callback method from {@link ProjectListFragment.Callbacks} indicating
	 * that the item with the given ID was selected.
	 */
	public void onItemSelected(Project p) {
		GlobalData.getData().selectedProject = p;
		Bundle arguments = new Bundle();
		arguments.putParcelable("Project", p);
		Log.i("Project", new Boolean(p == null).toString());
		ProjectDetailFragment fragment = new ProjectDetailFragment();

		fragment.setArguments(arguments);
		GlobalData.getData().currentFragment = fragment;
		getFragmentManager()
				.beginTransaction()
				.replace(
						this.mTwoPane ? R.id.twopane_detail_container
								: R.id.main_fragment_container, fragment)
				.commit();
		// If in two pane mode, we cannot go up.

		if (!this.mTwoPane) {
			GlobalData.getData().fragmentStack.push(fragment);
		}
		getActionBar().setDisplayHomeAsUpEnabled(!this.mTwoPane);
	}

	/**
	 * Callback method from {@link MilestoneListFragment.Callbacks} indicating
	 * that the item with the given ID was selected.
	 */
	public void onItemSelected(Milestone m) {
		GlobalData.getData().selectedMilestone = m;
		Bundle arguments = new Bundle();
		arguments.putParcelable("Milestone", m);
		MilestoneDetailFragment fragment = new MilestoneDetailFragment();
		fragment.setArguments(arguments);
		GlobalData.getData().currentFragment = fragment;
		getFragmentManager()
				.beginTransaction()
				.replace(
						this.mTwoPane ? R.id.twopane_detail_container
								: R.id.main_fragment_container, fragment)
				.commit();
		if (!this.mTwoPane) {
			GlobalData.getData().fragmentStack.push(fragment);
		}
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	/**
	 * Callback method from {@link ItemListFragment.Callbacks} indicating that
	 * the item with the given ID was selected.
	 */
	public void onItemSelected(Task t) {
		GlobalData.getData().selectedTask = t;
		Bundle arguments = new Bundle();
		arguments.putBoolean("Two Pane", this.mTwoPane);
		TaskDetailFragment fragment = new TaskDetailFragment();
		fragment.setArguments(arguments);
		GlobalData.getData().currentFragment = fragment;
		getFragmentManager()
				.beginTransaction()
				.replace(
						this.mTwoPane ? R.id.twopane_detail_container
								: R.id.main_fragment_container, fragment)
				.commit();
		if (!this.mTwoPane) {
			GlobalData.getData().fragmentStack.push(fragment);
		}
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	/**
	 * Logout the user.
	 */
	public void logOut(MenuItem item) {

		Intent login = new Intent(this, LoginActivity.class);
		GlobalData.getData().currentFragment = null;
		GlobalData.getData().user = null;
		this.startActivity(login);
		this.finish();
		Firebase ref = new Firebase(firebaseUrl);
		ref.unauth();
	}

	@Override
	public Object onRetainNonConfigurationInstance() {
		return this;
	}

	/**
	 * Allows the project manager to create a new milestone.
	 */
	public void createMilestone(MenuItem item) {
		if (GlobalData.getData().selectedProject != null
				&& GlobalData.getData().selectedProject.getProjectId() != null) {

			CreateMilestoneFragment msFrag = new CreateMilestoneFragment();

			Bundle arguments = new Bundle();
			arguments.putString("projectid",
					GlobalData.getData().selectedProject.getProjectId());
			msFrag.setArguments(arguments);
			msFrag.show(getFragmentManager(), "Diag");
			// if (this.currFragment instanceof MilestoneListFragment){
			// ((MilestoneListFragment)this.currFragment).dataChanged();
			// }
		}

	}

	/**
	 * Allows the project manager to create a new task.
	 */
	public void createTask(MenuItem item) {

		if (GlobalData.getData().selectedMilestone != null
				&& GlobalData.getData().selectedMilestone.getMilestoneId() != null) {
			CreateTaskFragment taskFrag = new CreateTaskFragment();
			Bundle arguments = new Bundle();
			arguments.putString("milestoneId",
					GlobalData.getData().selectedMilestone.getMilestoneId());
			arguments.putString("projectId",
					GlobalData.getData().selectedProject.getProjectId());
			taskFrag.setArguments(arguments);
			GlobalData.getData().currentFragment = taskFrag;
			taskFrag.show(getFragmentManager(), "Diag");
		}
	}

	/**
	 * sets Sorting mode, and then calls the sortingChanged method on the
	 * current fragment
	 */
	public void sortingMode(MenuItem item) {
		GlobalData.getData().sortingMode = item.getTitle().toString();
		Log.i("SORTINGMODE", GlobalData.getData().sortingMode);
		if (GlobalData.getData().currentFragment != null) {
			if (GlobalData.getData().currentFragment instanceof ProjectListFragment) {
				((ProjectListFragment) GlobalData.getData().currentFragment)
						.sortingChanged();
			}
		}
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
		return GlobalData.getData().user.getProjects();
	}

	/**
	 * Returns the Milestones for the selected Project
	 * 
	 * @return
	 */
	public ArrayList<Milestone> getMilestones() {
		return GlobalData.getData().selectedProject.getMilestones();
	}

	/**
	 * Returns the Tasks for the selected Milestone
	 * 
	 * @return
	 */
	public ArrayList<Task> getTasks() {
		return GlobalData.getData().selectedMilestone.getTasks();
	}

	/**
	 * Returns the Tasks for the selected Milestone
	 * 
	 * @return
	 */
	public Map<Member, Enums.Role> getProjectMembers() {
		return GlobalData.getData().selectedProject.getMembers();
	}

	/**
	 * Returns the current user
	 * 
	 * @return
	 */
	public User getUser() {
		return GlobalData.getData().user;
	}

	public Project getSelectedProject() {
		return GlobalData.getData().selectedProject;
	}

	/**
	 * Returns the currently selected task.
	 */
	public Task getSelectedTask() {
		return GlobalData.getData().selectedTask;
	}

	/**
	 * Returns the currently selected milestone
	 */
	public Milestone getSelectedMilestone() {
		return GlobalData.getData().selectedMilestone;
	}

	/**
	 * Returns the current sorting mode
	 */
	public String getSortMode() {
		return GlobalData.getData().sortingMode;
	}
}
