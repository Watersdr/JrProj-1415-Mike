package rhit.jrProj.henry;

import rhit.jrProj.firebase.Milestone;
import rhit.jrProj.henry.dummy.DummyContent;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A fragment representing a single Milestone detail screen. This fragment is
 * either contained in a {@link MilestoneListActivity} in two-pane mode (on
 * tablets) or a {@link MilestoneDetailActivity} on handsets.
 */
public class MilestoneDetailFragment extends Fragment {
	/**
	 * The fragment argument representing the item ID that this fragment
	 * represents.
	 */
	public static final String ARG_ITEM_ID = "item_id";

	/**
	 * The dummy content this fragment is presenting.
	 */
	private Milestone milestoneItem;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public MilestoneDetailFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getArguments().containsKey(ARG_ITEM_ID)) {
			// Load the dummy content specified by the fragment
			// arguments. In a real-world scenario, use a Loader
			// to load content from a content provider.
			milestoneItem = DummyContent.ITEM_MAP.get(getArguments().getString(
					ARG_ITEM_ID));
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_milestone_detail,
				container, false);

		// Show the dummy content as text in a TextView.
		if (milestoneItem != null) {
			((TextView) rootView.findViewById(R.id.milestone_detail))
					.setText(milestoneItem.toString());
		}

		return rootView;
	}
}
