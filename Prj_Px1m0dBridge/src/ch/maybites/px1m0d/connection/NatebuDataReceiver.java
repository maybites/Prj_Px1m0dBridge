package ch.maybites.px1m0d.connection;

public class NatebuDataReceiver{
	int[] bigStorage;
	int storagePosition;

	int[] PATTERN = {
			255, 127, 63, 31, 15, 7, 3, 1, 0};

	NatebuDataReceiver(){
		bigStorage = new int[256];
		storagePosition = 0;
	}

	int[] addData(int newbees){
		if(storagePosition < bigStorage.length){
			bigStorage[storagePosition] = newbees;
			storagePosition++;
		} 
		else {
			storagePosition = 0;
			bigStorage[storagePosition] = newbees;
		}
		if(findEndOfPackPattern()){
			int[] pack = getDataPackage();
			if(pack.length == 99){
				return pack;
			} 
		}
		return null;
	}


	int[] getDataPackage(){
		int[] newPackage = new int[storagePosition - PATTERN.length];
		for(int i = 0; i < storagePosition - PATTERN.length; i++){
			newPackage[i] = bigStorage[i];
		}
		storagePosition = 0;
		return newPackage;
	}


	boolean findEndOfPackPattern(){
		if((storagePosition - PATTERN.length) >= 0){
			for(int i = 0; i < PATTERN.length; i++){
				if(bigStorage[storagePosition - PATTERN.length + i] != PATTERN[i]){
					return false;
				}
			}
			return true;
		}
		return false;
	}

}
