package Gumtree.DataObj;

public class EditResObj {
    public String type;
    public int cnt;
    public double editDis;
    public double editDis2ForUp;

    public EditResObj(String type, int cnt, double editDis){
        this.type = type;
        this.cnt = cnt;
        this.editDis = editDis;
    }

    public EditResObj(String type, int cnt, double editDis, double editDis2){
        this.type = type;
        this.cnt = cnt;
        this.editDis = editDis;
        this.editDis2ForUp = editDis2;
    }
}
