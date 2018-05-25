package src.Algo;

public class Noeud{
	
    Noeud parent;
    int x, y;
    double coutG, coutH, coutF;
    
    public Noeud() {
        super();
        parent=null;
        coutG=0;
        coutH=0;
        coutF=0;
    }
    
    public Noeud(Noeud parent, double coutG, double coutH, double coutF, int x, int y) {
        super();
        this.parent = parent;
        this.coutG = coutG;
        this.coutH = coutH;
        this.coutF = coutF;
        this.x=x;
        this.y=y;
    }
    
    public int getX() {
        return x;
    }
    public void setX(int x) {
        this.x = x;
    }
    public int getY() {
        return y;
    }
    public void setY(int y) {
        this.y = y;
    }
    public Noeud getParent() {
        return parent;
    }
    public void setParent(Noeud parent) {
        this.parent = parent;
    }
    public double getCoutG() {
        return coutG;
    }
    public void setCoutG(double coutG) {
        this.coutG = coutG;
    }
    public double getCoutH() {
        return coutH;
    }
    public void setCoutH(double coutH) {
        this.coutH = coutH;
    }
    public double getCoutF() {
        return coutF;
    }
    public void setCoutF(double coutF) {
        this.coutF = coutF;
    }

}