package Equipe;

public class Lanceur {
    int x,y;
    public Lanceur(){

    }
    public String collect(){
        int caseContent=(int)Client.map[x][y];
        if(caseContent-'0'>=0&&caseContent-'0'<=3){
            return "P";
        }
        else{
            return getClosestFruit();
        }
    }
}
