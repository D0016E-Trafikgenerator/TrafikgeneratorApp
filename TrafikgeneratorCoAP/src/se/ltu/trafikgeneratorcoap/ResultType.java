package se.ltu.trafikgeneratorcoap;

public enum ResultType {
	SEND_DATA(0),
	RECEIVE_DATA(1),
	ANALYSE_DATA(2),
	LOAD_FILE(3);
	
	private final int index;   

    ResultType(int index) {
        this.index = index;
    }

    public int index() { 
        return index; 
    }
}
