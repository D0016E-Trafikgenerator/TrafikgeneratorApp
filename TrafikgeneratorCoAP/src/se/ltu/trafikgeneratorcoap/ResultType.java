package se.ltu.trafikgeneratorcoap;

public enum ResultType {
	SEND_DATA(0),
	RECEIVE_DATA(1),
	ANALYSE_DATA(2),
	LOAD_FILE(3),
	SENDING_DATA(4),
	SEND_DATA_SENSOR(5),
	RECEIVE_DATA_SENSOR(6);
	
	private final int index;   

    ResultType(int index) {
        this.index = index;
    }

    public int index() { 
        return index; 
    }
}
