package rhit.jrProj.henry;

import java.util.ArrayList;
import java.util.List;

import org.achartengine.GraphicalView;

import rhit.jrProj.henry.firebase.Milestone;
import rhit.jrProj.henry.firebase.Project;
import rhit.jrProj.henry.helpers.GraphHelper;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * A fragment representing a single Project detail screen. This fragment is
 * either contained in a {@link ProjectListActivity} in two-pane mode (on
 * tablets) or a {@link ProjectDetailActivity} on handsets.
 */
public class ProjectDetailFragment extends Fragment {

	/**
	 * The dummy content this fragment is presenting.
	 */
	private Project projectItem;

	private Callbacks mCallbacks;

	public interface Callbacks {
		/**
		 * Callback for when an item has been selected.
		 */

		public Project getSelectedProject();

	}

	/**
	 * A dummy implementation of the {@link Callbacks} interface that does
	 * nothing. Used only when this fragment is not attached to an activity.
	 */
	private static Callbacks sDummyCallbacks = new Callbacks() {

		public Project getSelectedProject() {
			return null;
		}

	};

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public ProjectDetailFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setRetainInstance(true);
		this.projectItem = this.mCallbacks.getSelectedProject();

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_project_detail,
				container, false);
		if (this.projectItem != null) {
			((TextView) rootView.findViewById(R.id.project_name))
					.setText("Name of project: " + this.projectItem.getName());
			((TextView) rootView.findViewById(R.id.project_due_date))
					.setText("Due on: " + this.projectItem.getDueDate());
			((TextView) rootView.findViewById(R.id.project_description))
					.setText("Description: "
							+ this.projectItem.getDescription());

			// Progress Bars for Hours, Tasks, and Milestones
			((TextView) rootView.findViewById(R.id.project_hours_percent))
					.setText("Hours Completed: "
							+ this.projectItem.getHoursPercent() + "%");
			ProgressBar hoursCompleteBar = ((ProgressBar) rootView
					.findViewById(R.id.project_hours_progress_bar));
			hoursCompleteBar.setMax(100);
			hoursCompleteBar.setProgress(this.projectItem.getHoursPercent());
			((TextView) rootView.findViewById(R.id.project_tasks_percent))
					.setText("Tasks Completed: "
							+ this.projectItem.getTasksPercent() + "%");
			ProgressBar tasksCompleteBar = ((ProgressBar) rootView
					.findViewById(R.id.project_tasks_progress_bar));
			tasksCompleteBar.setMax(100);
			tasksCompleteBar.setProgress(this.projectItem.getTasksPercent());

			((TextView) rootView.findViewById(R.id.project_milestones_percent))
					.setText("Milestones Completed: "
							+ this.projectItem.getMilestonesPercent() + "%");
			ProgressBar milestonesCompleteBar = ((ProgressBar) rootView
					.findViewById(R.id.project_milestones_progress_bar));
			milestonesCompleteBar.setMax(100);
			milestonesCompleteBar.setProgress(this.projectItem
					.getMilestonesPercent());

			Spinner spinner = (Spinner) rootView
					.findViewById(R.id.project_chart_spinner);
			// Create an ArrayAdapter using the string array and a default
			// spinner layout
			ArrayAdapter<CharSequence> adapter = ArrayAdapter
					.createFromResource(this.getActivity(),
							R.array.milestone_charts,
							android.R.layout.simple_spinner_item);
			// Specify the layout to use when the list of choices appears
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			// Apply the adapter to the spinner
			spinner.setAdapter(adapter);

			// Set the default for the spinner
			spinner.setSelection(0);
			// /////

			FrameLayout chartView = (FrameLayout) rootView
					.findViewById(R.id.pieChart);
			List<Integer> values = new ArrayList<Integer>();
			List<String> keys = new ArrayList<String>();
			for (Milestone milestone : this.projectItem.getMilestones()) {
				GraphHelper.PieChartInfo chartInfo = milestone
						.getLocAddedInfo();
				values.addAll(chartInfo.getValues());
				keys.addAll(chartInfo.getKeys());
			}

			// used to remove duplicate names, people assigned to multiple
			// milestones
			for (int x = 0; x < keys.size(); x++) {
				for (int y = x + 1; y < keys.size(); y++) {
					if (keys.get(x).equals(keys.get(y))) {
						values.set(x, values.get(x) + values.get(y));
						keys.remove(y);
						values.remove(y);
						y--;
					}

				}

			}

			GraphicalView chart = GraphHelper.makePieChart("Lines Added for "
					+ this.projectItem.getName(), values, keys,
					this.getActivity());
			chartView.addView(chart, new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			chart.repaint();

		}

		return rootView;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// Activities containing this fragment must implement its callbacks.
		if (!(activity instanceof Callbacks)) {
			throw new IllegalStateException(
					"Activity must implement fragment's callbacks.");
		}

		mCallbacks = (Callbacks) activity;
	}

	@Override
	public void onDetach() {
		super.onDetach();

		// Reset the active callbacks interface to the dummy implementation.
		mCallbacks = sDummyCallbacks;
	}

}
