package rhit.jrProj.henry;


public class GlobalData {
	
	
	private static GlobalData instance;
	
	
	private GlobalData(){
		
	}
	
	public static GlobalData getData(){
		if(null == instance){
			GlobalData.instance = new GlobalData();
		}
		return GlobalData.instance;
	}
	
	
}
