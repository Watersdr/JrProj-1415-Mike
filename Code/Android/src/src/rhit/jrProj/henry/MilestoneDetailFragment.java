package rhit.jrProj.henry;

import org.achartengine.GraphicalView;

import rhit.jrProj.henry.firebase.Milestone;
import rhit.jrProj.henry.helpers.GraphHelper;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * A fragment representing a single Milestone detail screen. This fragment is
 * either contained in a {@link MilestoneListActivity} in two-pane mode (on
 * tablets) or a {@link MilestoneDetailActivity} on handsets.
 */
public class MilestoneDetailFragment extends Fragment implements
		OnItemSelectedListener {

	/**
	 * The dummy content this fragment is presenting.
	 */
	private Milestone milestoneItem;

	private Callbacks mCallbacks;

	public interface Callbacks {
		/**
		 * Callback for when an item has been selected.
		 */

		public Milestone getSelectedMilestone();

	}

	/**
	 * A dummy implementation of the {@link Callbacks} interface that does
	 * nothing. Used only when this fragment is not attached to an activity.
	 */
	private static Callbacks sDummyCallbacks = new Callbacks() {

		public Milestone getSelectedMilestone() {
			return null;
		}

	};

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public MilestoneDetailFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setRetainInstance(true);
		this.milestoneItem = this.mCallbacks.getSelectedMilestone();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_milestone_detail,
				container, false);
		if (this.milestoneItem != null) {
			((TextView) rootView.findViewById(R.id.milestone_name))
					.setText("Name of Milestone: "
							+ this.milestoneItem.getName());
			((TextView) rootView.findViewById(R.id.milestone_due_date))
					.setText("Due on: " + this.milestoneItem.getDueDate());
			((TextView) rootView.findViewById(R.id.milestone_description))
					.setText("Description: "
							+ this.milestoneItem.getDescription());

			((TextView) rootView.findViewById(R.id.milestone_task_percent))
					.setText("Tasks Completed: "
							+ this.milestoneItem.getTaskPercent() + "%");
			ProgressBar taskCompleteBar = ((ProgressBar) rootView
					.findViewById(R.id.milestone_task_progress_bar));
			taskCompleteBar.setMax(100);
			taskCompleteBar.setProgress(this.milestoneItem.getTaskPercent());

			// //////
			// Task status spinner
			Spinner spinner = (Spinner) rootView
					.findViewById(R.id.milestone_chart_spinner);
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

			spinner.setOnItemSelectedListener(this);
			// /////

			/*
			 * FrameLayout chartView = (FrameLayout) rootView
			 * .findViewById(R.id.pieChart); GraphHelper.PieChartInfo chartInfo
			 * = this.milestoneItem.getLocAddedInfo(); GraphicalView pieChart =
			 * GraphHelper.makePieChart( "Lines Added for " +
			 * this.milestoneItem.getName(), chartInfo.getValues(),
			 * chartInfo.getKeys(), this.getActivity());
			 * 
			 * 
			 * GraphHelper.StackedBarChartInfo chartInfo = this.milestoneItem
			 * .getLocTotalInfo(); GraphicalView pieChart =
			 * GraphHelper.makeStackedBarChart( "Lines Total Added",
			 * "Developer", "Lines of Code", chartInfo.getValues(),
			 * chartInfo.getBarLabels(), chartInfo.getKeys(),
			 * this.getActivity()); chartView.addView(pieChart, new
			 * LayoutParams( LayoutParams.MATCH_PARENT,
			 * LayoutParams.MATCH_PARENT)); pieChart.repaint();
			 */
		}

		return rootView;
	}

	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		FrameLayout chartView = (FrameLayout) this.getActivity().findViewById(
				R.id.pieChart);
		chartView.removeAllViews();
		GraphicalView chart;
		if (position == 0) {
			GraphHelper.PieChartInfo chartInfo = this.milestoneItem
					.getLocAddedInfo();

			chart = GraphHelper.makePieChart("Lines Added for "
					+ this.milestoneItem.getName(), chartInfo.getValues(),
					chartInfo.getKeys(), this.getActivity());
			chartView.addView(chart, new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

		} else {
			GraphHelper.StackedBarChartInfo chartInfo = this.milestoneItem
					.getLocTotalInfo();

			chart = GraphHelper.makeStackedBarChart("Lines Total Added",
					"Developer", "Lines of Code", chartInfo.getValues(),
					chartInfo.getBarLabels(), chartInfo.getKeys(),
					this.getActivity());
			chartView.addView(chart, new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			chart.repaint();
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// Activities containing this fragment must implement its callbacks.
		if (!(activity instanceof Callbacks)) {
			throw new IllegalStateException(
					"Activity must implement fragment's callbacks.");
		}

		this.mCallbacks = (Callbacks) activity;
	}

	@Override
	public void onDetach() {
		super.onDetach();

		// Reset the active callbacks interface to the dummy implementation.
		this.mCallbacks = sDummyCallbacks;
	}
	
	public void onNothingSelected(AdapterView<?> parent) {
		// do nothing
	}
}
