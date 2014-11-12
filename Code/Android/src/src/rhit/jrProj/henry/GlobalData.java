package rhit.jrProj.henry;

import java.util.Stack;

import rhit.jrProj.henry.firebase.Milestone;
import rhit.jrProj.henry.firebase.Project;
import rhit.jrProj.henry.firebase.Task;
import rhit.jrProj.henry.firebase.User;
import android.app.Fragment;

public class GlobalData {
	
	
	private static GlobalData instance;
	
	/**
	 * Created user after login
	 */
	public User user;

	/**
	 * The project that has been selected from the list
	 */
	public Project selectedProject;

	/**
	 * The milestone selected by the user
	 */
	public Milestone selectedMilestone;

	/**
	 * The task that is currently selected by the user
	 */
	public Task selectedTask;

	/**
	 * Determines what page to fill in when the application starts
	 */
	public Stack<Fragment> fragmentStack;

	/**
	 * sorting mode
	 */
	public String sortingMode;

	/**
	 * current Fragment Used when the sorting mode is changed so that we can
	 * update the correct fragment's list.
	 */
	public Fragment currentFragment;
	
	private GlobalData(){
		
	}
	
	public static GlobalData getData(){
		if(null == instance){
			GlobalData.instance = new GlobalData();
		}
		return GlobalData.instance;
	}
	
	
}
