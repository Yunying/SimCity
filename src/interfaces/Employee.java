package interfaces;

public interface Employee{
	
	public abstract void msgStopWorkingGoHome();
	public abstract void msgHeresYourPaycheck(float paycheck);
	public abstract boolean hasReceivedPaycheck();
	public abstract void msgAtBuilding(Building building);

}
