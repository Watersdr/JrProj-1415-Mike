package rhit.jrProj.henry.bridge;

import java.util.Comparator;

import android.util.Log;
import android.widget.SimpleAdapter;
import rhit.jrProj.henry.firebase.Project;
import rhit.jrProj.henry.firebase.Milestone;
import rhit.jrProj.henry.firebase.Task;

/**
 * 
 * The callback for a dynamic item in an android listview container designed for sorting of projects.
 *
 * @author daveyle. Created Nov 6, 2014.
 */
public class SortedListChangeNotifier<T> extends ListChangeNotifier<T> {
	
	/**
	 * A simple string that stores what sorting mode the user chose
	 */
	private String sortType="A-Z";
	/**
	 * Stores the comparator so it is not recreated every time.
	 */
	private Comparator<T> c;
	
	/**
	 * Standard constructor
	 * @param adapter
	 */
	public SortedListChangeNotifier(SortedArrayAdapter<T> adapter) {
		super(adapter);
		createComparator();
		
	}
	/**
	 * Standard constructor
	 * @param adapter
	 */
	public SortedListChangeNotifier(SortedArrayAdapter<T> adapter, String sorttype){
		super(adapter);
		if (sorttype!=null){
			this.sortType=sorttype;
		}
		createComparator();
		
	}
	
	
	/**
	 * This method is called when the user selects a new sorting mode.
	 * It is called by the ProjectListFragment
	 * @param sorttype
	 */
	public void changeSorting(String sorttype){
		if (!(sorttype.equals(this.sortType))){
			this.sortType=sorttype;
			createComparator();
			onChange();
		}
	}
	/**
	 * sets this.c to be specific comparators based on the type of sorting requested.
	 */
	private void createComparator(){
		if (this.sortType.equals("A-Z")){
			this.c=new Comparator<T>(){
				@Override
				public int compare(T lhs, T rhs) {
					if (lhs instanceof Project && rhs instanceof Project){
						return ((Project) lhs).compareToIgnoreCase((Project) rhs);
						}

					return 0;
					}
				};
		}
		else if (this.sortType.equals("Z-A")){
			this.c=new Comparator<T>(){
				@Override
				public int compare(T lhs, T rhs) {
					if (lhs instanceof Project && rhs instanceof Project){
						Log.i("SLCN", "PROJECT");
						return -1*((Project) lhs).compareToIgnoreCase((Project) rhs);
						}
					
					return 0;
					}
				};
		}
		else if (this.sortType.equals("Newest Due Date on Top")){
			this.c=new Comparator<T>(){
				@Override
				public int compare(T lhs, T rhs) {
					if (lhs instanceof Project && rhs instanceof Project){
						return ((Project) lhs).compareToByDate((Project) rhs, true);
						}
//					else if (lhs instanceof Milestone && rhs instanceof Milestone){
//						return ((Milestone) lhs).compareToByDate((Milestone) rhs, true);
//						}
//					else if (lhs instanceof Task && rhs instanceof Task){
//						//Should not happen
//						return ((Task) lhs).compareToIgnoreCase((Task) rhs);
//						}
					return 0;
					}
				};
		}
		else if (this.sortType.equals("Oldest Due Date on Top")){
			this.c=new Comparator<T>(){
				public int compare(T lhs, T rhs) {
					if (lhs instanceof Project && rhs instanceof Project){
						return ((Project) lhs).compareToByDate((Project) rhs, false);
						}
//					else if (lhs instanceof Milestone && rhs instanceof Milestone){
//						return ((Milestone) lhs).compareToByDate((Milestone) rhs, false);
//						}
//					else if (lhs instanceof Task && rhs instanceof Task){
//						//Should not happen
//						return ((Task) lhs).compareToIgnoreCase((Task) rhs);
//						}
					return 0;
					}
				};
		}
		else{
			//Not yet implemented
			this.c=new Comparator<T>(){
				@Override
				public int compare(T lhs, T rhs) {
					return 0;
					}
				};
		}
	}
	

	/**
	 * 
	 * The method that will be triggered when data is changed in the object. 
	 * This method sorts on every change.
	 *
	 */
	public void onChange() {
		Log.i("sortmode", this.sortType);
		
		((SortedArrayAdapter<T>) super.getAdapter()).sort(this.c);
		super.getAdapter().notifyDataSetChanged();
		
	}
}