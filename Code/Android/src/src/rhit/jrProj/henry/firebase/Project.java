package rhit.jrProj.henry.firebase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import rhit.jrProj.henry.bridge.ListChangeNotifier;
import rhit.jrProj.henry.firebase.User.GrandChildrenListener;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

public class Project implements Parcelable {

	/**
	 * A reference to Firebase to keep the data up to date.
	 */
	private Firebase firebase;

	/**
	 * A List of milestones that are contained within the project
	 */
	private ArrayList<Milestone> milestones = new ArrayList<Milestone>();

	/**
	 * The project's name
	 */
	private String name;

	/**
	 * The members that are working on the project
	 */
	private Map<User, Enums.Role> members = new HashMap<User, Enums.Role>();

	/**
	 * A description of the project.
	 */
	private String description;

	/**
	 * Do we need to do anything with the backlog?
	 */
	// private Backlog;

	/**
	 * This is the class that onChange is called from to when a field in
	 * Firebase is updated. This then notifies the object that is displaying the
	 * project that this object has been updated.
	 */
	private ListChangeNotifier<Project> listViewCallback;

	private ListChangeNotifier<Milestone> milestoneListViewCallback;
	/**
	 * A Creator object that allows this object to be created by a parcel
	 */
	public static final Parcelable.Creator<Project> CREATOR = new Parcelable.Creator<Project>() {

		public Project createFromParcel(Parcel pc) {
			return new Project(pc);
		}

		public Project[] newArray(int size) {
			return new Project[size];
		}
	};

	/**
	 * 
	 * This constructor builds a new project that updates its self from
	 * firebase.
	 * 
	 * @param firebaseUrl
	 *            i.e. https://henry371.firebaseio.com/projects/-
	 *            JYcg488tAYS5rJJT4Kh
	 */
	public Project(String firebaseUrl) {
		this.firebase = new Firebase(firebaseUrl);
		this.firebase.addChildEventListener(new ChildrenListener(this));
		this.firebase.child("milestones").addChildEventListener(
				new GrandChildrenListener(this));
	}

	/**
	 * 
	 * Ctor from Parcel, reads back fields IN THE ORDER they were written
	 * 
	 * @param in
	 */
	Project(Parcel in) {
		this.firebase = new Firebase(in.readString());
		this.firebase.addChildEventListener(new ChildrenListener(this));
		this.firebase.child("milestones").addChildEventListener(
				new GrandChildrenListener(this));
		this.name = in.readString();
		this.description = in.readString();
		// this.members = new HashMap<User, Role>(); // How to transport? Loop?
		in.readTypedList(this.milestones, Milestone.CREATOR);
	}

	/**
	 * Gets an ArrayList of milestones associated with this project.
	 */
	public ArrayList<Milestone> getMilestones() {
		return this.milestones;
	}

	/**
	 * 
	 * Sets what should be calledback to when the project's data is modified.
	 * 
	 * @param lcn
	 */
	public void setListChangeNotifier(ListChangeNotifier<Project> lcn) {
		this.listViewCallback = lcn;
	}

	@Override
	public String toString() {
		return this.name;
	}

	/**
	 * If both of the firebase URLs are the same, then they are referencing the
	 * same project.
	 */
	@Override
	public boolean equals(Object o) {
		if (o instanceof Project) {
			return ((Project) o).firebase.toString().equals(
					this.firebase.toString());
		}
		return false;
	}

	public int describeContents() {
		// Do nothing.
		return 0;
	}

	/**
	 * Passes the Firebase URL, the name of the project, the description of the
	 * project and the milestones
	 */
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.firebase.toString());
		dest.writeString(this.name);
		dest.writeString(this.description);
		dest.writeTypedList(this.milestones);
		// TODO Members?
		// number for the loop and then loop through it all?
	}

	/**
	 * Gets the description of the project
	 * 
	 * @return
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * 
	 * 
	 *
	 */
	class ChildrenListener implements ChildEventListener {
		Project project;

		public ChildrenListener(Project project) {
			this.project = project;
		}

		/**
		 * Nothing to do here
		 */
		public void onCancelled(FirebaseError arg0) {
			// TODO Auto-generated method stub.
			// nothing to do here
		}

		/**
		 * Method that is called when a project is added to the list
		 * 
		 */
		public void onChildAdded(DataSnapshot arg0, String arg1) {
			if (arg0.getName().equals("name")) {
				this.project.name = arg0.getValue(String.class);
				if (this.project.listViewCallback != null) {
					this.project.listViewCallback.onChange();
				}
			} else if (arg0.getName().equals("description")) {
				this.project.description = arg0.getValue(String.class);
			} else if (arg0.getName().equals("milestones")) {
				for (DataSnapshot child : arg0.getChildren()) {
					Milestone m = new Milestone(child.getRef().toString());
					if (!this.project.milestones.contains(m)) {
						this.project.milestones.add(m);
					}
				}
			}
		}

		/**
		 * Will be called when any project value is changed
		 */
		public void onChildChanged(DataSnapshot arg0, String arg1) {
			if (arg0.getName().equals("name")) {
				this.project.name = arg0.getValue(String.class);
				if (this.project.listViewCallback != null) {
					this.project.listViewCallback.onChange();
				}
			} else if (arg0.getName().equals("description")) {
				this.project.description = arg0.getValue(String.class);
			} else if (arg0.getName().equals("milestones")) {
				Log.i("Henry", "Milestone Changed!?!");
			}
		}

		/**
		 * Nothing to do here
		 */
		public void onChildMoved(DataSnapshot arg0, String arg1) {
			Log.i("Henry", "Something Moved!?!");
		}

		/**
		 * Until further notice from Mike: do nothing
		 */
		public void onChildRemoved(DataSnapshot arg0) {
			Log.i("Henry", arg0.getName());
		}
	}

	/**
	 * Listener for Tasks
	 * 
	 * @author johnsoaa
	 * 
	 */
	class GrandChildrenListener implements ChildEventListener {
		private Project project;

		public GrandChildrenListener(Project project) {
			this.project = project;
		}

		/**
		 * Do nothing
		 */
		public void onCancelled(FirebaseError arg0) {
			// nothing to do
		}

		/**
		 * Fills in the new milestone's properties including the milestone name,
		 * description and list of tasks for that milestone
		 */
		public void onChildAdded(DataSnapshot arg0, String arg1) {
			Milestone m = new Milestone(arg0.getRef().toString());
			this.project.getMilestones().add(m);
			m.setListChangeNotifier(milestoneListViewCallback);
			if (this.project.listViewCallback != null) {
				this.project.listViewCallback.onChange();
			}
		}

		/**
		 * This will be called when the milestone data in Firebased is updated
		 */
		public void onChildChanged(DataSnapshot arg0, String arg1) {
			// All changes done within Milestone

		}

		/**
		 * Might do something here for the tablet
		 */
		public void onChildMoved(DataSnapshot arg0, String arg1) {
			// Nada- yet
		}

		/**
		 * Removes a task from a milestone
		 */
		public void onChildRemoved(DataSnapshot arg0) {
			Milestone m = new Milestone(arg0.getRef().toString());
			this.project.getMilestones().remove(m);
			if (this.project.listViewCallback != null) {
				this.project.listViewCallback.onChange();
			}
		}
	}

}
