package rhit.jrProj.henry.firebase;

import rhit.jrProj.henry.bridge.ListChangeNotifier;
import android.os.Parcel;
import android.os.Parcelable;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class Task implements Parcelable, ChildEventListener {

	/**
	 * A reference to firebase to keep the data up to date.
	 */
	private Firebase firebase;

	/**
	 * The task's name
	 */
	private String name;

	/**
	 * A description of the task
	 */
	private String description;

	/**
	 * A list of the user ids of the users assigned to the task
	 */
	private String assignedUserId;

	/**
	 * The name of the user assigned to this task
	 */
	private String assignedUserName = "default";

	/**
	 * The status of the task.
	 */
	private String status;

	/**
	 * The number of hours logged for this task
	 */
	private double hoursComplete;

	/**
	 * The total number of hours currently estimated for this task
	 */
	private double hoursEstimatedCurrent;

	/**
	 * The total number of hours originally estimated for this task
	 */
	private double hoursEstimatedOriginal;

	/**
	 * This is the class that onChange is called from to when a field in
	 * Firebase is updated. This then notifies the object that is displaying the
	 * task that this object has been updated.
	 */
	private ListChangeNotifier<Task> listViewCallback;

	/**
	 * The task's assignee(s)
	 */
	// private User assignedUser;

	/**
	 * the task's category
	 */
	// private Category category;

	/**
	 * A Creator object that allows this object to be created by a parcel
	 */
	public static final Parcelable.Creator<Task> CREATOR = new Parcelable.Creator<Task>() {

		public Task createFromParcel(Parcel pc) {
			return new Task(pc);
		}

		public Task[] newArray(int size) {
			return new Task[size];
		}
	};

	/**
	 * Creates a new task from a parcel
	 * 
	 * @param pc
	 */
	Task(Parcel pc) {
		this.firebase = new Firebase(pc.readString());
		this.name = pc.readString();
		this.description = pc.readString();
		this.assignedUserId = pc.readString();
		this.status = pc.readString();
	}

	public Task(String firebaseURL) {
		this.firebase = new Firebase(firebaseURL);
		this.firebase.addChildEventListener(this);
	}

	public int describeContents() {
		return 0;
	}

	/**
	 * 
	 * Sets a new list changed notifier
	 * 
	 * @param lcn
	 */
	public void setListChangeNotifier(ListChangeNotifier<Task> lcn) {
		this.listViewCallback = lcn;
	}

	/**
	 * Passes the Firebase URL, the name of the task and the description of the
	 * task to the next view
	 */
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.firebase.toString());
		dest.writeString(this.name);
		dest.writeString(this.description);
		dest.writeString(this.assignedUserId);
		dest.writeString(this.status);
	}

	@Override
	public String toString() {
		return this.name;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Task) {
			return ((Task) o).firebase.toString().equals(
					this.firebase.toString());
		}
		return false;
	};

	public void onCancelled(FirebaseError arg0) {
		// TODO Auto-generated method stub.

	}

	public void onChildAdded(DataSnapshot arg0, String arg1) {
		
		if (arg0.getName().equals("name")) {
			this.name = arg0.getValue().toString();
			if (this.listViewCallback != null) {
				this.listViewCallback.onChange();
			}
		} else if (arg0.getName().equals("description")) {
			this.description = arg0.getValue().toString();
		} else if (arg0.getName().equals("assignedTo")) {
			this.assignedUserName = "test";
			this.assignedUserId = arg0.getValue().toString();
			//this.firebase.getRoot().getPath().toString();
			// Set up a value event listener to get the assigned user's name
			/*this.firebase.getRoot().child("users").child("simplelogin:10")
					.addChildEventListener(new ChildEventListener() {
						// Retrieve new posts as they are added to Firebase
						@Override
						public void onChildAdded(DataSnapshot snapshot,
								String previousChildName) {
							if (snapshot.getName().equals("firstName")) {
								assignedUserName = snapshot.getValue().toString();
							}
						}

						@Override
						public void onCancelled(FirebaseError arg0) {
							//do nothing
						}

						@Override
						public void onChildChanged(DataSnapshot arg0,
								String arg1) {
							//do nothing
						}

						@Override
						public void onChildMoved(DataSnapshot arg0, String arg1) {
							//do nothing
						}

						@Override
						public void onChildRemoved(DataSnapshot arg0) {
							//do nothing
						}
					});*/

		} else if (arg0.getName().equals("status")) {
			this.status = arg0.getValue().toString();
		}
	}

	public void onChildChanged(DataSnapshot arg0, String arg1) {
		// TODO Auto-generated method stub.

	}

	public void onChildMoved(DataSnapshot arg0, String arg1) {
		// TODO Auto-generated method stub.

	}

	public void onChildRemoved(DataSnapshot arg0) {
		// TODO Auto-generated method stub.

	}

	/**
	 * 
	 * Returns the description of the task
	 * 
	 * @return the description of the task
	 */
	public String getDescription() {
		return this.description;
	}

	public String getName() {
		return this.name;
	}

	public String getAssignedUserId() {
		return this.assignedUserId;
	}

	/**
	 * Returns the name of the user assigned to this task
	 * 
	 * @return the name of the user assigned to this task
	 */
	public String getAssignedUserName() {
		return this.assignedUserName;
	}

	/**
	 * Returns the number of hours logged for this task
	 * 
	 * @return the number of hours logged for this task
	 */
	public double getHoursSpent() {
		return this.hoursComplete;
	}

	/**
	 * Returns the number of hours currently estimated for this task
	 * 
	 * @return the number of hours currently estimated for this task
	 */
	public double getCurrentHoursEstimate() {
		return this.hoursEstimatedCurrent;
	}

	/**
	 * Returns the status of this task
	 * 
	 * @return the status of this task
	 */
	public String getStatus() {
		return this.status;
	}

	/**
	 * Returns the number of hours originally estimated for this task
	 * 
	 * @return the number of hours originally estimated for this task
	 */
	public double getOriginalHoursEstimate() {
		return this.hoursEstimatedOriginal;
	}

	public void updateStatus(String taskStatus) {
		this.status = taskStatus;
		firebase.child("status").setValue(taskStatus);
		if (taskStatus.equals("Closed")) {
			firebase.child("is_completed").setValue(true);
		} else {
			firebase.child("is_completed").setValue(false);
		}
	}
}
